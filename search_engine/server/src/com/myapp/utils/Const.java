package com.myapp.utils;

public class Const 
{
	public final static String ROOT = "./database/"; 
	
	/*db store*/
	public final static String DB_STORE_NAME = "entity_store"; 

	

	// 7 days
	public final static int LOGIN_SESSION_AGE = 24  * 3600 * 7; 

	/*message*/
	public final static String LOGIN_FIRST_INFO = "Please login first.";
	public final static String SESSION_USERNAME_NULL_INFO = "Session username is null!";
	public static final String CAN_NOT_JOIN_GROUP_INFO = "You cannot join this group!";
	public static final String NO_THIS_USER_INFO = "No this user!";

	/*URL*/
	public final static String HOME_URL = "/home"; 
	public static final String HOME_TEST_URL = "/hometest";

	//Account
	public static final String REGISTER_URL = "/register";
	public final static String LOGIN_URL = "/login"; 
	public final static String LOGOFF_URL = "/logoff";
	public static final String ACCOUNT_SETTING_URL = "/setting";
	

	//Search
	public static final String SEARCH_URL= "/search";
	public static final String SEARCH_RESULT_URL = "/search_result";
	public static final String VOICE_SEARCH = "/voice_search";
}
