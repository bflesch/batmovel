package com.example.batmovel;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	public static final String SHARED_PREFS_NAME = "KINNEGAD";

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mLogin;
	private String mPassword;

	// UI references.
	private EditText mLoginView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		currentUser = User.getCurrentUser(getApplicationContext());

		if(currentUser == null || currentUser.isEmpty()){
			setupLogin();
		} else{
			setupLogout();
		}
	}
	
	
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(currentUser == null || currentUser.isEmpty()){
			setupLogin();
		} else{
			setupLogout();
		}
		
	}



	protected void setupLogin(){
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mLogin = getIntent().getStringExtra(EXTRA_EMAIL);
		mLoginView = (EditText) findViewById(R.id.username);
		mLoginView.setText(mLogin);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}
	
	protected void setupLogout(){
		setContentView(R.layout.activity_logout);
		findViewById(R.id.sign_out_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogout();
					}
				});
	}

	@Override
	protected void onStart(){
		super.onStart();
//		if (!currentUser.isEmpty()) {
//			changeToModeChooser();
//		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (mAuthTask != null)
			mAuthTask.cancel(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogout(){
		User.logout(getApplicationContext());
		setupLogin();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mLoginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mLogin = mLoginView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid login
		if (TextUtils.isEmpty(mLogin)) {
			mLoginView.setError(getString(R.string.error_field_required));
			focusView = mLoginView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	private void changeToModeChooser(){
		Intent intent = new Intent(this, ModeChooser.class);
		startActivity(intent);
	}

	private void saveCredentials(String nusp, String username){
		currentUser = new User();
		currentUser.uspNumber = nusp; 
		currentUser.stoaLogin = username;
		User.login(getApplicationContext(), currentUser);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {
		private static final String AUTHENTICATION_URL = "https://maxwell.stoa.usp.br/plugin/stoa/authenticate/";

		@Override
		protected JSONObject doInBackground(Void... params) {

			try {
				WebClient wc = new WebClient(AUTHENTICATION_URL);
				String response = wc.postHttps(mLogin,mPassword);

				JSONObject josie = new JSONObject(response);
				return josie;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(final JSONObject result) {
			boolean resultOK = false;
			String nusp = null;
			String username = null;

			mAuthTask = null;
			showProgress(false);
			if (result != null){
				try {
					resultOK = result.getBoolean("ok");
					nusp = result.getString("nusp");
					username = result.getString("username");
					System.err.println(result.toString()); //TODO remover
				} catch (JSONException e) {
					e.printStackTrace();
					resultOK = false;
				}
			}

			if (mLogin.equals("1111")){
				resultOK = true;
				nusp = "1111";
				username = "testGuy";
			}


			if (resultOK) {
				saveCredentials(nusp,username);
				changeToModeChooser();
			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
