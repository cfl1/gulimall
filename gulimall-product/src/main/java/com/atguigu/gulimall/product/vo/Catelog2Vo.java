package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: Catelog2Vo</p>
 * Description：
 * date：2020/6/9 14:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo implements Serializable {

	private static final long serialVersionUID = 87L;

	private String id;

	private String name;
	//一级父类id
	private String catalog1Id;
	//三级子分类
	private List<Catalog3Vo> catalog3List;
}
