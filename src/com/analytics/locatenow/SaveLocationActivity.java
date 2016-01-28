package com.analytics.locatenow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SaveLocationActivity extends Activity {
	EditText locationName, setLatitude, setLongitude;
	Button save, setLocation;
	static String location_id = "", status = "";
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
TextView saveheading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_location);

		// ActionBar actionBar = getActionBar();
		// actionBar.setBackgroundDrawable(new
		// ColorDrawable(getResources().getColor(R.color.blue_header)));

		locationName = (EditText) findViewById(R.id.edtSaveLocationName);
		setLatitude = (EditText) findViewById(R.id.edtLatitude);
		setLongitude = (EditText) findViewById(R.id.edtLongitude);
		setLocation = (Button) findViewById(R.id.btnTakeLocation);
		save = (Button) findViewById(R.id.btnSaveLocation);
		saveheading=(TextView)findViewById(R.id.txvSaveLocationHeading);
		save.setVisibility(View.GONE);

		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		saveheading.setTypeface(boldFont);
		locationName.setTypeface(boldFont);
		setLocation.setTypeface(boldFont);
		save.setTypeface(boldFont);
		locationName.setTypeface(regularFont);
		setLatitude.setTypeface(regularFont);
		setLongitude.setTypeface(regularFont);

		Intent i = getIntent();
		status = i.getStringExtra("STATUS");
		if (status.equalsIgnoreCase("UPDATE")) {
			location_id = i.getStringExtra("ID");
			locationName.setText(i.getStringExtra("NAME"));
			setLatitude.setText(i.getStringExtra("LATITUDE"));
			setLongitude.setText(i.getStringExtra("LONGITUDE"));
		}
		setLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (locationName.getText().toString().isEmpty()) {
					Toast.makeText(SaveLocationActivity.this,
							"Please put location name", Toast.LENGTH_LONG)
							.show();
				} else {
					Intent i = new Intent(SaveLocationActivity.this,
							NavigationDrawerActivity.class);
					i.putExtra("LOCATION_NAME", locationName.getText()
							.toString());
					if (status.equalsIgnoreCase("UPDATE")) {
						i.putExtra("LOCATION_TYPE", "UPDATE");
						i.putExtra("LATITUDE", setLatitude.getText().toString());
						i.putExtra("LONGITUDE", setLongitude.getText()
								.toString());
						i.putExtra("LOCATION_ID", location_id);
					} else {
						i.putExtra("LOCATION_TYPE", "SAVE");
					}
					startActivityForResult(i, 301);
				}
			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (locationName.getText().toString().isEmpty()) {
					Toast.makeText(SaveLocationActivity.this,
							"Please put location name", Toast.LENGTH_LONG)
							.show();
				} else if (setLatitude.getText().toString().isEmpty()) {
					Toast.makeText(SaveLocationActivity.this,
							"Your location not saved \n Please try again!",
							Toast.LENGTH_LONG).show();
				} else {
					// if(status.equalsIgnoreCase("UPDATE")){
					// updateLocation(location_id);
					// }else{
					// saveLocation();
					// }
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 301) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					setLatitude.setText(data.getStringExtra("CENTER_LATITUDE"));
					setLongitude.setText(data
							.getStringExtra("CENTER_LONGITUDE"));
					setLatitude.setEnabled(false);
					setLongitude.setEnabled(false);
					// data.getStringExtra("CURRENT_LATITUDE");
					// data.getStringExtra("CURRENT_LONGITUDE");

					Log.e("product code get success",
							data.getStringExtra("CENTER_LATITUDE") + ";"
									+ data.getStringExtra("CENTER_LONGITUDE")
									+ "success ");
					finish();
				}

			}

		}
	}
}
