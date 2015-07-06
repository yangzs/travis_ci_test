package com.yac.yacapp.domain;

import java.io.Serializable;

public class UserBasic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * {
"id": 13,
"name": "纪波",
"gender": "male",
"phone_number": "13141267740",
"avatar_img": {}
},

"basic": {
            "id": 1135,
            "name": "",
            "gender": "male",
            "phone_number": "13141267740",
            "avatar_img": {
                "id": null,
                "thumbnail_url": "",
                "original_url": "",
                "raw_url": null
            }
        }
	 */
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_PHONE_NUMBER = "phone_number";
	public static final String KEY_AVATAR_IMG = "avatar_img";
	
	public Long id;
	public String name;
	public String gender;
	public String phone_number;
	public Picture avatar_img;
	public String sing_in_origin;//for update
}
