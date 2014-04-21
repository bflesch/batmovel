package com.example.batmovel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
	
	final static String URL_POST = "http://uspservices.deusanyjunior.dj/carona";
	
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
		Ride ride = new Ride(true); //TODO remover booleano
		//TODO departure time
		ride.local_chegada = textViewIdToString(R.id.destino); 
		ride.local_partida = textViewIdToString(R.id.origem);
		ride.message = textViewIdToString(R.id.mensagem);
		new uploadJsonTask().execute(ride.toJsonString());
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

    private class uploadJsonTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... json_is_in_zero) {
    		//TODO funcionou ou não, jacaré ?
    			WebClient wc = new WebClient(URL_POST);
    			wc.postJson(json_is_in_zero[0]);
    			return true;
        }

        //TODO seria legal usar para alguma coisa...
        @Override
        protected void onPostExecute(Boolean result) {
        }
    }


}
