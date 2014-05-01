package br.usp.caronas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BatteryLevelReceiver extends BroadcastReceiver {
    
	private static boolean batteryNearDead = false;
	
	@Override
    public void onReceive(Context context, Intent intent) { 
    	if (intent.getAction() == Intent.ACTION_BATTERY_LOW) {
    		batteryNearDead = true; 
    	}
    	if (intent.getAction() == Intent.ACTION_BATTERY_OKAY) {
    		batteryNearDead = false; 
    	}
    }
	
	public boolean batteryDying(){
		return batteryNearDead;
	}
}