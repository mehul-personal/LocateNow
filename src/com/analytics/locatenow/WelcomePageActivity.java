package com.analytics.locatenow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WelcomePageActivity extends Activity {
	Button Signin,register;
	TextView heading;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wc_login_register);
		Signin=(Button) findViewById(R.id.btnLogin);
		register=(Button)findViewById(R.id.btnRegister);
		heading=(TextView)findViewById(R.id.txvLocateHeading);
		
		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		Signin.setTypeface(boldFont);
		register.setTypeface(boldFont);
		heading.setTypeface(boldFont);
		
		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		if (!mPrefs.getString("USER_ID", "").isEmpty()) {
			Intent i = new Intent(WelcomePageActivity.this,
					MainActivity.class);
			
			startActivity(i);
			finish();
		} else {

//			Intent i = new Intent(WelcomePageActivity.this,
//					LoginActivity.class);
//			i.putExtra("USER_TYPE", "SELLER");
//			startActivity(i);
//			finish();

		}
		Signin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(WelcomePageActivity.this, LoginActivity.class);
				startActivity(i);
			}
		});
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(WelcomePageActivity.this, RegisterActivity.class);
				startActivity(i);
			}
		});
	}
}
