package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class OrderServiceFlow implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public String time;
	public Integer status;
	public String address;
	public List<OrderServiceFlowItem> items;
	
/*
		{
            "items": [
                {
                    "name": "左前方45度照片",
                    "pics": [
                        {
                            "raw_url": "http://dummyimage.com/120x600",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 3,
                            "original_url": "http://dummyimage.com/125x125"
                        },
                        {
                            "raw_url": "http://dummyimage.com/336x280",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 2,
                            "original_url": "http://dummyimage.com/120x240"
                        }
                    ]
                },
                {
                    "name": "左前方45度照片",
                    "pics": [
                        {
                            "raw_url": "http://dummyimage.com/240x400",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 3,
                            "original_url": "http://dummyimage.com/300x250"
                        },
                        {
                            "raw_url": "http://dummyimage.com/88x31",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 2,
                            "original_url": "http://dummyimage.com/1200x1200"
                        }
                    ]
                },
                {
                    "name": "左前方45度照片",
                    "pics": [
                        {
                            "raw_url": "http://dummyimage.com/120x600",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 3,
                            "original_url": "http://dummyimage.com/100x200"
                        },
                        {
                            "raw_url": "http://dummyimage.com/240x400",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 2,
                            "original_url": "http://dummyimage.com/728x90"
                        },
                        {
                            "raw_url": "http://dummyimage.com/336x280",
                            "thumbnail_url": "http://dummyimage.com/100x200",
                            "id": 3,
                            "original_url": "http://dummyimage.com/125x125"
                        }
                    ]
                }
            ],
            "status": 1,
            "address": "默认供应商地址",
            "name": "初检爱车",
            "time": "2015-05-04T14:02:35.000"
        }
 */
}
