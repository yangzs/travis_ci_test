package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class CarCareBook implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
{
"miles": 105000,
"events": [
"保养前刹车片",
"保养后刹车片",
"保养机油",
"保养机滤"
]
}
	 */
	public static final String KEY_MILES = "miles";
	public static final String KEY_EVENTS = "events";
	
	public Integer miles;
	public List<String> events;
}
