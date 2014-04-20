package com.example.batmovel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
//import android.app.ActionBar;
//import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
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
import android.widget.TextView;
//import android.os.Build;

public class RideListActivity extends Activity {

	protected String JSONdata;
	protected JsonDownloader balacobaco;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			CaronasFragment cf = new CaronasFragment();
			getFragmentManager().beginTransaction().add(R.id.container, cf).commit();
			RideRecordListAdapter adapter = new RideRecordListAdapter();
			balacobaco = new JsonDownloader(adapter);
			cf.setListAdapter(adapter);
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		balacobaco.onDestroy();
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

		// Checks the network connection and sets the connected variable accordingly.
		private void updateConnectedFlag() {
			ConnectivityManager connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
			connected = (activeInfo != null && activeInfo.isConnected()); 
		}

		// Displays an error if the app is unable to load content.
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

		//Implementation of AsyncTask used to download XML feed from stackoverflow.com.
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

		JSONArray data = new JSONArray();
		boolean hasError = false;
		String errorMessage = "";
		

		public void setData(String json_string){
			try {
				JSONObject object = new JSONObject(json_string);
				this.data = object.getJSONArray("riderecordlist");
			}
			catch (JSONException e){
				e.printStackTrace();
			}
		}

		@Override
		public int getCount() {
			return data.length();
		}

		@Override
		public String getItem(int arg0) {
			try {
				return data.get(arg0).toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			try {
				return data.get(arg0).hashCode();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		public String getDestination(JSONObject record) throws JSONException {
			String destination;

			destination = record.getString("targetlocalization");

			return destination;
		}

		public String getInfo(JSONObject record) throws JSONException {
			String user;
			String origin;

			user = record.getString("login");
			origin = record.getString("actuallocalization");

			return user + ", out of " + origin;
		}

		public String getMessage(JSONObject record) throws JSONException {
			String message;

			message = record.getString("message");

			return message;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = ((LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_carona_list_item, container, false);
			}

			JSONObject record;
			try {
				record = data.getJSONObject(position).getJSONObject("riderecord");
				((TextView) convertView.findViewById(R.id.destination))
				.setText(getDestination(record));

				((TextView) convertView.findViewById(R.id.info))
				.setText(getInfo(record));

				((TextView) convertView.findViewById(R.id.message))
				.setText(getMessage(record));
			}
			catch (JSONException e ){
				e.printStackTrace();
			}

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

	public static class CaronasFragment extends ListFragment {
	}

}
