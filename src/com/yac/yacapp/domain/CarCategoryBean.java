package com.yac.yacapp.domain;

import java.io.Serializable;

public class CarCategoryBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String KEY_CATEGORY_TYPE = "category_type";
	public static final String KEY_CATEGORY_NAME = "category_name";
	public static final String KEY_BRAND_NAME = "brand_name";
	
	public Integer category_type;
	public String brand_name;
	public String category_name;
	public CarCategoryBean(Integer category_type, String brand_name, String category_name) {
		super();
		this.category_type = category_type;
		this.brand_name = brand_name;
		this.category_name = category_name;
	}
	public CarCategoryBean() {
		super();
	}
	@Override
	public String toString() {
		return "CarCategoryBean [category_type=" + category_type + ", brand_name=" + brand_name + ", category_name=" + category_name + "]";
	}
	
	
	
}
