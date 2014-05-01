package br.usp.caronas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

public class Review {

       String other_nusp;//"userid":"numero_usp_de_quem_voce_vai_avaliar",
       String other_login; 	   //    "userlogin":"login_de_quem_voce_vai_avaliar",
       String judge_nusp;       //    "judgeid":"numero_usp_do_avaliador",
       String judge_login;       //    "judgelogin":"login_do_avaliador",
       int stars;       //    "stars":"quantidade_de_estrelas_ou_nota",
       String message;       //    "message":"mensagem"}
   
	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");


	public Review(){
	}
	
	public Review(String jsonString) {
		fromJsonString(jsonString);
	}

	static public ArrayList<Review> listFromJsonList(JSONObject jlist){
		try {
			JSONArray jsonData = new JSONArray();
			ArrayList<Review> data = new ArrayList<Review>();
			jsonData = jlist.getJSONArray("reviewlist");
			for(int i=0; i<jsonData.length(); i++){
				Review review = new Review(jsonData.getJSONObject(i).toString());
				data.add(review);
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
			object = object.getJSONObject("reviewuser");
			this.other_nusp = object.getString("userid");
			this.other_login = object.getString("userlogin");
			this.judge_nusp = object.getString("judgeid");
			this.judge_login = object.getString("judgelogin");
			this.stars = object.getInt("stars");
			this.message = object.getString("message");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
    // TODO breno, use e diga se funciona =P
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		JSONObject recordJson = new JSONObject();
		try {
			recordJson.put("userid", other_nusp);
			recordJson.put("userlogin", other_login);
			recordJson.put("judgeid", judge_nusp);
			recordJson.put("judgelogin", judge_login);
			recordJson.put("stars", stars);
			recordJson.put("message", message);
			json.put("reviewuser",recordJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String toJsonString(){
		return this.toJSONObject().toString();
	}
}
