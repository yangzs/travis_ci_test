package com.yac.yacapp.domain;

import java.io.Serializable;
import java.util.List;

public class Pictures4Transfer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Integer position;
	public List<Picture> pictures;
	public Pictures4Transfer(Integer position, List<Picture> pictures) {
		super();
		this.position = position;
		this.pictures = pictures;
	}
	
}
