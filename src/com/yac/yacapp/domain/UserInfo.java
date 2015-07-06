package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
 * {
"basic": { … },
"location": [ … ],
"cars": [ … ],
"coupons": [ ],
"check_reports": [ ],
"orders": [ … ]
}
 */
	public static final String KEY_BASIC = "basic";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_CARS = "cars";
	public static final String KEY_COUPON = "coupons";
	public static final String KEY_CHECK_REPORTS = "check_reports";
	public static final String KEY_ORDERS = "orders";
	
	
	public UserBasic basic;
	public List<Location> location;
	public List<Car> cars;
	public List<Coupon> coupons;
	public List<CheckReport> check_reports;
	public List<Order> orders;
}
