package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class CheckReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Long id;
	public Long order_id;
	public String name;
	public String complete_time;
	public String create_time;
	public Integer qualified_size;
	public Car car;
	public List<Report> reports;
	public Integer unqualified_size;
/*
 * {
                "car": {
                    "bought_time": "2011-01-01T18:02:52.000",
                    "brand": "MINI",
                    "brand_img_url": {
                        "id": 1911,
                        "original_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1911.jpg/s1024.jpg",
                        "raw_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1911.jpg",
                        "thumbnail_url": "http://7xiqd7.com2.z0.glb.qiniucdn.com/1911.jpg/s250.jpg"
                    },
                    "category": "PACEMAN JCW",
                    "chassis_number": "哦哦",
                    "engine_number": "呃呃呃",
                    "id": 148,
                    "licence": {
                        "number": "rb0718哦哦",
                        "province": "津"
                    },
                    "miles": 60000,
                    "model": "2014款 T L4 1.6T JCW ALL 4 "
                },
                "complete_time": "2015-04-28T21:37:35.000",
                "create_time": "2015-04-28T21:28:52.000",
                "id": 15,
                "name": "0428报告单",
                "qualified_size": 1,
                "reports": [
                    {
                        "imgs": {},
                        "name": "检测后增项",
                        "status": 0,
                        "status_comment": ""
                    }
                ],
                "unqualified_size": 5
            }
 */
}
