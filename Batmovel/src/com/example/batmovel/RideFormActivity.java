package com.example.batmovel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Build;

public class RideFormActivity extends Activity {
	//TODO campos obrigatorios
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_form);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	protected String textViewIdToString(int id){
		  EditText editText = (EditText) findViewById(id);
          return editText.getText().toString();
	}
	//TODO matar a thread
	public void sendRide(View view){
		RideData ride = new RideData();
		//TODO departure time
		ride.local_chegada = textViewIdToString(R.id.destino); 
		ride.local_partida = textViewIdToString(R.id.origem);
		ride.message = textViewIdToString(R.id.mensagem);
		JSONObject rideJ = ride.createJson();

		new uploadJsonTask().execute(rideJ.toString());
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ride_form, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_ride_form,
					container, false);
			return rootView;
		}
	}

	public static class RideData {
		//TODO des-hardecodear
		public String n_usp = "5177188"; /*no jason, driver*/
		public String login = "josinalvo"; /*nome_de_usuario_no_stoa */
		public String departuretime = "2014-04-19T23:55:00Z";
		public String local_partida = ""; /*no json, actuallocalization*/
		public String local_chegada = ""; /*no jason, targetlocalization*/
		public String message= "";
		
		//TODO exigir campos exigidos

		public JSONObject createJson() {
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
		// TODO pensar na interacao do usuario com essa componente
	}
	
    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class uploadJsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... json_is_in_zero) {
    		//TODO funcionou ou não, jacaré ?
    		String url="http://uspservices.deusanyjunior.dj/carona";
    		try {
    			URL object = new URL(url);
    			HttpURLConnection con = (HttpURLConnection) object.openConnection();
    			con.setDoOutput(true);
    			con.setRequestProperty("Content-Type", "application/json");
    			//con.setRequestProperty("Content-Length", ""+json_is_in_zero[0].length());
    			//con.setRequestMethod("POST");
    			OutputStream stream = con.getOutputStream();
    			OutputStreamWriter wr= new OutputStreamWriter(stream);
    			wr.write(json_is_in_zero[0]);
    			wr.flush();
    			wr.close();

    			BufferedReader in = new BufferedReader(
    			        new InputStreamReader(con.getInputStream()));
    			String inputLine;
    			StringBuffer response = new StringBuffer();
    	 
    			while ((inputLine = in.readLine()) != null) {
    				response.append(inputLine);
    			}
    			in.close();
    	 
    			//print result
    			System.err.println(response.toString());
    			//TODO verificar sucesso no envio
    			
    			con.disconnect();
    			return "true";
         	} catch (Exception e) {
    			return "false";
    		}         
        }

        //TODO seria legal usar para alguma coisa...
        @Override
        protected void onPostExecute(String result) {
        }
    }


}
