package com.yac.yacapp.domain;

import java.io.Serializable;

public class Product4Create implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
 *     "products": [
        {
            "product_type": 1, 
            "part_type":1,
            "unit_count": 2
        }
    ],
    {
            "product_type": 5,
            "part_type": 9,
            "unit_count": 2,
            "selection_mode":2,
            "referee_keeper_id":14
      }
 */
	public static final String KEY_PRODUCT_TYPE = "product_type";
	public static final String KEY_PART_TYPE = "part_type";
	public static final String KEY_UNIT_COUNT = "unit_count";
	public static final String KEY_SELECTION_MODE = "selection_mode";
	public static final String KEY_REFEREE_KEEPER_ID = "referee_keeper_id";
	
	public Integer product_type;
	public Integer part_type;
	public Integer unit_count;
	public Integer selection_mode;
	public Long referee_keeper_id ;
}
