package com.yac.yacapp.domain;

import java.io.Serializable;

public class Part implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
 * {
"id": 20,
"score": 70,
"status": "需检测",
"comment": "破损程度一般，建议检测",
"car_id": 3,
"type": 10,
"name": "机滤",
"last_update_time": "2015-02-01T18:02:52.000",
"used_miles": 0
},
 */
	public static final String KEY_ID = "id";
	public static final String KEY_SCORE = "score";
	public static final String KEY_STATUS = "status";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_CAR_ID = "car_id";
	public static final String KEY_TYPE = "type";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAST_UPDATE_TIME = "last_update_time";
	public static final String KEY_USED_MILES = "used_miles";
	
	public Long id;
	public Integer score;
	public String status;
	public String comment;
	public Long car_id;
	public Integer type;
	public String name;
	public String last_update_time;
	public Integer used_miles;
	
	public Part(){
	}
	
	public Part(Integer type, String name, Integer score){
		this.score = score;
		this.type = type;
		this.name = name;
	}
	
}
