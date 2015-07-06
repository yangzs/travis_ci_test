package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class ProductPart implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
 * "part_type": 9,
"part_name": "机油",
"products": [ … ]
 */
	public static final String KEY_PART_TYPE = "part_type";
	public static final String KEY_PART_NAME = "part_name";
	public static final String KEY_PRODUCTS = "products";
	
	public Integer part_type;
	public String part_name;
	public List<Product> products;
	
	
}
