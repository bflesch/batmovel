package com.example.batmovel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ModeChooser extends Activity {

	public static final String SHARED_PREFS_NAME = "KINNEGAD";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_chooser);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onStart(){
		super.onStart();
		SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		String username = settings.getString("username", "usuário");

		((TextView) findViewById(R.id.greeting_area)).setText(username);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mode_chooser, menu);
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
	
	//TODO mudar o nome do metodo
	public void changeToRideList(View view){
		Intent intent = new Intent(this, RideListActivity.class);
		startActivity(intent);
	}

	public void changeToRideForm (View view) {
		Intent intent = new Intent(this, RideFormActivity.class);
		startActivity(intent);		
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
			View rootView = inflater.inflate(R.layout.fragment_mode_chooser,
					container, false);
			return rootView;
		}
	}

}
