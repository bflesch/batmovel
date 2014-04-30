package br.usp.caronas;

import android.content.Context;
import android.content.SharedPreferences;

public class User {

	private static final String USP_NUMBER_KEY = "usp_number";
	private static final String STOA_LOGIN_KEY = "stoa_login";

	private static User currentUser = null;
	private static Context applicationContext = null;

	protected String uspNumber;
	protected String stoaLogin;

	public boolean isEmpty(){
		return (uspNumber == null || stoaLogin == null);
	}

	public User(){
		super();
	}
	
	public User(String nusp, String login) {
		super();
		uspNumber = nusp;
		stoaLogin = login;
	}
	
	public static User getCurrentUser(Context context){
		setApplicationContext(context);
		if (currentUser == null)
			loadUserFromPreferences();
		return currentUser;
	}

	public static void login(Context context, User user){
		setCurrentUser(user);
		setApplicationContext(context);
		saveCurrentUserIntoPreferences();
	}
	
	public static void logout(Context context){
		setCurrentUser(null);
		setApplicationContext(context);
		saveCurrentUserIntoPreferences();
	}
	
	private static void setCurrentUser(User user){
		currentUser = user;
	}
	
	private static void setApplicationContext(Context context){
		if (applicationContext == null){
			applicationContext = context.getApplicationContext();
		}
	}

	private static void loadUserFromPreferences(){
		SharedPreferences preferences = applicationContext.getSharedPreferences(HitchhikingApplication.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		currentUser = new User();
		currentUser.uspNumber = preferences.getString(USP_NUMBER_KEY, null);
		currentUser.stoaLogin = preferences.getString(STOA_LOGIN_KEY, null);
	}

	private static void saveCurrentUserIntoPreferences(){
		SharedPreferences settings = applicationContext.getSharedPreferences(HitchhikingApplication.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		if (currentUser != null){
			editor.putString(USP_NUMBER_KEY, currentUser.uspNumber);
			editor.putString(STOA_LOGIN_KEY, currentUser.stoaLogin);
		} else {
			editor.remove(USP_NUMBER_KEY);
			editor.remove(STOA_LOGIN_KEY);
		}

		editor.commit();
	}
}
