package com.yac.yacapp.domain;

import java.io.Serializable;

public class Coupon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
 * {
            "id": 1, 
            "status": "已过期", 
            "name": "啊", 
            "number": "123", 
            "value": 20, 
            "expired_time": "2015-03-31T14:45:29.000"
        }
 */
	public static final String KEY_ID = "id";
	public static final String KEY_STATUS = "status";
	public static final String KEY_NAME = "name";
	public static final String KEY_NUMBER = "number";
	public static final String KEY_VALUE = "value";
	public static final String KEY_EXPIRED_TIME = "expired_time";
	
	public Long id;
	public String status;
	public String name;
	public String number;
	public Double value;
	public String expired_time;
}
