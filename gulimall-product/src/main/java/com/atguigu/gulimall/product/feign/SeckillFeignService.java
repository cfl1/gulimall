package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.fallback.SecKillFeignServiceFalback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>Title: SeckillFeignService</p>
 * Description：
 * date：2020/7/9 12:28
 */
@FeignClient(value = "gulimall-seckill",fallback = SecKillFeignServiceFalback.class)
public interface SeckillFeignService {

	@GetMapping("/sku/seckill/{skuId}")
	R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
