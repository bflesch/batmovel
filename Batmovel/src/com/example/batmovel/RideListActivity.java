package com.example.batmovel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
//import android.app.ActionBar;
//import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
//import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
//import android.os.Build;

public class RideListActivity extends ListActivity {

	protected String JSONdata;
	protected JsonDownloader downloader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_list);

		if (savedInstanceState == null) {
			RideRecordListAdapter adapter = new RideRecordListAdapter();
			setListAdapter(adapter);
			downloader = new JsonDownloader(adapter);
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		downloader.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void notifyUpdate(String str){
		this.JSONdata = str;
	}
	
	public void confirmHitchhike(Ride r){
		RideInterest interest = new RideInterest(r);
		interest.hitchhiker = getCurrentUser().uspNumber;
	}

	private User getCurrentUser() {
		return ((HitchhikingApplication) getApplication()).getCurrentUser();
	}
	
	public class JsonDownloader {
		private static final String URL =
				"http://uspservices.deusanyjunior.dj/carona/3.json";

		// Whether there is an internet connection.
		private boolean connected = false;

		// did we ever get a list or rides ?
		private boolean someInfo = false;

		// time of last update
		private long lastUpdate = 0;

		private static final int staleLimit = 60;

		private RideRecordListAdapter adapter;
		private Timer timer;

		public JsonDownloader(RideRecordListAdapter adapter) {
			this.adapter = adapter;
			timer = new Timer();
			timer.scheduleAtFixedRate(new UpdateTask(), 0, 1000*10 /*miliseconds*/);
		}

		public void onDestroy() {
			if (timer != null)
				timer.cancel();
		}

		private String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}

		private String downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			InputStream stream = conn.getInputStream();
			//TODO maybe think in terms of streams ?
			String response = convertStreamToString(stream);
			stream.close();
			return response;
		}

		private void updateConnectedFlag() {
			ConnectivityManager connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
			connected = (activeInfo != null && activeInfo.isConnected()); 
		}

		private void showError() {
			adapter.notifyError(R.string.connection_error); 
		}

		private void noStale() {
			adapter.clearError();
		}

		private void showStale(int staleTime) {
			//TODO quantos segundos sao stale ?
			adapter.notifyError("Dados não atualizados desde:" + staleTime);
			adapter.notifyDataSetChanged();
		}

		private class DownloadJsonTask extends AsyncTask<String, Void, String> {

			@Override 
			protected String doInBackground(String... urls) {
				try {
					return downloadUrl(urls[0]);
				} catch (IOException e) {
					//TODO O que acontece se a conexão der pau no meio ?
					adapter.notifyError(R.string.connection_error);
					adapter.setData("");
					adapter.notifyDataSetChanged();
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				adapter.setData(result);
				adapter.notifyDataSetChanged();
			}
		}

		//TODO parar quando a task nao estiver visivel
		class UpdateTask extends TimerTask {

			final static long NANOSECONDS_IN_A_SECOND = 1000*1000*1000;

			public void run() {

				updateConnectedFlag();

				if (connected) {
					someInfo = true;
					noStale();
					lastUpdate = (long) (System.nanoTime()/(NANOSECONDS_IN_A_SECOND));
					new DownloadJsonTask().execute(URL);
				}   else {
					if (someInfo) {
						long currTime = (long) (System.nanoTime()/(NANOSECONDS_IN_A_SECOND));
						if (currTime - lastUpdate > staleLimit)
							showStale((int) (currTime - lastUpdate));
					}
					else {
						showError();
					}
				}
			}
		}
	}

	public class RideRecordListAdapter extends BaseAdapter {

		JSONArray jsonData = new JSONArray();
		ArrayList<Ride> data = new ArrayList<Ride>();
		boolean hasError = false;
		String errorMessage = "";


		public void setData(String json_string){
			try {
				JSONObject object = new JSONObject(json_string);
				this.jsonData = object.getJSONArray("riderecordlist");
				this.data = new ArrayList<Ride>();
				for(int i=0; i<jsonData.length(); i++){
					data.add(new Ride(jsonData.getJSONObject(i).toString()));
				}
			}
			catch (JSONException e){
				e.printStackTrace();
			}
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

		public void notifyError(String errorMessage) {
			this.hasError = true;
			this.errorMessage = errorMessage;	
		}

		public void clearError() {
			this.errorMessage = "";
		}

		public void notifyError(int errorStringNumber) {
			notifyError(getResources().getString(errorStringNumber));
		}

	}

	public static class ConfirmHitchhikeDialogFragment extends DialogFragment {
		private CharSequence message;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(this.message)
			.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			})
			.setNegativeButton("Não", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			return builder.create();
		}

		public void setPayload(Ride r) {
			this.message = "Você aceita a carona para " + r.local_chegada + " oferecida por " + r.n_usp + "?";
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		ConfirmHitchhikeDialogFragment f = new ConfirmHitchhikeDialogFragment();
		f.setPayload((Ride) l.getItemAtPosition(position));
		(f).show(getFragmentManager(), "Brocolis");
	}
}
