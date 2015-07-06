package com.yac.yacapp.domain;

import java.io.Serializable;

public class OrderKeeperBasic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String KEY_ID = "id";
	public static final String KEY_TYPE = "type";
	public static final String KEY_NAME = "name";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_STAR_COUNT = "star_count";
	public static final String KEY_RATING = "rating";
	public static final String KEY_ID_NUMBER = "ID_number";
	public static final String KEY_PHONE_NUMBER = "phone_number";
	public static final String KEY_CAR_EXP_YEAR = "car_exp_year";
	public static final String KEY_AVATAR_IMG = "avatar_img";
	public static final String KEY_CURRENT = "current";
	
	
	public Long id;
	public String type;
	public String name;
	public String gender;
	public Integer star_count;
	public Double rating;
	public String ID_number;
	public String phone_number;
	public Integer car_exp_year;
	public String avatar_img;
	public Boolean current;
/*
 * {
"id": 999,
"type": "keeper",
"name": "默认管家",
"gender": "male",
"star_count": 4,
"rating": 4,
"ID_number": "",
"phone_number": "18210237734",
"car_exp_year": 1,
"avatar_img": "",
"current": true
}
 */

}
