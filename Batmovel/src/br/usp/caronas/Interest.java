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

public class Interest {

	public int interest_id;
	public int ride_id;
    //"riderecord_id":"id_da_carona",
    public String nusp_rider;
    //"hitchhiker":"numero_usp_do_caroneiro",
    public String user_rider;
    //"login":"nome_de_usuario_no_stoa",
    public String lat_atual_rider;
    //"actuallatitude":"valor_da_latitude_atual_em_float",
    public String lon_atual_rider;
    //"actuallongitude":"valor_da_longitude_atual_em_float",
    public String mensagem;
    //"message":"mensagem"

	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public Interest(){
	}

	public Interest(Ride r){
		this.ride_id = r.id;
	}
	
	public Interest(String jsonString) {
		fromJsonString(jsonString);
	}

	static public ArrayList<Interest> listFromJsonList(JSONObject jlist){
		try {
			JSONArray jsonData = new JSONArray();
			ArrayList<Interest> data = new ArrayList<Interest>();
			jsonData = jlist.getJSONArray("interestintheinterestlist");
			for(int i=0; i<jsonData.length(); i++){
				Interest interest = new Interest(jsonData.getJSONObject(i).toString());
				data.add(interest);
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
			object = object.getJSONObject("interestintheride");
			this.interest_id = object.getInt("id");
			this.ride_id = object.getInt("riderecord_id");
		    this.nusp_rider = object.getString("hitchhiker");
		    this.user_rider = object.getString("login");
		    this.lat_atual_rider = object.getString("actuallatitude");
		    this.lon_atual_rider = object.getString("actuallongitude");
		    this.mensagem = object.getString("message");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toJsonString(){
		JSONObject interest = new JSONObject();
		JSONObject wrapper = new JSONObject();
		try {
			interest.put("riderecord_id",ride_id);
			interest.put("hitchhiker", nusp_rider);
			interest.put("message", mensagem);
			wrapper.put("interestintheride", interest);
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		System.err.println(wrapper.toString());
		
		return wrapper.toString();
	}

}
