package com.yac.yacapp.domain;

import java.io.Serializable;

public class CarModelBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String KEY_MODEL_TYPE = "model_type";
	public static final String KEY_BRAND_NAME = "brand_name";
	public static final String KEY_CATEGORY_NAME = "category_name";
	public static final String KEY_ENGINE_DISPLACEMENT = "engine_displacement";
	public static final String KEY_PRODUCTION_YEAR = "production_year";
	public static final String KEY_PRODUCER = "producer";
	public static final String KEY_ENGINE_OIL_AMOUNT = "engine_oil_amount";
	public static final String KEY_CAR_MODEL_NAME = "car_model_Name";
	
	public Integer model_type;
	public String brand_name;
	public String category_name;
	public String engine_displacement;
	public String production_year;
	public String producer;
	public String engine_oil_amount;
	public String car_model_Name;
	public CarModelBean(Integer model_type, String brand_name, String category_name, String engine_displacement, String production_year, String producer, String engine_oil_amount, String car_model_Name) {
		super();
		this.model_type = model_type;
		this.brand_name = brand_name;
		this.category_name = category_name;
		this.engine_displacement = engine_displacement;
		this.production_year = production_year;
		this.producer = producer;
		this.engine_oil_amount = engine_oil_amount;
		this.car_model_Name = car_model_Name;
	}
	public CarModelBean() {
		super();
	}
	@Override
	public String toString() {
		return "CarSettingDetailsBean [model_type=" + model_type + ", brand_name=" + brand_name + ", category_name=" + category_name + ", car_model_name=" + car_model_Name + ", engine_displacement=" + engine_displacement + ", production_year=" + production_year + ", producer=" + producer + ", engine_oil_amount=" + engine_oil_amount + "]";
	}
	
}
