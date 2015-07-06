package com.yac.yacapp.icounts;


public interface ICounts {

	public final static String ROOT = 
			/*"http://120.132.59.94:80";*///正式库
			/*"http://123.59.52.186/develop";*///测试库
			"http://123.59.52.186/staging";//测试库
			/*"http://192.168.2.111:8080";*///王帅
	public final static String ROOTURL = ROOT + "/v1/api";
	public final static String ROOTURL_V2 = ROOT + "/v2/api/";
	
	public static final String SIGN_VERIFY_SUBURL = "/sign_verify_code.json";
	public static final String SIGN_UP_SUBURL= "/car_user/sign_up.json";
	public static final String USER_INFO_SUBURL= "/user_info.json";
	public static final String GET_PRODUCTS_SUBURL = "/products.json";
	public static final String PICK_TIME_SUBURL = "/time_segments.json";
	public static final String GET_COUPONS_SUBURL = "/coupons.json";
	public static final String CONVERSION_COUPON_SUBURL = "/coupons/conversion.json";
	public static final String CREATE_ORDER_SUBURL = "/order/create.json";
	public static final String ZFB_CHARGE_SUBURL = "/charge.json";
	public static final String GET_CAR_BRAND_SUBURL = "/meta_brands.json";
	public static final String GET_CATEGORY_SUBURL = "/meta_categories.json";
	public static final String GET_MODEL_SUBURL = "/meta_cars.json";
	public static final String CREATE_CAR_SUBURL = "/cars/create.json";
	public static final String UPDATE_CAR_SUBURL = "/cars/update.json";
	public static final String DELETE_CAR_SUBURL = "/cars/delete.json";
	public static final String SERVICE_FLOW_SUBURL = "/order/service_flow.json";
	public static final String CLIENT_FEEDBACK_SUBURL = "/feedback.json";
	public static final String GET_TRACKS_SUBURL = "/tracks.json";
	public static final String META_USER_SUBURL = "/meta_user.json";
	//use v2 
	public static final String GETORDER_SUBURL = "/orders.json";
	public static final String UPDATE_ORDER_SUBURL = "/order/update.json";
	//location
	public static final String CREATE_ADDRESS_SUBURL= "/address/create.json";
	public static final String UPDATE_ADDRESS_SUBURL= "/address/update.json";
	public static final String DELETE_ADDRESS_SUBURL= "/addresses/delete.json";
	//getui 
	public static final String GETUI_CLIENTID_UPDATE = "/push_config/update.json";
	
	public static final int NET_FAILURE = 99;
	public static final int PARSERJSON_FAILURE = 98;
	public static final int PARSERJSON_SUCCESS = 97;
	public static final int ZFB_CHARGE_WHAT = 96;
	public static final int FIRST_CHECK_USERINFO_WHAT = 95;
	public static final int UPDATE_USERINFO_WHAT = 94;
	public static final int GT_CLIENTID_UPDATE_WHAT = 93;
	
	public static final String DefaultPhoneNum = "10655020067877";
	
	public static final int KEEPER_PICK_SERVICE = 1;
	public static final int DOOR_SERVICE = 2;
	public static final int VALIDATE_CAR_SERVICE = 3;
	public static final int KEEPER_TEST_SERVICE = 4;
	public static final int VERIFY_LOSS_SERVICE = 5;
	public static final int EMERGENCY_RESCUE_SERVICE = 6;
	public static final int FROM_CARPARTS_DETAILS_SERVICE = 7;
	
	public static final String SP_NAME = "yacapp_sp";
	public static final String SP_UserInfo = "userinfo";
	public static final String SP_GT_CLIENTID = "getui_clientid";
	
	public static final int FROM_MYCAR = 1;
	public static final int FROM_CARMODEL = 2;
	
	public static final boolean debug = false;
	
	public static final String UPDATA_USERINFO_ACTION = "com.yac.yacapp.updateuserinfoaction";
	
	public static final int PICK_LOCATION_FLAG = 1;
}
