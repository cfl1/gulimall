package com.atguigu.gulimall.product.web;/**
 * @author chenfl
 * @create 2021-12-09-20:32
 */

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chenfl
 * @description
 * @date 2021/12/9 20:32
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        //1.查出所有的一级分类
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categorys);
        //视图解析器进行拼串
        // classpath:/templates/ + 返回值 + .html
        return "index";
    }

    // index/catalog.json
    @ResponseBody
    @RequestMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatlogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1. 获取一把锁,只要锁的名字是一样,就是同一把锁
        RLock mylock = redisson.getLock("my-lock");
        // 2. 加锁,阻塞式等待
        /**
         * 方式一加锁
         * 1).锁的自动续期，如果业务超长，运行期间自动给锁续上30s,不用担心业务时间长，锁自动过期被删除
         * 2).加锁的业务只要运行完成，就不会给当前的锁续期，及时不手动解锁
         **/
        // mylock.lock();

        /**
         * 方式二加锁
         * 10s自动解锁后，自动解锁时间一定要大于业务的执行时间。
         * 问题：在锁时间到了以后，不会自动续期
         * 1.如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
         * 2.如果我们未指定锁的超时时间，就使用30*1000【lockWatchdogTimeout看门狗的默认时间】
         * 只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】 每隔10s都会再次续期，续成满时间如当前是30s
         * internalLockLeaseTime【看门狗时间】/3......10s
         * 、、
         * //最佳实现
         * 1）mylock.lock(10, TimeUnit.SECONDS);省掉了整个续期的操作，手动解锁
         **/
        mylock.lock(10, TimeUnit.SECONDS);

        try {
            System.out.println("加锁成功，执行业务。。。" + Thread.currentThread().getId());
            //休眠方便测试
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 3. 解锁,假设解锁代码没有运行，Redisson 会出现死锁吗？（不会）
            System.out.println("释放锁。。。" + Thread.currentThread().getId());
            mylock.unlock();
        }
        return "hello";
    }


    //保证一定能读到最新的数据。修改期间，写锁是一个排它锁（互斥锁，独享锁），读锁是一个共享锁
    //读+读   相当于无所，并发读，只会在redis中记录好，所有当前的读锁，他们都会同事加锁成功
    //写+读   写锁没释放，读就必须等待
    //写+写   阻塞方式
    //读+写   有读锁，写也需要等待
    //只要有写的存在，都必须等待
    @ResponseBody
    @GetMapping("/write")
    public String write() {
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = readWriteLock.writeLock();
        try {
            System.out.println("写锁加锁成功" + Thread.currentThread().getId());
            //1.改数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("writeValue", s);
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("写锁释放" + Thread.currentThread().getId());
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String read() {
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        String writeValue = null;
        //加读锁
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            System.out.println("读锁加锁成功" + Thread.currentThread().getId());
            writeValue = redisTemplate.opsForValue().get("writeValue");
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放" + Thread.currentThread().getId());
        }
        return writeValue;
    }

    /**
     * @Author chenfl
     * @Description //放假，锁门
     * 1班没人了。。。2班。。。5个班全部走完，我们可以锁大门
     * @Date 9:49 2022/3/1
     * @Param []
     * @return java.lang.String
     **/
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();//等待闭锁都完成
        return "放假了。。。";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") long id) {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();//计数减一
        return id + "班的人都走了";
    }

    /**
     * @return
     * @Author chenfl
     * @Description //车库停车
     * 3个车位
     * 信号量也可以用作分布式限流
     * @Date 9:58 2022/3/1
     * @Param
     **/
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        // park.acquire();//获取一个信号，获取一个值,占一个车位
        boolean b = park.tryAcquire();//尝试获取
        if (b) {
            //执行业务
        } else {
            return "当前流量过大，请稍等";
        }
        return "ok=>" + b;
    }

    @ResponseBody
    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个信号
        return "ok";
    }
}
