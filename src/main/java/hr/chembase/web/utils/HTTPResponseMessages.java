package hr.chembase.web.utils;

public class HTTPResponseMessages {

	public static final String HTTP_SESSION_ID_INVALID = "INVALID_SESSION_ID";

	public static final String HTTP_OK = "OK";
	public static final String HTTP_ERROR = "Something went wrong!";
	
	public static final String HTTP_MISSING_PARAMS_IN_REQUEST = "Missing mandatory params in request!";
	public static final String INVALID_PARAMS_IN_REQUEST = "Invalid params in request!";

	public static final String HTTP_DATABASE_UNAVAILABLE = "Database unavailable!";
	public static final String HTTP_SQL_EXCEPTION = "SQL Exception!";

	public static final String HTTP_LOGIN_FAIL = "Login failed!";
	public static final String HTTP_LOGIN_FAIL_LOCKED = "Login failed! Account locked.";
	
	public static final String HTTP_FORBIDDEN = "Forbidden!";
}
