package com.example.batmovel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

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
import android.widget.TextView;
import android.os.Build;

public class RideFormActivity extends Activity {
	
	uploadJsonTask uploadTask = null;
	
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
	
	@Override
	protected void onStart() {
		super.onStart();
		TextView date = (TextView) findViewById(R.id.ScrollView01).findViewById(R.id.tempo_de_partida);
		Calendar c = Calendar.getInstance(); 
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String timeS = ""+hour+":"+minute;
		date.setText(timeS);
		//TODO escrever o numero de maneira nao escrota
	}

	protected String textViewIdToString(int id){
		  EditText editText = (EditText) findViewById(id);
          return editText.getText().toString();
	}
	//TODO less ugly form
	public void sendRide(View view){
		Ride ride = new Ride(true); //TODO remover booleano
		//TODO departure time
		ride.local_chegada = textViewIdToString(R.id.destino); 
		ride.local_partida = textViewIdToString(R.id.origem);
		ride.message = textViewIdToString(R.id.mensagem);
		uploadTask = new uploadJsonTask();
		uploadTask.execute(ride.toJsonString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ride_form, menu);
		return true;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if (uploadTask != null)
		    uploadTask.cancel(true);
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
    			
    			con.disconnect();
    			return "true";
         	} catch (Exception e) {
    			return "false";
    		}         
        }
        //TODO spinning for idleness
        //TODO notificar sucesso
        //TODO deixar os campos menos feios
        @Override
        protected void onPostExecute(String result) {
        }
    }


}
