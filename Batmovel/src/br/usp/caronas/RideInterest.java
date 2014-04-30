package br.usp.caronas;

import org.json.JSONException;
import org.json.JSONObject;

public class RideInterest {

	String id;
	String hitchhiker; //numero usp do caroneiro // OBRIGATORIO
	String login; //login do STOA do caroneiro
	String current_latitude; // no JSON, actuallatitude
	String current_logitude; // no JSON, actuallongitude
	String message; //OBRIGATORIO
	Ride bound_ride; //representada pelo riderecord_id no JSON // OBRIGATORIO

	public RideInterest(Ride r){
		this.bound_ride = r;
	}

	public String toJsonString(){
		JSONObject interest = new JSONObject();
		JSONObject wrapper = new JSONObject();
		try {
			interest.put("riderecord_id",bound_ride.id);
			interest.put("hitchhiker", hitchhiker);
			interest.put("message", message);
			wrapper.put("interestintheride", interest);
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		System.err.println(wrapper.toString());
		
		return wrapper.toString();
	}

}
