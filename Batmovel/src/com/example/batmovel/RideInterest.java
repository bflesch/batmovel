package com.example.batmovel;

public class RideInterest {
	
	String id; //no JSON, riderecord_id // OBRIGATORIO
	String hitchhiker; //numero usp do caroneiro // OBRIGATORIO
	String login; //login do STOA do caroneiro
	String current_latitude; // no JSON, actuallatitude
	String current_logitude; // no JSON, actuallongitude
	String message; //OBRIGATORIO
	Ride bound_ride;

	public RideInterest(Ride r){
		this.bound_ride = r;
	}

}
