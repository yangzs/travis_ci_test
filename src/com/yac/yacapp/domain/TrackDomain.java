package com.yac.yacapp.domain;

public class TrackDomain {

	public String address;
	public String address_name;
	public String createTime;
	public Double latitude;
	public Double longitude;
	public Supplier supplier;
	public Long track_id;
	public Integer type;
	
	/*
	 * {
            "address": "默认供应商",
            "address_name": 1,
            "createTime": "2015-06-09T16:41:35.000",
            "latitude": 39.980418,
            "longitude": 116.371086,
            "supplier": {
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
            },
            "track_id": 28,
            "type": 3
        }
	 */
}
