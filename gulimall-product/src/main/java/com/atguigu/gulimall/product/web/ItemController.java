package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

/**
 * <p>Title: ItemController</p>
 * Description：
 * date：2020/6/24 13:20
 */
@Controller
public class ItemController {

	@Autowired
	private SkuInfoService skuInfoService;

	/**
	 * @Author chenfl
	 * @Description //展示当前sku的详情
	 * @Date 16:55 2022/3/7
	 * @Param [skuId, model]
	 * @return java.lang.String
	 **/
	@RequestMapping("/{skuId}.html")
	public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

		SkuItemVo vo = skuInfoService.item(skuId);

		model.addAttribute("item", vo);
		return "item";
	}
}
