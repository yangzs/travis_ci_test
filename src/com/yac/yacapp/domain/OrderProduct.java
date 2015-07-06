package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class OrderProduct implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
{
"id": 691,
"comment": null,
"complete": false,
"product_type": 31,
"product_name": "豹王32",
"unit_count": 1,
"price": 32,
"labour_price": 0,
"source_inspection": null,
"total_price": 32,
"pic_id": [ ],
"pics": [ ],
"complete_time": null,
"keeper_id": null,
"part_type": 10,
"user_defined": false,
"product_info": "机滤_豹王32",
"product_categories": [
"机滤"
]
},
	 */
	public static final String KEY_ID = "recommended";
	public static final String KEY_COMMENT = "price";
	public static final String KEY_COMPLETE = "unit";
	public static final String KEY_PRODUCT_TYPE = "product_type";
	public static final String KEY_PRODUCT_NAME = "product_name";
	public static final String KEY_UNIT_COUNT = "product_name";
	public static final String KEY_PRICE = "product_name";
	public static final String KEY_LABOUR_PRICE = "labour_price";
	public static final String KEY_SOURCE_INSPECTION = "unit_count";
	public static final String KEY_TOTAL_PRICE = "user_defined";
	public static final String KEY_PIC_ID = "product_categories";
	public static final String KEY_PICS = "product_categories";
	public static final String KEY_COMPLETE_TIME = "product_categories";
	public static final String KEY_KEEPER_ID = "product_categories";
	public static final String KEY_PART_TYPE = "product_categories";
	public static final String KEY_USER_DEFINED = "product_categories";
	public static final String KEY_PRODUCT_INFO = "product_categories";
	public static final String KEY_PRODUCT_CATEGORIES = "product_categories";
	public static final String KEY_PAY_STATUS = "pay_status";
	public static final String KEY_REFEREE_KEEPER_ID = "referee_keeper_id";
	public static final String KEY_REFEREE_OPERATOR_ID = "referee_operator_id";
	public static final String KEY_SELECTION_MODE = "selection_mode";
	
	
	public Long id;
	public String comment;
	public Boolean complete;
	public Integer product_type;
	public String product_name;
	public Integer unit_count;
	public String unit;
	public Double price;
	public Double labour_price;
	public String source_inspection;
	public Double total_price;
	public List<Integer> pic_id;
	public List<Picture> pics;
	public String complete_time;
	public Long keeper_id;
	public Integer part_type;
	public Boolean user_defined;
	public String product_info;
	public List<String> product_categories;
    public Integer pay_status;//0，未付款；1，已付款
    public Long referee_keeper_id;//推荐管家ID
    public Long referee_operator_id;//推荐客服ID
    public Integer selection_mode;//1,用户自主下单;2,待确认;3,同意;4,拒绝
    	
//    public Boolean required_pay;
//    public Integer source_mode;

}
