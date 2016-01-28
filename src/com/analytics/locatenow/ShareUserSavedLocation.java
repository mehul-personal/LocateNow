package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Transformation;

public class ShareUserSavedLocation extends Activity {
	ArrayList<String> location_name_list, latitude_list, longitude_list,
			location_id_list;
	ListView savelocationlist;
	SaveLocationDataAdapter adapter;
	public static ArrayList<String> forward_id, forward_name, checkbox_val,
			forward_phone_no;
	public static Dialog picker, picker1;
	public static String selected_user_id = "";
	ArrayList<String> namelist, numberlist;
	Button shareMobileLocation;
	GPSTracker gps;
	public static String share_type = "";
	private PendingIntent tracking;
	private int START_DELAY = 5;
	private long UPDATE_INTERVAL = 30000;
	private AlarmManager alarms;
	static String selectedtime = "";
	public static ForwardUserAdapter forwardadapter;
	Button AddNewAddress;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	public static ListView itemlist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_saved_location);

		alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		savelocationlist = (ListView) findViewById(R.id.lvSaveLocation);
		shareMobileLocation = (Button) findViewById(R.id.btnShareMobileLocation);
		AddNewAddress = (Button) findViewById(R.id.btnAddNewAddress);

		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		shareMobileLocation.setTypeface(boldFont);
		AddNewAddress.setTypeface(boldFont);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.myPrimaryColor)));
		actionBar.setCustomView(R.layout.activity_location_header);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		ImageButton savelocation = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.imbSaveLocation);

		savelocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i3 = new Intent(ShareUserSavedLocation.this,
						SaveLocationActivity.class);
				i3.putExtra("STATUS", "ADD");
				startActivityForResult(i3, 1);
			}
		});
		AddNewAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i3 = new Intent(ShareUserSavedLocation.this,
						SaveLocationActivity.class);
				i3.putExtra("STATUS", "ADD");
				startActivityForResult(i3, 1);
			}
		});
		shareMobileLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share_type = "TRACK";
				try {
					gps = new GPSTracker(ShareUserSavedLocation.this);
					//if (gps.canGetLocation()) {
						Log.e("latlong",
								"" + gps.getLatitude() + gps.getLongitude());

						/*
						 * picker = new Dialog(ShareUserSavedLocation.this);
						 * picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
						 * picker
						 * .setContentView(R.layout.verify_pssword_dialog_item);
						 * 
						 * final EditText edtpassword = (EditText) picker
						 * .findViewById(R.id.edtPassword); Button
						 * submitpassword = (Button) picker
						 * .findViewById(R.id.btnSubmitPassword); submitpassword
						 * .setOnClickListener(new OnClickListener() {
						 * 
						 * @Override public void onClick(View v) { // TODO
						 * Auto-generated method stub
						 * 
						 * SharedPreferences mPrefs = getSharedPreferences(
						 * "LOGIN_DETAIL", MODE_PRIVATE); if
						 * (mPrefs.getString("PASSWORD", "") .equalsIgnoreCase(
						 * edtpassword.getText() .toString())) {
						 * picker.dismiss();
						 */picker1 = new Dialog(ShareUserSavedLocation.this);
						picker1.requestWindowFeature(Window.FEATURE_NO_TITLE);
						picker1.setContentView(R.layout.list_time_dialog_item);

						final Spinner time = (Spinner) picker1
								.findViewById(R.id.spnTime);
						Button submit = (Button) picker1
								.findViewById(R.id.btnSubmit);
						TextView heading = (TextView) picker1
								.findViewById(R.id.txvLocationVisibilityTime);
						heading.setTypeface(boldFont);
						submit.setTypeface(boldFont);
						submit.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated
								// method stub

								selectedtime = time.getSelectedItem()
										.toString();
								picker1.dismiss();
								userForwardList(gps.getLatitude() + "",
										gps.getLongitude() + "", "");

							}
						});
						picker1.show();

						/*
						 * } else { Toast.makeText( ShareUserSavedLocation.this,
						 * "Your Password is wrong please try again!",
						 * Toast.LENGTH_LONG).show(); } } });
						 * 
						 * picker.show();
						 *//*} else {
						Toast.makeText(
								ShareUserSavedLocation.this,
								"Sorry! we can't find your current location \n Please start your GPS!",
								Toast.LENGTH_LONG).show();
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		getSaveLocationData();
	}

	private void setRecurringAlarm(Context context) {

		// get a Calendar object with current time
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, START_DELAY);
		Intent intent = new Intent(context, AlarmReceiver.class);
		tracking = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), UPDATE_INTERVAL, tracking);
	}

	public void stopSendingLocation() {
		Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		tracking = PendingIntent.getBroadcast(getBaseContext(), 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(tracking);
		Log.e("stop location send", ">>>Stop tracking()");
	}

	public void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Goto Settings Page To Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void getSaveLocationData() {
		String tag_json_obj = "json_obj_req";

		String url = ApplicationData.serviceURL + "get_user_location.php";
		Log.e("url", url + "");

		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		final String user_id = mPrefs.getString("USER_ID", "");

		final ProgressDialog mProgressDialog = new ProgressDialog(
				ShareUserSavedLocation.this);
		mProgressDialog.setTitle("");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("Please Wait...");
		mProgressDialog.show();

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("get_user_location", response.toString());
						location_name_list = new ArrayList<String>();
						latitude_list = new ArrayList<String>();
						longitude_list = new ArrayList<String>();
						location_id_list = new ArrayList<String>();
						try {

							mProgressDialog.dismiss();
							JSONObject object = new JSONObject(response
									.toString());

							String msg = object.getString("msg");
							if (msg.equalsIgnoreCase("Success")) {
								JSONArray dataArr = object.getJSONArray("data");
								for (int i = 0; i < dataArr.length(); i++) {
									JSONObject dataOb = dataArr
											.getJSONObject(i);
									location_id_list.add(dataOb
											.getString("location_id"));
									location_name_list.add(dataOb
											.getString("location_name"));
									latitude_list.add(dataOb
											.getString("latitude"));
									longitude_list.add(dataOb
											.getString("longitude"));

								}
								adapter = new SaveLocationDataAdapter(
										location_id_list, location_name_list,
										latitude_list, longitude_list);
								savelocationlist.setAdapter(adapter);
							} else {
								Toast.makeText(
										ShareUserSavedLocation.this,
										"Sorry! we can't find any saved location",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(
									ShareUserSavedLocation.this,
									"Sorry! we are stuff to fetching data. \n Please try again!",
									Toast.LENGTH_SHORT).show();

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mProgressDialog.dismiss();
						VolleyLog.e("get_user_location Error", "Error: "
								+ error.getMessage());
						// hide the progress dialog
						error.getCause();
						error.printStackTrace();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("userid", user_id);
				return params;
			}
		};
		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		// Adding request to request queue
		ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
				tag_json_obj);

		/*
		 * new AsyncTask<Void, Void, String>() { ProgressDialog mProgressDialog;
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override protected String doInBackground(Void... params) { // TODO
		 * Auto-generated method stub
		 * 
		 * SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
		 * MODE_PRIVATE); String user_id = mPrefs.getString("USER_ID", "");
		 * 
		 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost =
		 * new HttpPost(ApplicationData.serviceURL + "get_user_location.php");
		 * try { // Add your data List<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>( 1); nameValuePairs .add(new
		 * BasicNameValuePair("userid", user_id));
		 * 
		 * httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
		 * HTTP.UTF_8));
		 * 
		 * // Execute HTTP Post Request HttpResponse response =
		 * httpclient.execute(httppost); BufferedReader in = new BufferedReader(
		 * new InputStreamReader(response.getEntity() .getContent()));
		 * StringBuffer sb = new StringBuffer(""); String line = ""; while
		 * ((line = in.readLine()) != null) { sb.append(line); } in.close();
		 * Log.e("get seller Data", "" + sb.toString()); return sb.toString(); }
		 * catch (Exception e) { Log.e("seller get data error", "" + e); return
		 * ""; } }
		 * 
		 * @Override protected void onPostExecute(String result) { // TODO
		 * Auto-generated method stub super.onPostExecute(result);
		 * location_name_list = new ArrayList<String>(); latitude_list = new
		 * ArrayList<String>(); longitude_list = new ArrayList<String>();
		 * location_id_list = new ArrayList<String>(); try {
		 * 
		 * mProgressDialog.dismiss(); JSONObject object = new
		 * JSONObject(result.toString());
		 * 
		 * String msg = object.getString("msg"); if
		 * (msg.equalsIgnoreCase("Success")) { JSONArray dataArr =
		 * object.getJSONArray("data"); for (int i = 0; i < dataArr.length();
		 * i++) { JSONObject dataOb = dataArr.getJSONObject(i);
		 * location_id_list.add(dataOb .getString("location_id"));
		 * location_name_list.add(dataOb .getString("location_name"));
		 * latitude_list.add(dataOb.getString("latitude"));
		 * longitude_list.add(dataOb.getString("longitude"));
		 * 
		 * } adapter = new SaveLocationDataAdapter(location_id_list,
		 * location_name_list, latitude_list, longitude_list);
		 * savelocationlist.setAdapter(adapter); } else {
		 * Toast.makeText(ShareUserSavedLocation.this,
		 * "Sorry! we can't find any saved location",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); Toast.makeText( ShareUserSavedLocation.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } }
		 * 
		 * @Override protected void onPreExecute() { // TODO Auto-generated
		 * method stub super.onPreExecute(); mProgressDialog = new
		 * ProgressDialog( ShareUserSavedLocation.this);
		 * mProgressDialog.setTitle("");
		 * mProgressDialog.setCanceledOnTouchOutside(false);
		 * mProgressDialog.setMessage("Please Wait..."); mProgressDialog.show();
		 * 
		 * } }.execute();
		 */
	}

	public void deleteLocation(final String locationId) {
		String tag_json_obj = "json_obj_req";
		String url = ApplicationData.serviceURL + "delete_location.php";
		Log.e("url", url + "");

		final ProgressDialog mProgressDialog = new ProgressDialog(
				ShareUserSavedLocation.this);
		mProgressDialog.setTitle("");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("Please Wait...");
		mProgressDialog.show();

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("get_user_location", response.toString());
						try {

							mProgressDialog.dismiss();
							JSONObject object = new JSONObject(response
									.toString());

							String msg = object.getString("msg");
							if (msg.equalsIgnoreCase("Success")) {
								Toast.makeText(ShareUserSavedLocation.this,
										"Your location deleted Successfully",
										Toast.LENGTH_SHORT).show();
								getSaveLocationData();
							} else {
								Toast.makeText(ShareUserSavedLocation.this,
										"Sorry! we can't delete your location",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(
									ShareUserSavedLocation.this,
									"Sorry! we are stuff to deleting data. \n Please try again!",
									Toast.LENGTH_SHORT).show();

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mProgressDialog.dismiss();
						VolleyLog.e("get_user_location Error", "Error: "
								+ error.getMessage());
						// hide the progress dialog
						error.getCause();
						error.printStackTrace();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
						MODE_PRIVATE);
				String user_id = mPrefs.getString("USER_ID", "");
				Map<String, String> params = new HashMap<String, String>();
				params.put("userid", user_id);
				params.put("location_id", locationId);
				return params;
			}
		};
		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		// Adding request to request queue
		ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
				tag_json_obj);

		/*
		 * new AsyncTask<Void, Void, String>() { ProgressDialog mProgressDialog;
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override protected String doInBackground(Void... params) { // TODO
		 * Auto-generated method stub
		 * 
		 * SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
		 * MODE_PRIVATE); String user_id = mPrefs.getString("USER_ID", "");
		 * 
		 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost =
		 * new HttpPost(ApplicationData.serviceURL + "delete_location.php"); try
		 * { // Add your data List<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>( 1); nameValuePairs .add(new
		 * BasicNameValuePair("userid", user_id)); nameValuePairs.add(new
		 * BasicNameValuePair("location_id", locationId));
		 * 
		 * httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
		 * HTTP.UTF_8));
		 * 
		 * // Execute HTTP Post Request HttpResponse response =
		 * httpclient.execute(httppost); BufferedReader in = new BufferedReader(
		 * new InputStreamReader(response.getEntity() .getContent()));
		 * StringBuffer sb = new StringBuffer(""); String line = ""; while
		 * ((line = in.readLine()) != null) { sb.append(line); } in.close();
		 * Log.e("get seller Data", "" + sb.toString()); return sb.toString(); }
		 * catch (Exception e) { Log.e("seller get data error", "" + e); return
		 * ""; } }
		 * 
		 * @Override protected void onPostExecute(String result) { // TODO
		 * Auto-generated method stub super.onPostExecute(result);
		 * 
		 * try {
		 * 
		 * mProgressDialog.dismiss(); JSONObject object = new
		 * JSONObject(result.toString());
		 * 
		 * String msg = object.getString("msg"); if
		 * (msg.equalsIgnoreCase("Success")) {
		 * Toast.makeText(ShareUserSavedLocation.this,
		 * "Your location deleted Successfully", Toast.LENGTH_SHORT).show();
		 * getSaveLocationData(); } else {
		 * Toast.makeText(ShareUserSavedLocation.this,
		 * "Sorry! we can't delete your location", Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); Toast.makeText( ShareUserSavedLocation.this,
		 * "Sorry! we are stuff to deleting data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } }
		 * 
		 * @Override protected void onPreExecute() { // TODO Auto-generated
		 * method stub super.onPreExecute(); mProgressDialog = new
		 * ProgressDialog( ShareUserSavedLocation.this);
		 * mProgressDialog.setTitle("");
		 * mProgressDialog.setCanceledOnTouchOutside(false);
		 * mProgressDialog.setMessage("Please Wait..."); mProgressDialog.show();
		 * 
		 * } }.execute();
		 */
	}

	public class SaveLocationDataAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<String> loc_id, loc_list, lat_list, long_list;

		int m = 0;

		public SaveLocationDataAdapter(ArrayList<String> location_id,
				ArrayList<String> loc_list, ArrayList<String> lat_list,
				ArrayList<String> long_list) {
			// TODO Auto-generated constructor stub
			this.loc_id = location_id;
			this.loc_list = loc_list;
			this.lat_list = lat_list;
			this.long_list = long_list;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			TextView locationName;
			ImageView share, update, delete, viewLocation;
		}

		public void refreshList() {
			super.notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			// View view = null;

			ViewHolder holder;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_saved_location,
						parent, false);
				holder = new ViewHolder();
				holder.locationName = (TextView) convertView
						.findViewById(R.id.txvLocationName);
				holder.share = (ImageView) convertView
						.findViewById(R.id.imvShareLocation);
				holder.update = (ImageView) convertView
						.findViewById(R.id.imvUpdateLocation);
				holder.delete = (ImageView) convertView
						.findViewById(R.id.imvDeleteLocation);
				holder.viewLocation = (ImageView) convertView
						.findViewById(R.id.imvViewLocation);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.locationName.setTypeface(mediumFont);
			if (position % 4 == 0) {
				holder.locationName.setBackgroundColor(Color
						.parseColor("#ffa200"));
				holder.share.setBackgroundColor(Color.parseColor("#4c3000"));
				holder.update.setBackgroundColor(Color.parseColor("#4c3000"));
				holder.delete.setBackgroundColor(Color.parseColor("#4c3000"));
				holder.viewLocation.setBackgroundColor(Color
						.parseColor("#4c3000"));
			} else if (position % 4 == 1) {
				holder.locationName.setBackgroundColor(Color
						.parseColor("#8a638f"));
				holder.share.setBackgroundColor(Color.parseColor("#3e2541"));
				holder.update.setBackgroundColor(Color.parseColor("#3e2541"));
				holder.delete.setBackgroundColor(Color.parseColor("#3e2541"));
				holder.viewLocation.setBackgroundColor(Color
						.parseColor("#3e2541"));
			} else if (position % 4 == 2) {
				holder.locationName.setBackgroundColor(Color
						.parseColor("#0bc1f0"));
				holder.share.setBackgroundColor(Color.parseColor("#119abd"));
				holder.update.setBackgroundColor(Color.parseColor("#119abd"));
				holder.delete.setBackgroundColor(Color.parseColor("#119abd"));
				holder.viewLocation.setBackgroundColor(Color
						.parseColor("#119abd"));
			} else if (position % 4 == 3) {
				holder.locationName.setBackgroundColor(Color
						.parseColor("#00a79d"));
				holder.share.setBackgroundColor(Color.parseColor("#0c736d"));
				holder.update.setBackgroundColor(Color.parseColor("#0c736d"));
				holder.delete.setBackgroundColor(Color.parseColor("#0c736d"));
				holder.viewLocation.setBackgroundColor(Color
						.parseColor("#0c736d"));
			}
			holder.locationName.setText(loc_list.get(position));
			holder.viewLocation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent();
					i.putExtra("LATITUDE", lat_list.get(position));
					i.putExtra("LONGITUDE", long_list.get(position));
					i.putExtra("LOCATION_NAME", loc_list.get(position));
					setResult(109, i);
					finish();
				}
			});
			holder.update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(ShareUserSavedLocation.this,
							SaveLocationActivity.class);
					i.putExtra("STATUS", "UPDATE");
					i.putExtra("ID", loc_id.get(position));
					i.putExtra("NAME", loc_list.get(position));
					i.putExtra("LATITUDE", lat_list.get(position));
					i.putExtra("LONGITUDE", long_list.get(position));
					startActivityForResult(i, 2);
				}
			});
			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					deleteLocation(loc_id.get(position));
				}
			});
			holder.share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					share_type = "NAVIGATE";
					selectedtime = "";
					/*
					 * picker = new Dialog(ShareUserSavedLocation.this);
					 * picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
					 * picker
					 * .setContentView(R.layout.verify_pssword_dialog_item);
					 * 
					 * final EditText edtpassword = (EditText) picker
					 * .findViewById(R.id.edtPassword); Button submitpassword =
					 * (Button) picker .findViewById(R.id.btnSubmitPassword);
					 * submitpassword.setOnClickListener(new OnClickListener() {
					 * 
					 * @Override public void onClick(View v) { // TODO
					 * Auto-generated method stub SharedPreferences mPrefs =
					 * getSharedPreferences( "LOGIN_DETAIL", MODE_PRIVATE); if
					 * (mPrefs.getString("PASSWORD", "") .equalsIgnoreCase(
					 * edtpassword.getText().toString())) {
					 */userForwardList(lat_list.get(position),
							long_list.get(position), loc_id.get(position));
					/*
					 * picker.dismiss(); } else { Toast.makeText(
					 * ShareUserSavedLocation.this,
					 * "Your Password is wrong please try again!",
					 * Toast.LENGTH_LONG).show(); } } });
					 * 
					 * picker.show();
					 */}
			});
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loc_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {
			getSaveLocationData();
		}
	}

	public class CircleTransform implements Transformation {
		@Override
		public Bitmap transform(Bitmap source) {
			int size = Math.min(source.getWidth(), source.getHeight());

			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;

			Bitmap squaredBitmap = Bitmap
					.createBitmap(source, x, y, size, size);
			if (squaredBitmap != source) {
				source.recycle();
			}

			Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			BitmapShader shader = new BitmapShader(squaredBitmap,
					BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setAntiAlias(true);

			float r = size / 2f;
			canvas.drawCircle(r, r, r, paint);

			squaredBitmap.recycle();
			return bitmap;
		}

		@Override
		public String key() {
			return "circle";
		}
	}

	public void userForwardList(final String lat, final String log,
			final String location_id) {

		String tag_json_obj = "json_obj_req";
		String url = ApplicationData.serviceURL + "all_bonjour_user.php";
		Log.e("url", url + "");

		final ProgressDialog mProgressDialog = new ProgressDialog(
				ShareUserSavedLocation.this);
		mProgressDialog.setTitle("");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("Please Wait...");
		mProgressDialog.show();

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("all_bonjour_user", response.toString());
						forward_id = new ArrayList<String>();
						forward_name = new ArrayList<String>();

						forward_phone_no = new ArrayList<String>();
						try {

							mProgressDialog.dismiss();
							JSONObject object = new JSONObject(response
									.toString());

							String msg = object.getString("msg");
							if (msg.equalsIgnoreCase("Success")) {
								JSONArray dataArr = object.getJSONArray("data");
								for (int i = 0; i < dataArr.length(); i++) {
									JSONObject dataOb = dataArr
											.getJSONObject(i);
									forward_name.add(dataOb.getString("name"));
									forward_id.add(dataOb.getString("id"));
									forward_phone_no.add(dataOb
											.getString("phone"));

								}

								getAllContacts(lat, log, location_id);

							} else {
								Toast.makeText(
										ShareUserSavedLocation.this,
										"Sorry! we are stuff to fetching data. \n Please try again!",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(
									ShareUserSavedLocation.this,
									"Sorry! we are stuff to fetching data. \n Please try again!",
									Toast.LENGTH_SHORT).show();

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mProgressDialog.dismiss();
						VolleyLog.e("all_bonjour_user Error",
								"Error: " + error.getMessage());
						// hide the progress dialog
						error.getCause();
						error.printStackTrace();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
						MODE_PRIVATE);
				String user_id = mPrefs.getString("USER_ID", "");
				Map<String, String> params = new HashMap<String, String>();
				params.put("userid", user_id);

				return params;
			}
		};
		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		// Adding request to request queue
		ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
				tag_json_obj);

		/*
		 * new AsyncTask<Void, Void, String>() { ProgressDialog mProgressDialog;
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override protected String doInBackground(Void... params) { // TODO
		 * Auto-generated method stub
		 * 
		 * SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
		 * MODE_PRIVATE); String user_id = mPrefs.getString("USER_ID", "");
		 * 
		 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost =
		 * new HttpPost(ApplicationData.serviceURL + "all_bonjour_user.php");
		 * try { // Add your data List<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>( 1); nameValuePairs .add(new
		 * BasicNameValuePair("userid", user_id));
		 * 
		 * httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
		 * HTTP.UTF_8));
		 * 
		 * // Execute HTTP Post Request HttpResponse response =
		 * httpclient.execute(httppost); BufferedReader in = new BufferedReader(
		 * new InputStreamReader(response.getEntity() .getContent()));
		 * StringBuffer sb = new StringBuffer(""); String line = ""; while
		 * ((line = in.readLine()) != null) { sb.append(line); } in.close();
		 * Log.e("user forward Data", "" + sb.toString()); return sb.toString();
		 * } catch (Exception e) { Log.e("user forward problem data setting", ""
		 * + e); return ""; } }
		 * 
		 * @Override protected void onPostExecute(String result) { // TODO
		 * Auto-generated method stub super.onPostExecute(result); forward_id =
		 * new ArrayList<String>(); forward_name = new ArrayList<String>();
		 * 
		 * forward_phone_no = new ArrayList<String>(); try {
		 * 
		 * mProgressDialog.dismiss(); JSONObject object = new
		 * JSONObject(result.toString());
		 * 
		 * String msg = object.getString("msg"); if
		 * (msg.equalsIgnoreCase("Success")) { JSONArray dataArr =
		 * object.getJSONArray("data"); for (int i = 0; i < dataArr.length();
		 * i++) { JSONObject dataOb = dataArr.getJSONObject(i);
		 * forward_name.add(dataOb.getString("name"));
		 * forward_id.add(dataOb.getString("id"));
		 * forward_phone_no.add(dataOb.getString("phone"));
		 * 
		 * }
		 * 
		 * getAllContacts(lat, log, location_id);
		 * 
		 * } else { Toast.makeText( ShareUserSavedLocation.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); Toast.makeText( ShareUserSavedLocation.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } }
		 * 
		 * @Override protected void onPreExecute() { // TODO Auto-generated
		 * method stub super.onPreExecute(); mProgressDialog = new
		 * ProgressDialog( ShareUserSavedLocation.this);
		 * mProgressDialog.setTitle("");
		 * mProgressDialog.setCanceledOnTouchOutside(false);
		 * mProgressDialog.setMessage("Please Wait..."); mProgressDialog.show();
		 * 
		 * } }.execute();
		 */
	}

	public void getAllContacts(final String lat, final String log,
			final String location_id) {
		namelist = new ArrayList<String>();
		numberlist = new ArrayList<String>();
		final ArrayList<String> filter_id = new ArrayList<String>();
		final ArrayList<String> filter_name = new ArrayList<String>();
		checkbox_val = new ArrayList<String>();
		// Cursor phones =
		// getActivity().getContentResolver().query(Phone.CONTENT_URI, null,
		// null, null, Phone.DISPLAY_NAME+ " ASC");
		Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null,
				null, null, "upper(" + Phone.DISPLAY_NAME + ") ASC");
		while (phones.moveToNext()) {
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumberString = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			phoneNumberString = phoneNumberString.replace(" ", "");
			int l = phoneNumberString.length();
			String phone = "";
			if (l > 10) {
				phone = phoneNumberString.substring((l - 10), l);
			} else {
				phone = phoneNumberString;
			}
			Log.e(name, phone);
			namelist.add(name);
			numberlist.add(phone);
		}

		picker = new Dialog(ShareUserSavedLocation.this);
		picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picker.setContentView(R.layout.list_select_person_dialog);
		TextView selectPersonTitle = (TextView) picker
				.findViewById(R.id.txvSelectPerson);
		itemlist = (ListView) picker
				.findViewById(R.id.lsvItemDialog);
		Button forward = (Button) picker.findViewById(R.id.btnShareLocation);
		Button share = (Button) picker.findViewById(R.id.btnShareViaOtherApps);
		final AutoCompleteTextView search_user = (AutoCompleteTextView) picker
				.findViewById(R.id.autoCompleteTextView1);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, forward_name);
		search_user.setAdapter(adapter1);
		forward.setText("Share Location");

		share.setTypeface(boldFont);
		forward.setTypeface(boldFont);
		search_user.setTypeface(regularFont);
		selectPersonTitle.setTypeface(boldFont);

		search_user.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				search_user.setInputType(InputType.TYPE_CLASS_TEXT);
				search_user.onTouchEvent(event); // call native handler
				return true; // consume touch even
			}
		});
		search_user.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				for (int i = 0; i < forward_name.size(); i++) {
					if (parent.getItemAtPosition(position).toString()
							.equalsIgnoreCase(forward_name.get(i))) {
						// selected_user_id = forward_id.get(i);
						// sellerShareLocation(lat, log);
						// picker.dismiss();
						filter_id.add(forward_id.get(i));
						filter_name.add(forward_name.get(i));
						checkbox_val.add("false");
						Log.e("name", parent.getItemAtPosition(position)
								.toString() + ":" + forward_name.get(i));
						break;
					}
				}
				forwardadapter.notifyDataSetChanged();
				// forwardadapter = new ForwardUserAdapter(filter_id,
				// filter_name);
				// itemlist.setAdapter(forwardadapter);
			}
		});

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String shareBody = "Hey! freinds login in a bonjour you find the your friend location";
				Intent sharingIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Here's my location,courtesy of bonjour");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				startActivity(Intent.createChooser(sharingIntent,
						"Share via other apps"));
			}
		});

		for (int j = 0; j < forward_phone_no.size(); j++) {
			for (int i = 0; i < numberlist.size(); i++) {
				if (forward_phone_no.get(j).equalsIgnoreCase(numberlist.get(i))) {
					filter_id.add(forward_id.get(j));
					filter_name.add(forward_name.get(j));
					checkbox_val.add("false");
					break;
				}
			}
		}
		forwardadapter = new ForwardUserAdapter(filter_id, filter_name);
		itemlist.setAdapter(forwardadapter);
		forward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selected_user_id = "";
				int m = 0;
				for (int i = 0; i < checkbox_val.size(); i++) {
					Log.e("checkbox value", "value:" + checkbox_val.get(i));
					if (checkbox_val.get(i).equalsIgnoreCase("true")) {
						selected_user_id = selected_user_id + "|"
								+ filter_id.get(i);
					} else {
						m++;
					}
				}
				Log.e("selected user id", selected_user_id + "");
				if (m != checkbox_val.size()) {
					sellerShareLocation(lat, log, location_id);
					picker.dismiss();
				} else {
					Toast.makeText(ShareUserSavedLocation.this,
							"Please select any sender", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		picker.show();

	}

	public class ForwardUserAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<String> forwardUserID, forwardUserName;

		int m = 0;

		public ForwardUserAdapter(ArrayList<String> forwardUserID,
				ArrayList<String> forwardUserName) {
			// TODO Auto-generated constructor stub
			this.forwardUserID = forwardUserID;
			this.forwardUserName = forwardUserName;

			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			TextView txvForwardName;
			CheckBox chbForwardUser;

		}

		public void refreshList() {
			super.notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			// View view = null;

			ViewHolder holder;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_user_list, parent,
						false);
				holder = new ViewHolder();
				holder.txvForwardName = (TextView) convertView
						.findViewById(R.id.txvForwarduserName);
				holder.chbForwardUser = (CheckBox) convertView
						.findViewById(R.id.chbForwardUser);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txvForwardName.setTypeface(mediumFont);
			if(checkbox_val.get(position).equalsIgnoreCase("false")){
				holder.chbForwardUser.setChecked(false);
			}else{
				holder.chbForwardUser.setChecked(true);
			}
			holder.chbForwardUser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(checkbox_val.get(position).equalsIgnoreCase("false")){
						 checkbox_val.set(position, "true");
					}else{
						checkbox_val.set(position, "false");
					}
					
				}
			});
			/*holder.chbForwardUser
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							//checkbox_val.set(position, isChecked + "");
							int pos = itemlist.getPositionForView(buttonView);
					         System.out.println("Pos ["+pos+"]");
					         if (pos != ListView.INVALID_POSITION) {
//					             Planet p = planetsList.get(pos);        
//					             p.setChecked(isChecked);
					        	 checkbox_val.set(pos, isChecked + "");
					         }
					
						}
					});*/

			holder.txvForwardName.setText(forwardUserName.get(position));
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return forwardUserID.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public void sellerShareLocation(final String lat, final String log,
			final String location_id) {

		String tag_json_obj = "json_obj_req";
		String url = ApplicationData.serviceURL+ "share_location.php";
        Log.e("url", url + "");
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		final String user_id = mPrefs.getString("USER_ID", "");
		
        final ProgressDialog mProgressDialog = new ProgressDialog(ShareUserSavedLocation.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("share_location", response.toString());
                        forward_id = new ArrayList<String>();
        				forward_name = new ArrayList<String>();
        				checkbox_val = new ArrayList<String>();
        				try {

        					mProgressDialog.dismiss();
        					JSONObject object = new JSONObject(response.toString());

        					String msg = object.getString("msg");
        					if (msg.equalsIgnoreCase("Success")) {
        						JSONObject dataob = object.getJSONObject("data");
        						picker.dismiss();
        						SharedPreferences mPrefs = getSharedPreferences(
        								"LOGIN_DETAIL", MODE_PRIVATE);
        						Editor edit = mPrefs.edit();
        						edit.putString("SHARE_TRIP_ID",
        								dataob.getString("last_share_id"));
        						edit.commit();
        						Toast.makeText(ShareUserSavedLocation.this,
        								"Thank you! your location shared successfully",
        								Toast.LENGTH_SHORT).show();

        						LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        						if (locationManager
        								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        							// Toast.makeText(this,
        							// "GPS is Enabled in your device",
        							// Toast.LENGTH_SHORT).show();
        						} else {
        							showGPSDisabledAlertToUser();
        						}
        						setRecurringAlarm(ShareUserSavedLocation.this);
        					} else {
        						Toast.makeText(
        								ShareUserSavedLocation.this,
        								"Sorry! we are stuff to fetching data. \n Please try again!",
        								Toast.LENGTH_SHORT).show();
        					}

        				} catch (Exception e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        					Toast.makeText(
        							ShareUserSavedLocation.this,
        							"Sorry! we are stuff to fetching data. \n Please try again!",
        							Toast.LENGTH_SHORT).show();

        				}
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("share_location Error",
                        "Error: " + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                             params.put("userid", user_id);
                             params.put("to_userid", selected_user_id.substring(1,
 									selected_user_id.length()));
                             params.put("latitude", lat);
                             params.put("longitude", log);
                             params.put("comment", "nothing");
                             params.put("sharing_type", share_type);
                             params.put("visible_time", selectedtime);
                             params.put("location_id", location_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
		
		
		/*new AsyncTask<Void, Void, String>() {
			ProgressDialog mProgressDialog;

			@SuppressWarnings("deprecation")
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
						MODE_PRIVATE);
				String user_id = mPrefs.getString("USER_ID", "");

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(ApplicationData.serviceURL
						+ "share_location.php");
				try {
					Log.e("sharing latlong", lat + "::" + log);
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs
							.add(new BasicNameValuePair("userid", user_id));
					nameValuePairs.add(new BasicNameValuePair("to_userid",
							selected_user_id.substring(1,
									selected_user_id.length())));
					nameValuePairs.add(new BasicNameValuePair("latitude", lat));
					nameValuePairs
							.add(new BasicNameValuePair("longitude", log));
					nameValuePairs.add(new BasicNameValuePair("comment",
							"nothing"));
					nameValuePairs.add(new BasicNameValuePair("sharing_type",
							share_type));
					nameValuePairs.add(new BasicNameValuePair("visible_time",
							selectedtime));
					nameValuePairs.add(new BasicNameValuePair("location_id",
							location_id));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
							HTTP.UTF_8));

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
					Log.e("user forward Data", "" + sb.toString());
					return sb.toString();
				} catch (Exception e) {
					Log.e("user forward problem data setting", "" + e);
					return "";
				}
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				forward_id = new ArrayList<String>();
				forward_name = new ArrayList<String>();
				checkbox_val = new ArrayList<String>();
				try {

					mProgressDialog.dismiss();
					JSONObject object = new JSONObject(result.toString());

					String msg = object.getString("msg");
					if (msg.equalsIgnoreCase("Success")) {
						JSONObject dataob = object.getJSONObject("data");
						picker.dismiss();
						SharedPreferences mPrefs = getSharedPreferences(
								"LOGIN_DETAIL", MODE_PRIVATE);
						Editor edit = mPrefs.edit();
						edit.putString("SHARE_TRIP_ID",
								dataob.getString("last_share_id"));
						edit.commit();
						Toast.makeText(ShareUserSavedLocation.this,
								"Thank you! your location shared successfully",
								Toast.LENGTH_SHORT).show();

						LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
						if (locationManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							// Toast.makeText(this,
							// "GPS is Enabled in your device",
							// Toast.LENGTH_SHORT).show();
						} else {
							showGPSDisabledAlertToUser();
						}
						setRecurringAlarm(ShareUserSavedLocation.this);
					} else {
						Toast.makeText(
								ShareUserSavedLocation.this,
								"Sorry! we are stuff to fetching data. \n Please try again!",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(
							ShareUserSavedLocation.this,
							"Sorry! we are stuff to fetching data. \n Please try again!",
							Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(
						ShareUserSavedLocation.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please Wait...");
				mProgressDialog.show();

			}
		}.execute();*/
	}

}
