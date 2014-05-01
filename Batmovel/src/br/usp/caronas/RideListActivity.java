package br.usp.caronas;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class RideListActivity extends ListActivity {

	protected JsonDownloader downloader;

	final static String URL_POST = "http://uspservices.deusanyjunior.dj/interesseemcarona";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_list);
		RideRecordListAdapter adapter = new RideRecordListAdapter();
		setListAdapter(adapter);
		downloader = new JsonDownloader(adapter);
		if(savedInstanceState != null){
			downloader.onRestoreInstanceState(savedInstanceState);
		}
		downloader.start();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
        downloader.start();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if (downloader != null)
			downloader.stop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(downloader != null)
			downloader.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			User.logout(getApplicationContext());
			Intent intent = new Intent(this, ModeChooser.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void toast (String text){
		
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
	}
	
	public class JsonDownloader {
		private static final String URL =
				"http://uspservices.deusanyjunior.dj/carona/3.json";
		public final static long NANOSECONDS_IN_A_SECOND = 1000*1000*1000;

		// Whether there is an internet connection.
		private boolean connected = false;

		// did we ever get a list or rides ?
		private boolean someInfo = false;

		// time of last update
		private long lastUpdate = 0;

		private long staleTime = 0;

		//número de segundos até os dados perderem a validade
		private static final int STALE_LIMIT = 15;
		private static final String LAST_UPDATE_KEY = "last_update_at";
		private static final String SOME_INFO_KEY = "info_received";

		private RideRecordListAdapter adapter;
		private Timer timer;

		public JsonDownloader(RideRecordListAdapter adapter) {
			this.adapter = adapter;
			this.timer = new Timer();
		}

		public void start(){
			timer.scheduleAtFixedRate(new UpdateTask(), 0, 10*1000 /*miliseconds*/);
		}

		public void onRestoreInstanceState(Bundle state) {
			lastUpdate = state.getLong(LAST_UPDATE_KEY, 0);
			someInfo = state.getBoolean(SOME_INFO_KEY,false);
		}

		public void onSaveInstanceState(Bundle outState) {
			outState.putLong(LAST_UPDATE_KEY, lastUpdate);
			outState.putBoolean(SOME_INFO_KEY, someInfo);
		}

		public void stop() {
			if (timer != null)
				timer.cancel();
		}

		private void updateConnectedFlag() {
			ConnectivityManager connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
			connected = (activeInfo != null && activeInfo.isConnected()); 
		}

		protected void showStale() {
			setError("Faz " + staleTime + " segundos que não consigo atualizar as caronas");
		}

		protected void checkStale() {
			long currTime = System.nanoTime()/(NANOSECONDS_IN_A_SECOND);
			staleTime = (currTime - lastUpdate);
			if (staleTime > STALE_LIMIT)
				showStale();
		}

		private void setError(String message){
			TextView errorView = (TextView) findViewById(R.id.error);
			errorView.setText(message);
		}

		private class DownloadJsonTask extends AsyncTask<String, Void, JSONObject> {

			String error = null;

			@Override 
			protected JSONObject doInBackground(String... urls) {
				
				
				//TODO remover
	        	RatingManager ratings = new RatingManager();
	        	System.err.println("----------");
	        	ArrayList<User> pending = ratings.pendingReviews(new User("1111","marcos"));
	        	System.err.println("the pending reviews for 1111:");
	        	if (pending == null)
	        		System.err.println("could not be downloaded");
	        	else {
	        		for (int i = 0;i<pending.size();i++) {
	        			User user = pending.get(i);
	        			System.err.println(user.uspNumber+" is pending");
	        		}
	        	}
	        	System.err.println("----------");
	        	
	        	System.err.println("5177188 avaliou 1111");
	        	System.err.println(ratings.numberOfReviews("5177188", "1111"));
	        	System.err.println("72... avaliou 1111");
	        	System.err.println(ratings.numberOfReviews("7261561", "1111"));
	        	System.err.println("111 avaliou 1111");
	        	System.err.println(ratings.numberOfReviews("111", "1111"));
	        	
	        	System.err.println("----------");
	        	//TODO remover outros println
				
				WebClient wc = new WebClient(urls[0]);
				JSONObject jsonResponse = wc.getJson();
				if(jsonResponse == null) { 
					this.error = getResources().getString(R.string.connection_error);
				}
				return jsonResponse;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				if (error == null){ //tudo ocorreu bem
					TextView emptyView = (TextView) findViewById(android.R.id.empty);
					someInfo = true;
					lastUpdate = System.nanoTime()/(NANOSECONDS_IN_A_SECOND);
					setError("Caronas estão atualizadas");
					emptyView.setText("Não há caronas disponíveis");
					adapter.setData(result);
					adapter.notifyDataSetChanged();
				}
				else if (someInfo){ //erro, mas já temos dados. Avisar que estão vencidos
					long currTime = System.nanoTime()/(NANOSECONDS_IN_A_SECOND);
					staleTime = currTime - lastUpdate;
					if ( staleTime > STALE_LIMIT)
						showStale();
				}
				else { //erro, não temos dados. Mostrar que não temos dados
					TextView emptyView = (TextView) findViewById(android.R.id.empty);
					setError("Sem dados");
					emptyView.setText(error);
					adapter.setData(result);
					adapter.notifyDataSetChanged();
				}
			}
		}

		class UpdateTask extends TimerTask {
			public void run() {
				System.err.println("ran");//TODO caralho pq demora tanto ?? O ran aparece mó rapido
				updateConnectedFlag();
				if (connected) {
					new DownloadJsonTask().execute(URL);
				}
				else {
					long currTime = System.nanoTime()/(NANOSECONDS_IN_A_SECOND);
					staleTime = (currTime - lastUpdate);
					if (someInfo){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								checkStale();
							}
						});
					}
				}
			}
		}
	}

	public class RideRecordListAdapter extends BaseAdapter {

		ArrayList<Ride> data = new ArrayList<Ride>();
		boolean hasError = false;
		String errorMessage = "";

		public boolean setData (JSONObject object){
			data = Ride.listFromJsonList(object);
			
			if (data == null)
				return false;
			
			for(int i=(data.size()-1); i>=0; i--){
				Ride ride = data.get(i);
				if (ride.isGone()){
					data.remove(i);
				}
			}
			
			return true;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Ride getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return data.get(arg0).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = ((LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_carona_list_item, container, false);
			}

			Ride ride = data.get(position);
			String driverName;

			if ((ride.login != null) && (!ride.login.isEmpty()))
				driverName = ride.login;
			else
				driverName = ride.n_usp;

			((TextView) convertView.findViewById(R.id.destination))
			.setText(ride.local_chegada);

			((TextView) convertView.findViewById(R.id.info))
			.setText(driverName + ", saindo de " + ride.local_partida);

			((TextView) convertView.findViewById(R.id.message))
			.setText(ride.message);

			return convertView;
		}
	}

	public static class ConfirmHitchhikeDialogFragment extends DialogFragment {
		private CharSequence message;
		private RideListActivity parentRideList;
		private Ride boundRide;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(this.message)
			.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					parentRideList.sendInterestForRide(boundRide);
				}
			})
			.setNegativeButton("Não", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//nao fazer nada, queremos so cancelar o dialogo
				}
			});
			return builder.create();
		}

		public void setPayload(Ride r, RideListActivity rla) {
			this.message = "Você aceita a carona para " + r.local_chegada + " oferecida por " + r.login + "?";
			this.boundRide = r;
			this.parentRideList = rla;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		Ride r = (Ride) l.getItemAtPosition(position);
		ConfirmHitchhikeDialogFragment f = new ConfirmHitchhikeDialogFragment();
		f.setPayload(r,this);
		(f).show(getFragmentManager(), "Brocolis");
	}

	protected void sendInterestForRide(Ride boundRide) {
		Interest rideInterest = new Interest(boundRide);
		rideInterest.nusp_rider = User.getCurrentUser(getApplicationContext()).uspNumber;
		rideInterest.mensagem = "quero ir com você!";
		UploadJsonTask uploader = new UploadJsonTask();
		uploader.execute(rideInterest.toJsonString());
		//TODO spinning for idleness
	}

	private class UploadJsonTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... json_is_in_zero) {
			
			//TODO funcionou?
			WebClient wc = new WebClient(URL_POST);
			//returns true if sucess
			return wc.postJson(json_is_in_zero[0]);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
				toast(getResources().getString(R.string.interest_sent));
			else
				toast(getResources().getString(R.string.interest_not_sent));
		}
	}
}
