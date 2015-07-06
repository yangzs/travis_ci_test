package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class PicktimeSegment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String KEY_KEY = "key";
	public static final String KEY_DATA = "data";	
	
	public String key;
	public List<String> data;
	
}
