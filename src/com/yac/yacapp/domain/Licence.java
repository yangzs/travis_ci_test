package com.yac.yacapp.domain;

import java.io.Serializable;

public class Licence implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
 * "licence": {
"province": "京",
"number": ""
},
 */
	public static final String KEY_PROVINCE = "province";
	public static final String KEY_NUMBER = "number";
	
	public String province;
	public String number;
	@Override
	public String toString() {
		return province + " " + number;
	}
	
}
