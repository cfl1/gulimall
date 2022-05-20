package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>Title: Catalog3Vo</p>
 * Description：
 * date：2020/6/9 14:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog3Vo implements Serializable {

	private static final long serialVersionUID = 454L;

	private String id;

	private String name;
	//父分类，二级分类id
	private String catalog2Id;
}
