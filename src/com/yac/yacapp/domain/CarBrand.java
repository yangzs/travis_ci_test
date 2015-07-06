package com.yac.yacapp.domain;

import java.io.Serializable;

public class CarBrand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
 * {
"brand_type": 46,
"img_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1052.jpg/s1024.jpg",
"brand_name": "本田",
"logo_attachment_id": 1052,
"brand_pinyin": "bentian",
"first_letter": "b"
}
 */
	public static final String KEY_BRAND_TYPE = "brand_type";
	public static final String KEY_IMG_URL = "img_url";
	public static final String KEY_BRAND_NAME = "brand_name";
	public static final String KEY_LOGO_ATTACHMENT_ID = "logo_attachment_id";
	public static final String KEY_BRAND_PINYIN = "brand_pinyin";
	public static final String KEY_FIRST_LETTER = "first_letter";
	
	public Integer brand_type;
	public String img_url;
	public String brand_name;
	public Long logo_attachment_id;
	public String brand_pinyin;
	public String first_letter;
	
}
