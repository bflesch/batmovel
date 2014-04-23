package com.example.batmovel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.os.Build;

public class RideFormActivity extends Activity {
	
	final static String URL_POST = "http://uspservices.deusanyjunior.dj/carona";
	uploadJsonTask uploadTask = null;
	
	//TODO e se o usuario cancelar o envio no meio ?

	public int selected_hour;
	public int selected_minute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_form);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	private String fill (int n) {
		if (n < 10)
			return "0"+n;
		return ""+n;
	}
	
	private void setFormTime () {
		TextView date = (TextView) findViewById(R.id.ScrollView01).findViewById(R.id.tempo_de_partida);
		String hourS = fill(selected_hour);
		String minuteS = fill(selected_minute);
		String timeS = ""+hourS+":"+minuteS;
		date.setText(timeS);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Calendar c = Calendar.getInstance(); 
		c.setLenient(true);
		c.add(Calendar.MINUTE, 10);
		selected_hour = c.get(Calendar.HOUR_OF_DAY);
		selected_minute = c.get(Calendar.MINUTE);
	
		setFormTime();

		
	}

	private String textViewIdToString(int id){
		  EditText editText = (EditText) findViewById(id);
          return editText.getText().toString();
	}
	
	private String build_datetime(){
		Calendar rideCal = Calendar.getInstance();
		rideCal.setLenient(true);
		rideCal.set(Calendar.HOUR_OF_DAY,selected_hour);
		rideCal.set(Calendar.MINUTE,selected_minute);
		Calendar rightNow = Calendar.getInstance();
		
		//Se horario indicado é antes do horario atual (hopefully)
		if (rightNow.compareTo(rideCal) == 1) {
			
			rideCal.add(Calendar.DAY_OF_MONTH, 1);
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		String dateIso = df.format(rideCal.getTime());
		return dateIso;
	} 
	
	private void toast (String text){
		
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
	}
	
	public void sendRide(View view){
		Ride ride = new Ride(true); //TODO remover booleano
		ride.local_chegada = textViewIdToString(R.id.destino); 
		ride.local_partida = textViewIdToString(R.id.origem);
		ride.message = textViewIdToString(R.id.mensagem);
		ride.departuretime = build_datetime();

		
		if (ride.local_chegada.equals(""))
			toast(getString(R.string.no_destination));
		else if (ride.local_partida.equals(""))
			toast(getString(R.string.no_origin));
		else {
            toast (getString(R.string.sending_ride));
			uploadTask = new uploadJsonTask();
			uploadTask.execute(ride.toJsonString());
		}
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
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
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


	public static class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Create a new instance of TimePickerDialog and return it
			RideFormActivity parentActivity = (RideFormActivity) getActivity();
			return new TimePickerDialog(parentActivity, this, parentActivity.selected_hour, parentActivity.selected_minute,
					DateFormat.is24HourFormat(parentActivity));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			RideFormActivity parentActivity = (RideFormActivity) getActivity();
			parentActivity.selected_hour = hourOfDay;
			parentActivity.selected_minute = minute;
			parentActivity.setFormTime();
		}
	}
	
	
	
    private class uploadJsonTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... json_is_in_zero) {
    			WebClient wc = new WebClient(URL_POST);
    			boolean result = wc.postJson(json_is_in_zero[0]);
    			return result;
        }
        @Override
        protected void onPostExecute(Boolean result) {
        	if (result) {
        		//TODO adicionar spinning para o usuario ficar mais feliz
        		toast (getString(R.string.ride_sent));
        		finish();
        	}
        	else 
        		toast (getString(R.string.send_failed));
        		
        }
    }


}
