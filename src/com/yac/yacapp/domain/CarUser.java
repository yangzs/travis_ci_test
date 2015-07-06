package com.yac.yacapp.domain;

public class CarUser {

	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_VERIFY_CODE = "verify_code";
	public static final String KEY_SIGN_ORIGIN = "sign_origin";
	
	public Long user_id;
	public String name;
	public String gender;
	public String phone;
	public String token;
	public String verify_code;
	public String sign_origin;

/*
"code": "00000",
"data": {
    "userId": 1007,
    "token": "36FF7814591B200F916BBFA724CBABDB",
    "phone": "15202207925",
    "verify_code": "029814",
    "sign_origin": "wx"
	}
	
	"code": "00000",
"data": {
"token": "L9CWzmITsLTUtugWQTpUvLGTY6NADm2fTa1jqQdxSmzUb3KFgBPzReiBHu5YHIEY",
"phone": "13141267740",
"name": 
"gender": "male",
"user_id": 13,
"verify_code": "632765",
"sign_origin": "app"
}
	*/
}