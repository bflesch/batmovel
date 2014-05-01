package br.usp.caronas;

import java.util.Date;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class HitchhikingApplication extends Application {
	
	public static final String SHARED_PREFS_NAME = "KINNEGAD";

	private static final String RIDE_KEY = "teh_ride_saved";
	
	private Location currLocation = null;
	LocationManager locationManager = null;
	LocationListener locationListener = null;
	
	public void saveRide(Ride ride){
		SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(RIDE_KEY, ride.toJsonString());

		
		editor.commit();
	}

	public Ride loadRide(){
		SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		
		String rideS = preferences.getString(RIDE_KEY, null);
		
		if (rideS == null)
			return null;
		
		Ride ride = new Ride(rideS);
		
		return ride;
	}
	
	protected Location betterLocation(Location location, Location currentBestLocation) {
		
		System.err.println(location.toString());
		
		if (location.getAccuracy() > 100)
			return currentBestLocation;

		if (currentBestLocation == null)
			return location;    
	    
	    if (currentBestLocation.getTime() < location.getTime())
	        return location;

	    return currentBestLocation;
	}
	
	public String getCoords (){
		int five_minutes = 1000 * 60 * 5; //in milliseconds
		Date rightNow = new Date();
		if (currLocation == null || (rightNow.getTime() - currLocation.getTime() > five_minutes)) {
			currLocation = null;
			return null;
		}

		//TODO on the other side, make it better
		return ""+currLocation.getLatitude()+","+currLocation.getLongitude();
	}
	
    public void startGPS() {
		
		@SuppressWarnings("unused")
		int twenty_seconds = 20 * 1000;
		
		BatteryLevelReceiver battery = new BatteryLevelReceiver(); 
		if (battery.batteryDying())
			return;
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				currLocation = betterLocation(location,currLocation);
			}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		//TODO (extra feature) add network provider
	}
	
	public void stopGPS(){
		if (locationListener != null)
		    locationManager.removeUpdates(locationListener);
		locationListener = null;
	}
}
