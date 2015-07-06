package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Long id;
	public List<OrderProduct> products;
	public List<OrderProduct> increase_products;
//	public List<OrderInspection> inspections;
	public Boolean customerEvaluated;
	public Boolean paid;// true 已付款   false 未付款
	public String number;
	public Boolean disabled;
	public Boolean committed;
	//public Integer order_type;//没有用
	public String service_type;
	public String peer_source;
	public Long source_order_id;
	public List<OrderKeeperBasic> keeper_basics;
	public Long current_keeper_id;
	public OrderClientBasic client_basic;
	public Coupon coupon;
	public Car car;
	public String pick_time;
	public String pick_time_segment;
	public String place_time;
	public String start_time;
	public String end_time;
//	public OrderClientFeedback client_feedback;
	public Integer keeper_rating;
	public Integer order_rating;
	public String user_evaluation;
	public Double total_price;
	public Double fee;
	public Double coupon_price;
	public OrderOperator operator;
//	public List<OrderSupplier> suppliers;//供应商
	public String status;
	public String comment;
	public String product_comment;
	public String operator_comment;
	public Long take_keeper_id;
	public String take_time;
	public String give_back_time;
	public Long give_back_keeper_id;
	public List<Long> relation_order_ids;
	public Boolean keeper_confirmed;
//	public "pay_types": [ ],
//	public Inetger pay_type;
//	public List<E>"pay_type_infos": [ ]
//	public String sale_source;
	public Integer pay_status;//1,已支付；2未支付；3部分支付
	public Integer pay_mode;//1线上；2线下
	
/*
 * {
"id": 390,
"products": [ … ],
"inspections": [ ],
"customerEvaluated": false,
"paid": false,
"number": "20150525141515793",
"disabled": false,
"committed": true,
"order_type": null,
"service_type": "keeper",
"peer_source": "app",
"source_order_id": null,
"keeper_basics": [ … ],
"current_keeper_id": 999,
"client_basic": { … },
"coupon": null,
"car": { … },
"pick_time": "2015-05-26 周二",
"pick_time_segment": "10:00-11:00",
"place_time": "2015-05-25T14:15:15.000",
"start_time": null,
"end_time": null,
"client_feedback": { … },
"keeper_rating": 5,
"order_rating": 5,
"user_evaluation": "",
"total_price": 416,
"fee": 69,
"coupon_price": 0,
"operator": null,
"suppliers": [ … ],
"status": "待付款",
"comment": "",
"product_comment": "",
"operator_comment": null,
"take_keeper_id": null,
"take_time": null,
"give_back_time": null,
"give_back_keeper_id": null,
"relation_order_ids": [ ],
"keeper_confirmed": false,
"pay_types": [ ],
"pay_type": null,
"pay_type_infos": [ ]
},
 */
	
}
