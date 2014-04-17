package com.example.batmovel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new CaronasFragment()).commit();
		}			
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

	
	public static class CaronasFragment extends ListFragment {
		
		@Override
		public void onStart(){
			super.onStart();
			JSONObject object;
			JSONArray array = null;
			try {
				object = new JSONObject("{\"riderecordlist\":[{\"riderecord\":{\"route\":\"\",\"created_at\":\"2014-04-16T16:12:34-03:00\",\"actuallongitude\":null,\"updated_at\":\"2014-04-16T16:12:34-03:00\",\"targetlocalization\":\"easy_living\",\"id\":1,\"departuretime\":\"2014-09-19T20:55:00-03:00\",\"actuallatitude\":null,\"actuallocalization\":\"casa_da_tia_do_batima\",\"targetlongitude\":null,\"driver\":5177188,\"targetlatitude\":null,\"message\":\"huhuhu\",\"login\":\"coringa\"}},{\"riderecord\":{\"route\":\"\",\"created_at\":\"2014-04-16T16:23:49-03:00\",\"actuallongitude\":null,\"updated_at\":\"2014-04-16T16:23:49-03:00\",\"targetlocalization\":\"etherea\",\"id\":2,\"departuretime\":\"2014-09-19T20:55:00-03:00\",\"actuallatitude\":null,\"actuallocalization\":\"eternia\",\"targetlongitude\":null,\"driver\":5177188,\"targetlatitude\":null,\"message\":\"sempre tem dente de coelho\",\"login\":\"he-man\"}}]}");
				array = object.getJSONArray("riderecordlist");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        setListAdapter(new MyAdapter(array));
		}
		
		public class MyAdapter extends BaseAdapter {
			
			JSONArray data;
			
			public MyAdapter(JSONArray ja){
				this.data = ja;
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
					convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_carona_list_item, container, false);
				}
				
				JSONObject record;
				try {
					record = data.getJSONObject(position).getJSONObject("riderecord");
				} catch (JSONException e) {
					e.printStackTrace();
					record = null;
				}
				
				try {
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
			
		}
	}

}
