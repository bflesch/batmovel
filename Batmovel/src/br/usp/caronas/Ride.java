package br.usp.caronas;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Ride {

	public int id; // identificador da carona no sistema
	public String n_usp; /*no json, driver*/
	public String login; /*nome_de_usuario_no_stoa */
	public String departuretime;
	public String local_partida; /*no json, actuallocalization*/
	public String local_chegada; /*no json, targetlocalization*/
	public String message;

	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public Ride(){
	}

	public Ride(String jsonString) {
		fromJsonString(jsonString);
	}

	static public ArrayList<Ride> listFromJsonList(JSONObject jlist){
		try {
			JSONArray jsonData = new JSONArray();
			ArrayList<Ride> data = new ArrayList<Ride>();
			jsonData = jlist.getJSONArray("riderecordlist");
			for(int i=0; i<jsonData.length(); i++){
				Ride ride = new Ride(jsonData.getJSONObject(i).toString());
				data.add(ride);
			}
			return data;
		}
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void fromJsonString(String jsonString) {
		try {
			JSONObject object = new JSONObject(jsonString);
			object = object.getJSONObject("riderecord");
			this.id = object.getInt("id");
			this.local_partida = object.getString("actuallocalization");
			this.local_chegada = object.getString("targetlocalization");
			this.departuretime = object.getString("departuretime");
			this.n_usp = object.optString("driver");
			this.login = object.optString("login"); //TODO decidir isso. Do server sempre tem. 
			                                        //No disco, nem sempre
			this.message = object.optString("message");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
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

	public boolean isGone() {
		Calendar now = Calendar.getInstance();
		Date departure;
		Calendar then = Calendar.getInstance();

		try {
			departure = dateFormatter.parse(departuretime);
			then.setTime(departure);
		} catch (ParseException e) {
			e.printStackTrace();
			return true;
		}
		try { 
			boolean gone = now.after(then);
			return gone;
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			return true;
		}
	}
}
