package br.usp.caronas;

import java.util.ArrayList;
import java.util.Timer;

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
import android.widget.Toast;

public class RatingActivity extends ListActivity {

	public static final String URL_POST_RATING = "http://uspservices.deusanyjunior.dj/avaliacaodousuario";
	private Timer timer;
	private RatingListAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
		adapter = new RatingListAdapter();
		setListAdapter(adapter);
		(new UpdateRatingListTask()).execute();
	}
	
	public class UpdateRatingListTask extends AsyncTask<Void, Void, ArrayList<User>> {
			
		public ArrayList<User> doInBackground(Void... voids){
			return (new RatingManager()).pendingReviews(User.getCurrentUser(getApplicationContext()));
		}
		
		public void onPostExecute(ArrayList<User> usersToRate){
			if (usersToRate == null){
				TextView tv = (TextView) findViewById(android.R.id.empty);
				tv.setText(R.string.connection_error);
			}
			else if (usersToRate.isEmpty()){
				TextView tv = (TextView) findViewById(android.R.id.empty);
				tv.setText(R.string.no_ratings_available);
			}
			else {
				adapter.setData(usersToRate);
				adapter.notifyDataSetChanged();
			}
		}
		
	};
	
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null)
			timer.cancel();
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
		attempter.execute(jsonRating);
	}
	
	private void toast(String text){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private class AttemptRatingTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... json_is_in_zero) {
			WebClient wc = new WebClient(URL_POST_RATING);
			return wc.postJson(json_is_in_zero[0]);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result){
				toast("Avaliação enviada com sucesso");
				(new UpdateRatingListTask()).execute();
			} else {
				toast("Erro ao enviar avaliação. Tente novamente.");
			}
		}
	}
	

	public class RatingListAdapter extends BaseAdapter {

		ArrayList<User> data = new ArrayList<User>();
		boolean hasError = false;
		String errorMessage = "";

		public void setData (ArrayList<User> newData){
			data = newData;
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
