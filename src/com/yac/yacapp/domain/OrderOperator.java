package com.yac.yacapp.domain;

import java.io.Serializable;

public class OrderOperator implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String KEY_ID = "id";
	public static final String KEY_AVATAR_IMG = "avatar_img";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHONE_NUMBER = "phone_number";
	public static final String KEY_TYPE = "type";
	
	
	public Long id;
	public String avatar_img;
	public String gender;
	public String name;
	public String phone_number;
	public String type;
/*
 * 	"operator":
    {
        "avatar_img":"","gender":"male","id":33,"name":"付恒","phone_number":"13366240850","type":"operator"
    }
 */
}
