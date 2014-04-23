package com.example.batmovel;

import android.app.Application;
import android.content.SharedPreferences;

public class HitchhikingApplication extends Application {
	
	public static final String SHARED_PREFS_NAME = "KINNEGAD";
	private static final String USP_NUMBER_KEY = "usp_number";
	private static final String STOA_LOGIN_KEY = "stoa_login";
	private User user = null;
	
	public User getCurrentUser(){
		if (user == null)
			loadUserFromPreferences();
		return user;
	}
	
	public void setCurrentUser(User user){
		this.user = user;
	}
	
	public void loadUserFromPreferences(){
		SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		
		user = new User();
		user.uspNumber = preferences.getString(USP_NUMBER_KEY, null);
		user.stoaLogin = preferences.getString(STOA_LOGIN_KEY, null);
	}
	
	public void saveCurrentUserIntoPreferences(){
		SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(USP_NUMBER_KEY, user.uspNumber);
		editor.putString(STOA_LOGIN_KEY, user.stoaLogin);
		
		editor.commit();
	}
	
	public void saveRide(Ride ride){
		SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(USP_NUMBER_KEY, user.uspNumber);
		editor.putString(STOA_LOGIN_KEY, user.stoaLogin);
		
		editor.commit();
	}

}
