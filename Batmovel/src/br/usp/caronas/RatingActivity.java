package br.usp.caronas;

import java.util.ArrayList;

import com.example.batmovel.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class RatingActivity extends ListActivity {

	public static final String URL_POST_RATING = "http://uspservices.deusanyjunior.dj/avaliacaodousuario";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
		RatingListAdapter rla = new RatingListAdapter();
		rla.setData(null);
		setListAdapter(rla);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rating, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			User.logout(getApplicationContext());
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		User u = (User) l.getItemAtPosition(position);
		RatingManager.prepareToRate(u);
		((TextView) findViewById(R.id.rated_user)).setText("Avaliando " + u.stoaLogin);
	}


	public void attemptRating(View v){
		AttemptRatingTask attempter = new AttemptRatingTask();
		RatingBar rb = (RatingBar) findViewById(R.id.rating_bar);
		float rating = rb.getRating();
		User currentUser = User.getCurrentUser(getApplicationContext());
		String jsonRating = RatingManager.getRatingJson(rating,currentUser);
		//attempter.execute(jsonRating);
		System.err.println(jsonRating); //TODO
	}
	
	private class AttemptRatingTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... json_is_in_zero) {
			WebClient wc = new WebClient(URL_POST_RATING);
			return wc.postJson(json_is_in_zero[0]);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			//TODO wait for acceptance or maybe go to chat
		}
	}
	

	public class RatingListAdapter extends BaseAdapter {

		ArrayList<User> data = new ArrayList<User>();
		boolean hasError = false;
		String errorMessage = "";

		public void setData (ArrayList<User> newData){
			//data = newData;
			data = new ArrayList<User>();
			User user = new User();
			user.uspNumber = "1234567";
			user.stoaLogin = "ahhah";
			data.add(user);
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public User getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return data.get(arg0).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = ((LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_rating_list_item, container, false);
			}

			User reviewCandidate = data.get(position);

			((TextView) convertView.findViewById(R.id.username))
			.setText(reviewCandidate.stoaLogin);

			((TextView) convertView.findViewById(R.id.message))
			.setText(reviewCandidate.uspNumber);

			return convertView;
		}
	}
}
