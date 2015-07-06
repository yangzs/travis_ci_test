package com.yac.yacapp.domain;

import java.io.Serializable;

public class ClientFeedback implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String comment;
	public Integer keeper_stars;
	public Integer order_stars;
	public Boolean if_feedback_committed;

}
