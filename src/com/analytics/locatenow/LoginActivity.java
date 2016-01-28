package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	EditText phone, password;
	Button login, register;
	Activity main_act;
	TextView locateHeading;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		locateHeading = (TextView) findViewById(R.id.txvLocateHeading);
		phone = (EditText) findViewById(R.id.edtPhone);
		password = (EditText) findViewById(R.id.edtPassword);
		login = (Button) findViewById(R.id.btnSignIn);
		
		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		login.setTypeface(boldFont);
		locateHeading.setTypeface(boldFont);
		phone.setTypeface(mediumFont);
		password.setTypeface(mediumFont);

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (phone.getText().toString().isEmpty()) {
					phone.setError("Please fill this field");
					Toast.makeText(LoginActivity.this,
							"Please Enter phone number", Toast.LENGTH_LONG)
							.show();
				} else if (password.getText().toString().isEmpty()) {
					password.setError("Please fill this field");
					Toast.makeText(LoginActivity.this, "Please Enter password",
							Toast.LENGTH_LONG).show();
				} else
					UserLogin();
			}
		});

		// register.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent i = new Intent(LoginActivity.this,
		// RegisterActivity.class);
		// startActivity(i);
		// }
		// });
	}

	public void UserLogin() {

		new AsyncTask<Void, Void, String>() {
			ProgressDialog mProgressDialog;

			@SuppressWarnings("deprecation")
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(ApplicationData.serviceURL
						+ "login.php");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);

					nameValuePairs.add(new BasicNameValuePair("phone", phone
							.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("password",
							password.getText().toString()));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					StringBuffer sb = new StringBuffer("");
					String line = "";
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}
					in.close();
					Log.e("Login Data", "" + sb.toString());
					return sb.toString();
				} catch (Exception e) {
					Log.e("problem data setting", "" + e);
					return "";
				}

			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				try {

					mProgressDialog.dismiss();
					JSONObject object = new JSONObject(result.toString());
					Log.e("2", "2");
					JSONArray dataArr = object.getJSONArray("data");
					JSONObject dataOb = dataArr.getJSONObject(0);
					String msg = object.getString("msg");
					if (msg.equalsIgnoreCase("Success")) {
						String id = dataOb.getString("id");
						String name = dataOb.getString("name");
						String countrycode = dataOb.getString("countrycode");
						String password = dataOb.getString("password");
						String image = dataOb.getString("image");
						String phone = dataOb.getString("phone");
						String email = dataOb.getString("email");

						SharedPreferences mPrefs = getSharedPreferences(
								"LOGIN_DETAIL", MODE_PRIVATE);
						Editor edit = mPrefs.edit();
						edit.putString("USER_ID", id);
						edit.putString("NAME", name);
						edit.putString("COUNTRYCODE", countrycode);
						edit.putString("PASSWORD", password);
						edit.putString("IMAGE", image);
						edit.putString("PHONE", phone);
						edit.putString("EMAIL", email);
						edit.commit();

						/**** PUSH NOTIFICATION ****/
						// main_act = LoginActivity.this;
						// PushActivity p = new PushActivity();
						// p.pushNotiFication(main_act, id);
						new GcmRegistrationAsyncTask(LoginActivity.this)
								.execute();
						Intent i = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(i);
						finish();

					} else {
						Toast.makeText(getApplicationContext(),
								"Oopss! Login failure please try again",
								Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Oopss! Login failure please try again",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(LoginActivity.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please wait ..");
				mProgressDialog.show();

			}
		}.execute();
	}

}
