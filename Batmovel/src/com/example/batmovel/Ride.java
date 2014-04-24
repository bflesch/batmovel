package com.example.batmovel;

import org.json.JSONException;
import org.json.JSONObject;

public class Ride {

	public String id; // identificador da carona no sistema
	public String n_usp; /*no jason, driver*/
	public String login; /*nome_de_usuario_no_stoa */
	public String departuretime;
	public String local_partida; /*no json, actuallocalization*/
	public String local_chegada; /*no json, targetlocalization*/
	public String message;


	public Ride(){
	}

	public Ride(String jsonString) {
		fromJsonString(jsonString);
	}

	public void fromJsonString(String jsonString) {
		try {
			JSONObject object = new JSONObject(jsonString);
			object = object.getJSONObject("riderecord");
			this.n_usp = object.getString("driver");
			this.login = object.getString("login");
			this.local_partida = object.getString("actuallocalization");
			this.local_chegada = object.getString("targetlocalization");
			this.departuretime = object.getString("departuretime");
			this.message = object.getString("message");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean isPublishable(){
		//TODO exigir campos exigidos
		return true;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject recordJson = new JSONObject();
		try {
			recordJson.put("driver", n_usp);
			recordJson.put("login", login);
			recordJson.put("actuallocalization", local_partida);
			recordJson.put("targetlocalization", local_chegada);
			recordJson.put("departuretime", departuretime);
			recordJson.put("message", message);
			json.put("riderecord",recordJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String toJsonString(){
		return this.toJSONObject().toString();
	}
}
