package com.example.batmovel;

import android.app.Application;
import android.content.SharedPreferences;

public class HitchhikingApplication extends Application {
	
	public static final String SHARED_PREFS_NAME = "KINNEGAD";
	private static final String USP_NUMBER_KEY = "usp_number";
	private static final String STOA_LOGIN_KEY = "stoa_login";
	private static final String RIDE_KEY = "teh_ride_saved";
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
		
		editor.putString(RIDE_KEY, ride.toJsonString());
		//TODO deletar isso
		System.err.println("----saved----\n");
		System.err.println(ride.toJsonString());
		System.err.println("----saved----\n");
		
		editor.commit();
	}

	public Ride loadRide(){
		SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		
		String rideS = preferences.getString(RIDE_KEY, null);
		System.err.println("----loaded----\n");
		System.err.println(rideS);
		System.err.println("----loaded----\n");
		//TODO deletar isso
		
		if (rideS == null)
			return null;
		
		Ride ride = new Ride(rideS);
		
		return ride;
	}
	
}
