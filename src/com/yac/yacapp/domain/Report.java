package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class Report implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String KEY_NAME = "name";
	public static final String KEY_STATUS = "status";
	public static final String KEY_STATUS_COMMENT = "status_comment";
	public static final String KEY_IMGS = "imgs";
	
	public String name;
	public Integer status;
	public String status_comment;
	public List<Picture> imgs;
/*
 * {
        "imgs": {},
        "name": "检测后增项",
        "status": 0,
        "status_comment": ""
    }
 */

}
