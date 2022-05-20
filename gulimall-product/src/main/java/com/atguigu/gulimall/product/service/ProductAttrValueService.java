package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author chenfl
 * @email 1571539116@qq.com
 * @date 2019-10-01 21:08:49
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttr(List<ProductAttrValueEntity> collect);


    List<ProductAttrValueEntity> baseAttrlistforspu(Long spuId);


    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
    /**
     * @Author chenfl
     * @Description //查询规格属性
     * @Date 20:21 2021/12/7
     * @Param [spuId]
     * @return java.util.List<com.atguigu.gulimall.product.entity.ProductAttrValueEntity>
     **/
    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);
}

