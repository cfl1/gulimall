package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog3Vo;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * @CacheEvict 失效模式
     * @Caching 同时进行多种缓存操作
     * @CacheEvict(value="category",allEntries = true)   指定删除某个分区下的所有缓存
     * 存储同一类型的数据，都可以指定成同一个分区，分区名默认就是存储的前缀（配置文件不指定的话）
     * @param category
     */
    // @Caching(evict = { @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
    //         @CacheEvict(value = "category",key = "'getCatelogJson'")})
    @CacheEvict(value="category",allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //每一个需要缓存的数据我们都来指定要放到哪个名字的缓存【缓存的分区（按照业务类型分）】
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)   //代表当前方法的结果需要缓存，如果缓存中有，方法不用调用，如果缓存中没有就会调用方法，最后将方法的结果放入缓存
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
        // 测试能否缓存null值
//		return null;
    }

    //TODO 产生堆外内存溢出：OutOfDirectMemoryError
    //1).sprignboot2.0以后默认是使用lettuce作为操作redis的客户端，它使用netty进行网络通信
    //2).lettuce的bug导致netty堆外内存溢出，netty如果没有指定堆外内存，默认使用-xmx默认的内存，没有及时释放内存
    //  可以通过-Dio.netty.maxDirectMemory进行设置
    //解决方案：不能通过-Dio.netty.maxDirectMemory只去调大内存
    //  1)升级lettuce客户端
    //  2)切换使用jedis
    //缓存中拿数据
    // @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        //1.加入缓存逻辑,缓存中存的数据是json字符串,还用逆转为能用的对象类型【序列号与反序列化】
        //json跨语言，跨平台兼容
        /**
         * 1.空结果缓存，解决缓存穿透
         * 2.设置过期时间，加随机值，解决缓存雪崩
         * 3.加锁，解决缓存击穿
         **/
        String catelogJson = redisTemplate.opsForValue().get("catelogJson");
        if (StringUtils.isEmpty(catelogJson)) {
            //2.缓存中没有，查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();
            //3.查到的数据再放入缓存   这个操作再getCatelogJsonFromDb()完成
            return catelogJsonFromDb;
        }
        //转为我们指定的对象
        Map<String, List<Catelog2Vo>> parseObject = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return parseObject;
    }

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 拿到每一个一级分类 然后查询他们的二级分类
            List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类
                    List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                    // 三级分类有数据的情况下
                    if (level3 != null) {
                        List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parent_cid;
    }

    /**
     * 第一次查询的所有 CategoryEntity 然后根据 parent_cid去这里找
     */
    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> entityList, Long parent_cid) {
        return entityList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
    }

    //加入缓存前从数据库查询并封装分类数据,使用本地锁
    // @Override
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithLocalLock() {

        //只要是同一把锁，就能锁住需要这个锁的所有线程
        //1).synchronized (this),SpringBoot所有的组件在容器中都是单例的
        //TODO 本地锁，synchronized (this)，JUC（lock），在分布式情况下，想要锁住所有，我们必须使用分布式锁
        synchronized (this) {

            //得到锁以后，再去缓存中确定一次，如果有了就不用查数据库了
            String catelogJson = redisTemplate.opsForValue().get("catelogJson");
            if (!StringUtils.isEmpty(catelogJson)) {
                Map<String, List<Catelog2Vo>> parseObject = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return parseObject;
            }

            List<CategoryEntity> entityList = baseMapper.selectList(null);
            // 查询所有一级分类
            List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
            Map<String, List<Catelog2Vo>> parent_cid = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                // 拿到每一个一级分类 然后查询他们的二级分类
                List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (entities != null) {
                    catelog2Vos = entities.stream().map(l2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                        // 找当前二级分类的三级分类
                        List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                        // 三级分类有数据的情况下
                        if (level3 != null) {
                            List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(catalog3Vos);
                        }
                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            String string = JSON.toJSONString(parent_cid);
            redisTemplate.opsForValue().set("catelogJson", string, 1, TimeUnit.DAYS);
            return parent_cid;
        }
    }

    //加入缓存前从数据库查询并封装分类数据,使用redis占坑实现分布式锁
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        //1.占分布式锁，  去redis占坑
        //2.设置过期时间（必须和加锁是同步的，院子的）,避免删除锁的时候宕机造成死锁
        String uuid = UUID.randomUUID().toString();//使用随机避免删了别人的锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            // 加锁成功...执行业务
            Map<String, List<Catelog2Vo>> fromDb;
            try {
                fromDb = getFromDb();
            } finally {
                //获取值对比和对比成功删除要是原子操作
                // String lock1 = redisTemplate.opsForValue().get("lock");
         /* ①原方式  if (uuid.equals(lock1)) {
                //删除我自己的锁
                redisTemplate.delete("lock");//删除锁
            }*/

                //lua 脚本解锁
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
                // ②改进方式，删除锁
                Long lock2 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return fromDb;
        } else {
            //加锁失败。。。重试 synchronize（）
            //休眠100ms重试
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDbWithRedisLock();//自旋的方式
        }
    }

    /**
     * @Author chenfl
     * @Description //缓存中的数据如果和数据库保持一致
     * 缓存一致性问题
     * 1）双写模式   数据库更改的时候同时修改缓存
     * 2）失效模式   数据库更改的时候同时删除缓存
     * @Date 10:18 2022/3/1
     * @Param []
     * @return java.util.Map<java.lang.String,java.util.List<com.atguigu.gulimall.product.vo.Catelog2Vo>>
     **/
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedissonLock() {
        //1.锁的名字是同一个就是同一把锁（锁的名字牵扯到锁的粒度）
        //锁的粒度，具体缓存的是某个数据，11号商铺，product-11-lock，
        RLock catelogJsonLock = redisson.getLock("CatelogJson-lock");

        catelogJsonLock.lock();

        Map<String, List<Catelog2Vo>> fromDb;
        try {
            fromDb = getFromDb();
        } finally {
            catelogJsonLock.unlock();
        }
        return fromDb;
    }

    private Map<String, List<Catelog2Vo>> getFromDb() {
        String catelogJson = redisTemplate.opsForValue().get("catelogJson");
        if (!StringUtils.isEmpty(catelogJson)) {
            Map<String, List<Catelog2Vo>> parseObject = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return parseObject;
        }

        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 拿到每一个一级分类 然后查询他们的二级分类
            List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类
                    List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                    // 三级分类有数据的情况下
                    if (level3 != null) {
                        List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        String string = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catelogJson", string, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;

    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}