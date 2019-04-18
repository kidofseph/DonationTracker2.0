package background.util;

import java.io.File;

public class DonationTrackerConstants 
{
	public final static String CLIENT_ID = "E3VmbsETpxHwXCWKeoDE7F82h5nBxZpMOs5qKCdk";
	public final static File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/highlighttracker");
	
	public static final String SCOPES = "donations.read";
	public static final String TOKEN_SERVER_URL = "https://www.streamlabs.com/api/v1.0/token";
	public static final String AUTH_SERVER_URL = "https://www.streamlabs.com/api/v1.0/authorize";
	public static final String API_KEY = "E3VmbsETpxHwXCWKeoDE7F82h5nBxZpMOs5qKCdk";
	public static final String API_SECRET = "ydMilNXFdk4fbKBuJLCexAxBpvIZi7Vh68HlIANg";
	public static final String REDIRECT_URL = "http://localhost:9090/Callback";
	public static final String DOMAIN = "localhost";
	public static final int PORT = 9090;
//	public final static String USER = "sevens1ns";
}
