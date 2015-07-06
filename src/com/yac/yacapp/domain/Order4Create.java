package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class Order4Create implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
{"phone_number": "123456", 
    "contact_name": "张三22", 
    "user_id": 1,
    "car_id": 1, 
    "location": {
        "latitude": 12333.1231231, 
        "longitude": 1231231.1232, 
        "name": "豪情酒吧", 
        "address": "工人体育场西路7号"
    },     
    "coupon_id": 1, 
    "coupon_code": "abcde", 
    "pick_time": "2011-08-21 周一", 
    "pick_time_segment": "08:00-09:00", 
    "service_type": "keeper", 
    "comment": "备注信息", 
    "operator_comment":"客服备注信息",
    "peer_source": "app", 
    "products": [
        {
            "product_type": 1, 
            "part_type":1,
            "unit_count": 2
        }
    ],
    "product_comment":"商品说明"
}*/
	public String phone_number;
	public String contact_name;
	public Long user_id;
	public Long car_id;
	public Location location;
	public Long coupon_id;
	public String coupon_code;
	public String pick_time;
	public String pick_time_segment;
	public String service_type;
	public String comment;
	public String operator_comment;
	public String peer_source;
	public List<Product4Create> products;
	public String product_comment;
	public Integer pay_mode;//1线上；2线下
	
}
