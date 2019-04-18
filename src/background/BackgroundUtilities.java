package background;

import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;

import background.util.DonationTrackerConstants;
//import util.OAuth2ClientCredentials;

public class BackgroundUtilities 
{
	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static JsonFactory JSON_FACTORY = new JacksonFactory();
//	private static FileDataStoreFactory DATA_STORE_FACTORY;
	public static Credential authorize(DataStoreFactory p_dsf) throws Exception
	{
		
		AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
			      .authorizationHeaderAccessMethod(),
			      HTTP_TRANSPORT,
			      JSON_FACTORY,
			      new GenericUrl(DonationTrackerConstants.TOKEN_SERVER_URL),
			      new ClientParametersAuthentication(
			          DonationTrackerConstants.API_KEY, DonationTrackerConstants.API_SECRET),
			      DonationTrackerConstants.CLIENT_ID,
			      DonationTrackerConstants.AUTH_SERVER_URL).setScopes(Arrays.asList(DonationTrackerConstants.SCOPES))
			      .setDataStoreFactory(p_dsf).build();

		//authorize
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(DonationTrackerConstants.DOMAIN).setPort(DonationTrackerConstants.PORT).build();
		
		
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		
	}
}
