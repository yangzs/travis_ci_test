package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * {
"recommended": true,
"price": 82,
"unit": "升",
"product_type": 4,
"product_name": "银美孚",
"labour_price": 0,
"unit_count": 4,
"user_defined": false,
"product_categories": [
"其他",
"机油"
]
}
	 */
	public static final String KEY_RECOMMENDED = "recommended";
	public static final String KEY_PRICE = "price";
	public static final String KEY_UNIT = "unit";
	public static final String KEY_PRODUCT_TYPE = "product_type";
	public static final String KEY_PRODUCT_NAME = "product_name";
	public static final String KEY_LABOUR_PRICE = "labour_price";
	public static final String KEY_UNIT_COUNT = "unit_count";
	public static final String KEY_USER_DEFINED = "user_defined";
	public static final String KEY_PRODUCT_CATEGORIES = "product_categories";
	
	public Boolean recommended;
	public Double price;
	public String unit;
	public Integer product_type;
	public String product_name;
	public Double labour_price;
	public Integer unit_count;
	public Boolean user_defined;
	public List<String> product_categories;
	public Boolean isChecked = false;
}
