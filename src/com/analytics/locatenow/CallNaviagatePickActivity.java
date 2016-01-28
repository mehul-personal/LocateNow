package com.analytics.locatenow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CallNaviagatePickActivity extends Activity {
	ImageButton imbcall, imbnavigate, imbchat;
	TextView txvcall, txvnavigate, txvchat, headertitle;
	static String clat = "", clog = "", slat = "", slog = "", phone = "",
			to_id = "", from_id = "", share_id = "", user_name = "";
	LinearLayout llnavigate;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_action);
		imbcall = (ImageButton) findViewById(R.id.imbCall);
		imbnavigate = (ImageButton) findViewById(R.id.imbNavigate);
		imbchat = (ImageButton) findViewById(R.id.imbChat);
		llnavigate = (LinearLayout) findViewById(R.id.llNavigate);
		headertitle = (TextView) findViewById(R.id.txvPickActionHeader);
		txvcall = (TextView) findViewById(R.id.txvCall);
		txvnavigate = (TextView) findViewById(R.id.txvNavigate);
		txvchat = (TextView) findViewById(R.id.txvChat);

		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");

		headertitle.setTypeface(boldFont);
		txvcall.setTypeface(mediumFont);
		txvnavigate.setTypeface(mediumFont);
		txvchat.setTypeface(mediumFont);

		Intent i = getIntent();
		if (i.getStringExtra("STATUS").equalsIgnoreCase("TRACK")) {
			llnavigate.setVisibility(View.GONE);
		} else {
			llnavigate.setVisibility(View.VISIBLE);
		}
		clat = i.getStringExtra("CURRENT_LAT");
		clog = i.getStringExtra("CURRENT_LOG");
		slat = i.getStringExtra("SHARE_LAT");
		slog = i.getStringExtra("SHARE_LOG");
		phone = i.getStringExtra("PHONE");
		to_id = i.getStringExtra("TO_USER_ID");
		from_id = i.getStringExtra("FROM_ID");
		share_id = i.getStringExtra("SHARE_ID");
		user_name = i.getStringExtra("USER_NAME");
		imbchat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(CallNaviagatePickActivity.this,
						ChatRoomDetails.class);
				i.putExtra("STATUS", "USER");
				i.putExtra("TO_ID", to_id);
				i.putExtra("FROM_ID", from_id);
				i.putExtra("SHARE_ID", share_id);
				i.putExtra("CHAT_USERNAME", user_name);
				startActivity(i);
				finish();
			}
		});
		txvchat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(CallNaviagatePickActivity.this,
						ChatRoomDetails.class);
				i.putExtra("STATUS", "USER");
				i.putExtra("TO_ID", to_id);
				i.putExtra("FROM_ID", from_id);
				i.putExtra("SHARE_ID", share_id);
				i.putExtra("CHAT_USERNAME", user_name);
				startActivity(i);
				finish();
			}
		});
		imbcall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent(Intent.ACTION_DIAL);

					intent.setData(Uri.parse("tel:" + phone));
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(CallNaviagatePickActivity.this,
							"Sorry! No dial application found",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		txvcall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent(Intent.ACTION_DIAL);

					intent.setData(Uri.parse("tel:" + phone));
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(CallNaviagatePickActivity.this,
							"Sorry! No dial application found",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		imbnavigate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr=" + clat
								+ "," + clog + "&daddr=" + slat + "," + slog));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(intent);
			}
		});
		txvnavigate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr=" + clat
								+ "," + clog + "&daddr=" + slat + "," + slog));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(intent);
			}
		});
	}
}
