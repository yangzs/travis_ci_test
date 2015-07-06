package com.yac.yacapp.domain;

import java.io.Serializable;

public class OrderClientBasic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String KEY_ID = "id";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_PHONE_NUMBER = "phone_number";
	public static final String KEY_NAME = "name";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_AVATAR_IMG = "avatar_img";
	
	public Long id;
	public Location location;
	public String phone_number;
	public String name;
	public String gender;
	public String avatar_img;
/*
 * {
"id": 13,
"location": {
"longitude": 21.1,
"latitude": 21.1,
"address": "",
"name": ""
},
"phone_number": "13141267740",
"name": "纪波测试下单",
"gender": null,
"avatar_img": ""
},
 */

}
