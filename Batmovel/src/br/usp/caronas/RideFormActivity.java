package br.usp.caronas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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

public class RideFormActivity extends Activity {
	
	final static String URL_POST = "http://uspservices.deusanyjunior.dj/carona";
	uploadJsonTask uploadTask = null;

	public int selected_hour;
	public int selected_minute;
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
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
		
		HitchhikingApplication app = (HitchhikingApplication) getApplication();
		app.startGPS();
		
		Calendar c = Calendar.getInstance(); 
		c.setLenient(true);
		c.add(Calendar.MINUTE, 10);
		selected_hour = c.get(Calendar.HOUR_OF_DAY);
		selected_minute = c.get(Calendar.MINUTE);
	
		setFormTime();

		
	}
	
	protected void onPause(){
		super.onPause();
		HitchhikingApplication app = (HitchhikingApplication) getApplication();
		app.stopGPS();
		
	}

	
	
	/* Carrega a ride salva anteriormente no disco. Seta o formulário e as variaveis internas*/
	public void loadRide(){
		
		Ride ride;
		HitchhikingApplication app = (HitchhikingApplication) getApplication();
		ride = app.loadRide();
		
		if (ride==null) {
			toast(getString(R.string.no_ride_yet));
			return;
		}
		
		putStringInTextViewId(R.id.destino,ride.local_chegada);
		putStringInTextViewId(R.id.mensagem,ride.message);
		putStringInTextViewId(R.id.origem,ride.local_partida);
		
		System.err.println(ride.departuretime);
		
		try {
			java.util.Date d = dateFormatter.parse(ride.departuretime);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			selected_hour = c.get(Calendar.HOUR_OF_DAY);
			selected_minute = c.get(Calendar.MINUTE);
			
			setFormTime();
		} catch (ParseException e) {
			System.err.println("isso não deve acontecer de jeito nenhum ... Só se houver uma string cagada SALVA LOCALMENTE ...");
			e.printStackTrace();
		}

	}
	
	public void saveRide(){
		Ride ride = buildRideFromForm();
		HitchhikingApplication app = (HitchhikingApplication) getApplication();
		app.saveRide(ride);	
		toast(getString(R.string.save_ok));
	}

	
	private void putStringInTextViewId(int id,String text){
		EditText editText = (EditText) findViewById(id);
		editText.setText(text);
		
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
		
		//Se horario indicado é antes do horario atual,
		// então o usuário quer dar carona no dia seguinte
		if (rightNow.compareTo(rideCal) == 1) {
			
			rideCal.add(Calendar.DAY_OF_MONTH, 1);
		}
		String dateIso = dateFormatter.format(rideCal.getTime());
		return dateIso;
	} 
	
	private void toast (String text){
		
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
	}
	
	private Ride buildRideFromForm(){
		Ride ride = new Ride();
		ride.local_chegada = textViewIdToString(R.id.destino); 
		ride.local_partida = textViewIdToString(R.id.origem);
		ride.message = textViewIdToString(R.id.mensagem);
		ride.departuretime = build_datetime();
		return ride;
	}
	
	public void sendRide(View view){
		
        Ride ride = buildRideFromForm();

		User user = User.getCurrentUser(getApplicationContext());
		
		ride.n_usp = user.uspNumber;
		ride.login = user.stoaLogin;
		
		if (ride.local_chegada.equals(""))
			toast(getString(R.string.no_destination));
		else if (ride.local_partida.equals(""))
			toast(getString(R.string.no_origin));
		else {
            toast (getString(R.string.sending_ride));
    		if (uploadTask != null)
    		    uploadTask.cancel(true);
			uploadTask = new uploadJsonTask();
			uploadTask.execute(ride.toJsonString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ride_form, menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void getGPS(){
		HitchhikingApplication app = (HitchhikingApplication) getApplication();
		String coords = app.getCoords();
		if (coords == null)
			toast("não foi possivel pegar a posição.\n O GPS está ligado ?");
		else 
		    putStringInTextViewId(R.id.origem,app.getCoords());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.load_ride_menu_item:
	            loadRide();
	            System.err.println("carregar!");
	            return true;
	        case R.id.save_ride_menu_item:
	            saveRide(); System.err.println("salvar!");
	            return true;
	        case R.id.get_GPS_location:
	        	getGPS();
	        	return true;
	        case R.id.action_logout:
				User.logout(getApplicationContext());
				Intent intent = new Intent(this, ModeChooser.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if (uploadTask != null) {
		    uploadTask.cancel(true);
		    toast(getString(R.string.ride_sending_cancelled));
		}
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
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
        	uploadTask = null;
        		
        }
    }


}
