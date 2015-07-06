package com.yac.yacapp.domain;

import java.io.Serializable;

public class Supplier implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Long id;
	public String name;
	public Double longitude;
	public Double latitude;
	public String address;
	//public String address_name;
	public Double rating;
	public Boolean layoff;
	public String contact_name;
	public String phone_number;
	public String mobile_number;
	public Integer time_score;
	public Integer price_score;
	public Integer service_score;
	public Integer part_score;
	public Integer work_score;
	public Integer confirm_score;
	public Long supplier_id;
	public Long keeper_id;
	public String type;
	public String evaluation;
	
	/*"supplier": {
        "address": "默认供应商",
        "address_name": 1,
        "confirm_score": 1,
        "contact_name": "默认供应商",
        "evaluation": 1,
        "keeper_id": 1,
        "latitude": 39.980141,
        "layoff": false,
        "longitude": 116.368005,
        "mobile_number": 1,
        "name": "默认",
        "part_score": 1,
        "phone_number": "4000102766",
        "price_score": 1,
        "rating": 3.5,
        "service_score": 1,
        "supplier_id": 999,
        "time_score": 1,
        "type": 1,
        "work_score": 1
    },*/
}
