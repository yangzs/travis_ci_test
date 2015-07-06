package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class Car implements Serializable{

	/*
	 * {
"id": 3,
"disabled": false,
"licence": { … },
"brand": "阿尔法·罗密欧",
"category": "Giulietta",
"model": "2014款 T 基本型 2014款",
"bought_time": "2015-02-01T18:02:52.000",
"miles": 0,
"chassis_number": "哦哦哦",
"engine_number": "",
"brand_img_url": { … },
"model_type": 465,
"user_id": 13,
"carcare_book": [ ],
"whole_score": 70,
"parts": [ … ]
},
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String 	KEY_ID = "id";
	public static final String 	KEY_DISABLED = "disabled";
	public static final String 	KEY_LICENCE = "licence";
	public static final String 	KEY_BRAND = "brand";
	public static final String 	KEY_CATEGORY = "category";
	public static final String 	KEY_MODEL = "model";
	public static final String 	KEY_BOUGHT_TIME = "bought_time";
	public static final String 	KEY_MILES = "miles";
	public static final String 	KEY_CHASSIS_NUMBER = "chassis_number";
	public static final String 	KEY_ENGINE_NUMBER = "engine_number";
	public static final String 	KEY_BRAND_IMG_URL = "brand_img_url";
	public static final String 	KEY_MODEL_TYPE = "model_type";
	public static final String 	KEY_USER_ID = "user_id";
	public static final String 	KEY_CARCARE_BOOK = "carcare_book";
	public static final String 	KEY_WHOLE_SCORE = "whole_score";
	public static final String 	KEY_PARTS = "parts";
	
	
	public Long id;
	public Boolean disabled;
	public Licence licence;
	public String brand;
	public String category;
	public String model;
	public String bought_time;
	public Integer miles;
	public String chassis_number;
	public String engine_number;
	public Picture brand_img_url;
	public Integer model_type;
	public Long user_id;
	public List<CarCareBook> carcare_book;
	public Integer whole_score;
	public List<Part> parts;
}
