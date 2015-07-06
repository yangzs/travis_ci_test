package com.yac.yacapp.domain;

import java.io.Serializable;

public class Location implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * {
"longitude": 21.1,
"latitude": 21.1,
"address": "",
"name": ""
}
	 */
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_NAME = "name";
	public static final String KEY_ID = "id";
	public static final String KEY_USER_ID = "user_id";
	
	public Double longitude;
	public Double latitude;
	public String address;
	public String name;
	public Long id;
	public Long user_id;
	
	public Location() {
		super();
	}

	public Location(Double longitude, Double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Location(Double longitude, Double latitude, String address, String name) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
		this.name = name;
	}
	
	
}
