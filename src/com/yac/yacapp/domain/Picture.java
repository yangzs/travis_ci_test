package com.yac.yacapp.domain;

import java.io.Serializable;

public class Picture implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * "brand_img_url": {
"id": 1041,
"thumbnail_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1041.jpg/s250.jpg",
"original_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1041.jpg/s1024.jpg",
"raw_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1041.jpg"
},
	 */
	public static final String KEY_ID = "id"; 
	public static final String KEY_THUMBNAIL_URL = "thumbnail_url";
	public static final String KEY_ORIGINAL_URL = "original_url";
	public static final String KEY_RAW_URL = "raw_url";
	
	public Long id;
	public String thumbnail_url;
	public String original_url;
	public String raw_url;
	
}
