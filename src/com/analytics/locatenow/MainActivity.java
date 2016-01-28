package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

import android.annotation.SuppressLint;
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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerCallbacks, OnCameraChangeListener, LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, OnItemClickListener,
		InfoWindowAdapter {
	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private Toolbar mToolbar;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	GoogleMap googleMap;
	private GoogleApiClient mLocationClient = null;
	private LocationRequest mLocationRequest = null;
	public static LatLng currentLatLang;
	static boolean flag = false, trackstart = false, chklocation = false;

	ImageButton imbMapType, imbShareLocation, imbSaveLocation, imbChatMessage;
	public static String CLICKED_USERID = "", selected_trip_id = "",
			share_type = "";
	public static TimerTask doAsynchronousTask;
	static String selectedphone = "", selectedshareid = "",
			selected_user_name = "";
	static ArrayList<String> shareUserID, shareLatitude, shareLongitude,
			shareComment, shareUserName, shareTripID, sharing_type,
			location_name, sharePhone, shareUserImage, messageID;
	// public static BadgeView badge;
	public static ArrayList<String> forward_id, forward_name, checkbox_val,
			forward_phone_no;
	static String selectedtime = "";
	static ArrayList<String> namelist, numberlist;
	public static Dialog picker;
	public static String selected_user_id = "";
	public static ForwardUserAdapter forwardadapter;
	private PendingIntent tracking;
	private int START_DELAY = 5;
	private AlarmManager alarms;
	static Button chatcount;
	static ArrayList<Marker> trackMarker;

	private boolean servicesConnected() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == resultCode) {
			Log.d("Location Updates", "Google Play services is available.");
			return true;
		} else {
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			if (errorDialog != null) {
				errorDialog.show();
			}
			return false;
		}
	}

	Typeface mediumFont, boldFont, regularFont, semiboldFont;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_topdrawer);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		// int actionBarTitleId = Resources.getSystem().getIdentifier(
		// "action_bar_title", "id", "android");
		// if (actionBarTitleId > 0) {
		// TextView title = (TextView)
		// getWindow().findViewById(actionBarTitleId);
		//
		// title.setTypeface(mediumFont);
		// }
		SpannableString s = new SpannableString("Welcome to Locate Now");
		s.setSpan(new TypefaceSpan("Lato-Medium.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(s);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_drawer);
		mNavigationDrawerFragment.setup(R.id.fragment_drawer,
				(DrawerLayout) findViewById(R.id.drawer), mToolbar);
		imbMapType = (ImageButton) findViewById(R.id.imbMapType);
		imbShareLocation = (ImageButton) findViewById(R.id.imbShareLocation);
		imbSaveLocation = (ImageButton) findViewById(R.id.imbSaveLocation);
		imbChatMessage = (ImageButton) findViewById(R.id.imbChatMessages);
		trackMarker = new ArrayList<Marker>();
		// badge = new BadgeView(MainActivity.this, imbChatMessage);
		// badge.setX(-30);
		mLocationClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		servicesConnected();
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		googleMap = supportMapFragment.getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		googleMap.setMyLocationEnabled(true);

		getSaveLocationData();
		SharedPreferences mPrefs = getBaseContext().getSharedPreferences(
				"LOGIN_DETAIL", MODE_PRIVATE);
		String user_id = mPrefs.getString("USER_ID", "");
		getMySharedLocation(user_id);
		imbMapType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chklocation = false;
				final Dialog picker = new Dialog(MainActivity.this);
				picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
				picker.setContentView(R.layout.list_map_type);

				ListView itemlist = (ListView) picker
						.findViewById(R.id.lsvItemDialog);
				final ArrayList<String> data = new ArrayList<String>();
				final ArrayList<Integer> image = new ArrayList<Integer>();
				data.add("Normal");
				data.add("Satellite");
				data.add("Terrain");
				data.add("Hybrid");
				image.add(R.drawable.ic_map_normal);
				image.add(R.drawable.ic_map_satellite);
				image.add(R.drawable.ic_map_terrien);
				image.add(R.drawable.ic_map_hybrid);
				//
				// ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				// MainActivity.this, android.R.layout.simple_list_item_1,
				// android.R.id.text1, data);
				MapTypeAdapter adapter = new MapTypeAdapter(data, image);
				itemlist.setAdapter(adapter);
				itemlist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						if (position == 0) {
							googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						} else if (position == 1) {
							googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
						} else if (position == 2) {
							googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
						} else if (position == 3) {
							googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						}
						picker.dismiss();
					}
				});
				picker.show();
			}
		});
		imbShareLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				share_type = "TRACK";
				chklocation = false;
				try {
					final GPSTracker gps = new GPSTracker(MainActivity.this);
					//if (gps.canGetLocation()) {
						Log.e("latlong",
								"" + gps.getLatitude() + gps.getLongitude());

						/*
						 * final Dialog picker = new
						 * Dialog(GPSActivityOnline.this);
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
						 */
						final Dialog picker1 = new Dialog(MainActivity.this);
						picker1.requestWindowFeature(Window.FEATURE_NO_TITLE);
						picker1.setContentView(R.layout.list_time_dialog_item);

						final Spinner time = (Spinner) picker1
								.findViewById(R.id.spnTime);
						Button submit = (Button) picker1
								.findViewById(R.id.btnSubmit);
						TextView heading = (TextView) picker1
								.findViewById(R.id.txvLocationVisibilityTime);
						TextView watchMe = (TextView) picker1
								.findViewById(R.id.txvWatchMe);
						submit.setTypeface(boldFont);
						heading.setTypeface(boldFont);
						watchMe.setTypeface(mediumFont);
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
						 * } else { Toast.makeText( GPSActivityOnline.this,
						 * "Your Password is wrong please try again!",
						 * Toast.LENGTH_LONG).show(); } } });
						 * 
						 * picker.show();
						 *//*} else {
						Toast.makeText(
								MainActivity.this,
								"Sorry! we can't find your current location \n Please start your GPS!",
								Toast.LENGTH_LONG).show();
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}

				chklocation = false;
			}
		});

		imbSaveLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i3 = new Intent(MainActivity.this,
						SaveLocationActivity.class);
				i3.putExtra("STATUS", "ADD");
				startActivityForResult(i3, 1);
				chklocation = false;
			}
		});
		imbChatMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ShareLocationAdapter adapter = new ShareLocationAdapter(
				// shareUserID, shareLatitude, shareLongitude,
				// shareComment, shareUserName, shareTripID,
				// sharing_type, location_name, sharePhone,
				// messageID);

				Intent i = new Intent(MainActivity.this,
						AllMessageActivity.class);

				startActivityForResult(i, 11);
			}
		});
		googleMap.setOnCameraChangeListener(this);
		// googleMap.setOnMapLongClickListener(this);
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				if (arg0.getSnippet() != null) {
					if (arg0.getSnippet().toString().equalsIgnoreCase("SAVE")) {
						if (currentLatLang != null) {
							SharedPreferences mPrefs = getSharedPreferences(
									"LOGIN_DETAIL", MODE_PRIVATE);

							/*
							 * shareUserID = new ArrayList<String>();
							 * shareLatitude = new ArrayList<String>();
							 * shareLongitude = new ArrayList<String>();
							 * shareComment = new ArrayList<String>();
							 * shareUserName = new ArrayList<String>();
							 * shareTripID = new ArrayList<String>();
							 * shareUserImage = new ArrayList<String>();
							 * sharePhone=new ArrayList<String>();
							 * sharing_type=new ArrayList<String>();
							 * location_name=new ArrayList<String>();
							 */

							Intent i = new Intent(MainActivity.this,
									CallNaviagatePickActivity.class);
							i.putExtra("STATUS", "NAVIGATE");
							i.putExtra("CURRENT_LAT", currentLatLang.latitude
									+ "");
							i.putExtra("CURRENT_LOG", currentLatLang.longitude
									+ "");
							i.putExtra("SHARE_LAT", arg0.getPosition().latitude
									+ "");
							i.putExtra("SHARE_LOG",
									arg0.getPosition().longitude + "");
							i.putExtra("PHONE", selectedphone + "");
							i.putExtra("TO_USER_ID", CLICKED_USERID);
							i.putExtra("FROM_ID",
									mPrefs.getString("USER_ID", ""));
							i.putExtra("SHARE_ID", selectedshareid);
							i.putExtra("USER_NAME", selected_user_name + "");
							startActivity(i);
						} else {
							Toast.makeText(
									MainActivity.this,
									"Sorry! can't find your current location \n Please start your GPS ",
									Toast.LENGTH_LONG).show();
						}
					} else if (arg0.getSnippet().toString()
							.equalsIgnoreCase("TRACK")) {
						if (currentLatLang != null) {
							SharedPreferences mPrefs = getSharedPreferences(
									"LOGIN_DETAIL", MODE_PRIVATE);

							Intent i = new Intent(MainActivity.this,
									CallNaviagatePickActivity.class);
							i.putExtra("STATUS", "TRACK");
							i.putExtra("CURRENT_LAT", currentLatLang.latitude
									+ "");
							i.putExtra("CURRENT_LOG", currentLatLang.longitude
									+ "");
							i.putExtra("SHARE_LAT", arg0.getPosition().latitude
									+ "");
							i.putExtra("SHARE_LOG",
									arg0.getPosition().longitude + "");
							i.putExtra("PHONE", selectedphone + "");
							i.putExtra("TO_USER_ID", CLICKED_USERID);
							i.putExtra("FROM_ID",
									mPrefs.getString("USER_ID", ""));
							i.putExtra("SHARE_ID", selectedshareid);
							i.putExtra("USER_NAME", selected_user_name + "");
							startActivity(i);
						} else {
							Toast.makeText(
									MainActivity.this,
									"Sorry! can't find your current location \n Please start your GPS ",
									Toast.LENGTH_LONG).show();
						}
					}
				}
				return false;
			}
		});

		/** open comment when get api working **/
		// getData();

		// try {
		// // "chat_seller_id", "chat_user_id",
		// // "chat_offer_id", "chat_id",
		//
		// SQLiteDatabase mdatabase = openOrCreateDatabase("CHAT_DATABASE.db",
		// Context.MODE_PRIVATE, null);
		//
		// String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
		// + "CHAT_TABLE"
		// +
		// "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_to_id TEXT,chat_from_id TEXT,chat_comment TEXT,chat_id TEXT)";
		//
		// String DATABASE_COUNT = "CREATE TABLE IF NOT EXISTS "
		// + "CHAT_COUNT"
		// +
		// "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_to_id TEXT,chat_from_id TEXT,chat_comment TEXT,chat_id TEXT)";
		//
		// mdatabase.execSQL(DATABASE_CREATE);
		// mdatabase.execSQL(DATABASE_COUNT);
		// mdatabase.close();
		//
		// SQLiteDatabase db = openOrCreateDatabase("CHAT_DATABASE.db",
		// Context.MODE_PRIVATE, null);
		// // BadgeView badge = new BadgeView(GPSActivityOnline.this, chat);
		// Cursor cursor = db.rawQuery("SELECT * FROM CHAT_COUNT ", null);
		// cursor.moveToFirst();
		// Log.e("chat user list size",
		// "chat userid:" + ",count:" + cursor.getCount());
		chatcount = (Button) findViewById(R.id.btnChatCount);
		chatcount.setTypeface(boldFont);
		// if (cursor.getCount() > 0) {
		// chatcount.setText(cursor.getCount() + "");
		// chatcount.setVisibility(View.VISIBLE);
		// } else {
		// chatcount.setVisibility(View.GONE);
		// }

		// cursor.close();
		// db.close();

		chatcount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(MainActivity.this,
						AllMessageActivity.class);

				startActivityForResult(i, 11);
				/*
				 * SharedPreferences mPrefs = getSharedPreferences(
				 * "LOGIN_DETAIL", MODE_PRIVATE);
				 * 
				 * Intent i = new Intent(MainActivity.this, ChatUserList.class);
				 * i.putExtra("TO_ID", CLICKED_USERID); i.putExtra("FROM_ID",
				 * mPrefs.getString("USER_ID", "")); startActivity(i);
				 */
			}
		});
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {

		// if (mLocationClient.isConnected()) {
		// mLocationClient.removeLocationUpdates(this);
		// }
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// Toast.makeText(this, "Menu item selected -> " + position,
		// Toast.LENGTH_SHORT).show();
		Log.e("position clicked", "position:" + position);
		if (position == 1) {
			Intent i = new Intent(MainActivity.this,
					ShareUserSavedLocation.class);
			startActivityForResult(i, 109);
			chklocation = true;

		} else if (position == 2) {
			chklocation = false;
			trackstart = false;
			if (currentLatLang != null) {
				CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
						new LatLng(currentLatLang.latitude,
								currentLatLang.longitude), 16);
				googleMap.animateCamera(update);
			}
		} else if (position == 3) {
			Intent i3 = new Intent(MainActivity.this,
					SaveLocationActivity.class);
			i3.putExtra("STATUS", "ADD");
			startActivityForResult(i3, 1);
			chklocation = false;
		} else if (position == 4) {
			Intent i = new Intent(MainActivity.this, AllMessageActivity.class);
			startActivityForResult(i, 11);
		} else if (position == 5) {
			SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
					MODE_PRIVATE);
			chklocation = false;

			Intent i = new Intent(MainActivity.this, ChatUserList.class);
			i.putExtra("TO_ID", CLICKED_USERID);
			i.putExtra("FROM_ID", mPrefs.getString("USER_ID", ""));
			startActivity(i);
		} else if (position == 6) {
			Intent i = new Intent(MainActivity.this, HistoryActivity.class);
			startActivity(i);
			chklocation = false;
		} else if (position == 7) {
			SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
					MODE_PRIVATE);
			mPrefs.edit().clear().commit();
			Intent i = new Intent(MainActivity.this, WelcomePageActivity.class);
			startActivity(i);
			finish();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen())
			mNavigationDrawerFragment.closeDrawer();
		else
			super.onBackPressed();
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	private LatLng convertLocationtoLatLang(Location location) {
		currentLatLang = new LatLng(location.getLatitude(),
				location.getLongitude());

		Log.e("CURRENT LATLONG", currentLatLang.toString());

		return currentLatLang;

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		if (chklocation == false) {
			if (trackstart == false) {
				try {
					googleMap.clear();
					Location mLastLocation = LocationServices.FusedLocationApi
							.getLastLocation(mLocationClient);
					currentLatLang = convertLocationtoLatLang(mLastLocation);
					SharedPreferences mPrefs = getSharedPreferences(
							"LOGIN_DETAIL", MODE_PRIVATE);

					setUpMap(mLastLocation.getLatitude() + "",
							mLastLocation.getLongitude() + "",
							mPrefs.getString("NAME", ""),
							mPrefs.getString("IMAGE", ""), "CURRENT", "", "");
				} catch (Exception e) {
					Log.e("error get current location", e + "");
				}
			}
		}

	}

	private void setUpMap(final String lat, final String longi,
			final String fullnameList, final String imageList,
			final String markerType, final String selected_trip_id,
			final String general_trip_id) throws IOException {
		Log.e("in setup:", "name:" + fullnameList);
		Log.e("image", "img come :" + imageList);
		// googleMap.clear();
		final View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.custom_marker_layout, null);

		final ImageView img = (ImageView) marker
				.findViewById(R.id.imvProfileImage);
		final TextView name = (TextView) marker
				.findViewById(R.id.txvProfileName);
		final ImageView baseimg = (ImageView) marker.findViewById(R.id.base);
		name.setText(fullnameList + "");
		name.setTypeface(mediumFont);
		/*
		 * Picasso.with(MainActivity.this).load(imageList) .transform(new
		 * CircleTransform()) .placeholder(R.drawable.no_image_profile)
		 * .into(profileimage, new Callback.EmptyCallback() {
		 * 
		 * @Override public void onError() { // TODO Auto-generated method stub
		 * super.onError(); Picasso.with(MainActivity.this)
		 * .load(R.drawable.no_image_profile) .transform(new CircleTransform())
		 * .into(profileimage); } });
		 */

		/*
		 * googleMap.addMarker(new MarkerOptions() .position( new
		 * LatLng(Double.parseDouble(lat), Double.parseDouble(longi)))
		 * .snippet(markerType) .icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.ic_blue_marker)));
		 */

		Picasso.with(MainActivity.this).load(imageList)
				.transform(new CircleTransform()).noFade()
				.into(img, new Callback.EmptyCallback() {
					@Override
					public void onSuccess() {
						trackMarker.add(googleMap.addMarker(new MarkerOptions()
								.position(
										new LatLng(Double.parseDouble(lat),
												Double.parseDouble(longi)))
								.snippet(markerType)
								.icon(BitmapDescriptorFactory
										.fromBitmap(createDrawableFromView(
												MainActivity.this, marker)))));
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub
						super.onError();
						Picasso.with(MainActivity.this)
								.load(R.drawable.no_image_profile)
								.transform(new CircleTransform()).into(img);

						trackMarker.add(googleMap.addMarker(new MarkerOptions()
								.position(
										new LatLng(Double.parseDouble(lat),
												Double.parseDouble(longi)))

								.snippet(markerType)
								.icon(BitmapDescriptorFactory
										.fromBitmap(createDrawableFromView(
												MainActivity.this, marker)))));
					}
				});
		if (selected_trip_id.equalsIgnoreCase(general_trip_id)) {
			if (markerType.equalsIgnoreCase("NAVIGATE"))
				baseimg.setImageResource(R.drawable.ic_blue_marker);
			else
				baseimg.setImageResource(R.drawable.ic_map_marker);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(
					Double.parseDouble(lat), Double.parseDouble(longi)), 16);
			googleMap.animateCamera(update);
		} else {
			if (markerType.equalsIgnoreCase("NAVIGATE"))
				baseimg.setImageResource(R.drawable.ic_blue_marker);
			else
				baseimg.setImageResource(R.drawable.ic_yellow_marker);
		}
		final LatLng markerLatLng = new LatLng(Double.parseDouble(lat),
				Double.parseDouble(longi));
		final View mapView = getSupportFragmentManager().findFragmentById(
				R.id.map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressLint("NewApi")
						// We check which build version we are using.
						@Override
						public void onGlobalLayout() {
							LatLngBounds bounds = new LatLngBounds.Builder()
									.include(markerLatLng).build();
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}
							// googleMap.moveCamera(CameraUpdateFactory
							// .newLatLngBounds(bounds,10, 10, 0));
						}

					});
		}

	}

	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Log.e("onactivityresylt latlongcall", "lat:long list call");
			if (requestCode == 109) {
				CameraUpdate update = CameraUpdateFactory
						.newLatLngZoom(
								new LatLng(Double.parseDouble(data
										.getStringExtra("LATITUDE")), Double
										.parseDouble(data
												.getStringExtra("LONGITUDE"))),
								18);
				googleMap.animateCamera(update);
				chklocation = true;
				
				googleMap.addMarker(new MarkerOptions()
				.position(
						new LatLng(
								Double.parseDouble(data
										.getStringExtra("LATITUDE")),
								Double.parseDouble(data
										.getStringExtra("LONGITUDE"))))
				.title(data
						.getStringExtra("LOCATION_NAME"))
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_green_marker)));
			}
			if (requestCode == 11) {
				// badge.setText(data.getStringExtra("MESSAGE_COUNT")+"");

				// ArrayList<Integer> removePos = data
				// .getIntegerArrayListExtra("REMOVE_POSITION");

				// if (removePos != null) {
				// for (int i = 0; i < removePos.size(); i++) {
				// shareUserID.remove(removePos.get(i));
				// shareLatitude.remove(removePos.get(i));
				// shareLongitude.remove(removePos.get(i));
				// shareComment.remove(removePos.get(i));
				// shareUserName.remove(removePos.get(i));
				// shareTripID.remove(removePos.get(i));
				// sharing_type.remove(removePos.get(i));
				// location_name.remove(removePos.get(i));
				// sharePhone.remove(removePos.get(i));
				// messageID.remove(removePos.get(i));
				// }
				//
				// }
				if (Integer.parseInt(data.getStringExtra("MESSAGE_COUNT")) > 0) {
					chatcount.setVisibility(View.VISIBLE);
				} else {
					chatcount.setVisibility(View.GONE);
				}
				chatcount.setText(data.getStringExtra("MESSAGE_COUNT") + "");
				if (data.getStringExtra("RESPONSE_CALL").equalsIgnoreCase(
						"CALL")) {
					CLICKED_USERID = data.getStringExtra("SHARE_USER_ID") + "";
					selectedphone = data.getStringExtra("SHARE_PHONE");
					selectedshareid = data.getStringExtra("SELECTED_TRIP_ID");
					selected_user_name = data.getStringExtra("SHARE_USER_NAME");
					if (data.getStringExtra("SHARE_TYPE").equalsIgnoreCase(
							"NAVIGATE")) {
						trackstart = false;
						if (doAsynchronousTask != null)
							doAsynchronousTask.cancel();
						// CLICKED_USERID = data.getStringExtra("SHARE_USER_ID")
						// +
						// "";
						// selectedphone = data.getStringExtra("SHARE_PHONE");
						// selectedshareid =
						// data.getStringExtra("SELECTED_TRIP_ID");
						// selected_user_name=data.getStringExtra("SHARE_USER_NAME");
						googleMap.clear();

						googleMap
								.addMarker(new MarkerOptions()
										.position(
												new LatLng(
														Double.parseDouble(data
																.getStringExtra("SHARE_LATITUDE")),
														Double.parseDouble(data
																.getStringExtra("SHARE_LONGITUDE"))))
										.snippet("SAVE")
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.ic_blue_marker)));
						CameraUpdate update = CameraUpdateFactory
								.newLatLngZoom(
										new LatLng(
												Double.parseDouble(data
														.getStringExtra("SHARE_LATITUDE")),
												Double.parseDouble(data
														.getStringExtra("SHARE_LONGITUDE"))),
										18);
						googleMap.animateCamera(update);
					} else {
						trackstart = true;
						// Toast.makeText(GPSActivityOnline.this, "tripid:"
						// + shareTripId.get(position), Toast.LENGTH_LONG);
						Log.e("share trip id",
								"" + data.getStringExtra("SELECTED_TRIP_ID"));
						selected_trip_id = data
								.getStringExtra("SELECTED_TRIP_ID") + "";
						if (!flag) {
							sendFrequentData();
							flag = true;
						} else {
							getUserData(selected_trip_id);
						}

					}
				}
			}
		}
	}

	public void getUserData(final String trip_id) {
		String tag_json_obj = "json_obj_req";
		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		final String user_id = mPrefs.getString("USER_ID", "");
		String url = ApplicationData.serviceURL
				+ "get_seller_trip_location.php";
		Log.e("url", url + "");
		// final ProgressDialog mProgressDialog = new
		// ProgressDialog(MainActivity.this);
		// mProgressDialog.setTitle("");
		// mProgressDialog.setCanceledOnTouchOutside(false);
		// mProgressDialog.setMessage("Please Wait...");
		// mProgressDialog.show();

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("FinishTrip", response.toString());

						shareUserID = new ArrayList<String>();
						shareLatitude = new ArrayList<String>();
						shareLongitude = new ArrayList<String>();
						shareComment = new ArrayList<String>();
						shareUserName = new ArrayList<String>();
						shareTripID = new ArrayList<String>();
						shareUserImage = new ArrayList<String>();
						sharePhone = new ArrayList<String>();
						sharing_type = new ArrayList<String>();
						location_name = new ArrayList<String>();
						try {
							JSONObject jsob = new JSONObject(response
									.toString());
							if (jsob.getString("msg").equalsIgnoreCase(
									"Success")) {
								JSONArray datarray = jsob.getJSONArray("data");
								if (trackMarker != null)
									for (int j = 0; j < trackMarker.size(); j++) {
										trackMarker.remove(j);
									}

								// Log.e("array length", "" +
								// datarray.length());
								for (int i = 0; i < datarray.length(); i++) {
									JSONObject dataOb = datarray
											.getJSONObject(i);
									shareUserID.add(dataOb.getString("userid"));
									shareLatitude.add(dataOb
											.getString("latitude"));
									shareLongitude.add(dataOb
											.getString("longitude"));
									shareComment.add(dataOb
											.getString("comments"));
									shareUserName.add(dataOb
											.getString("username"));
									shareTripID.add(dataOb
											.getString("share_trip_id"));
									shareUserImage.add(dataOb
											.getString("user_image"));
									sharePhone.add(dataOb.getString("phone"));
									sharing_type.add(dataOb
											.getString("sharing_type"));
									location_name.add(dataOb
											.getString("location_name"));

									// if (trip_id.equalsIgnoreCase(dataOb
									// .getString("share_trip_id"))) {
									Log.e("tracking location matched", ""
											+ shareUserImage.get(i));
									googleMap.clear();
									setUpMap(shareLatitude.get(i),
											shareLongitude.get(i),
											shareUserName.get(i),
											shareUserImage.get(i),
											sharing_type.get(i), trip_id,
											shareTripID.get(i));

									// }
								}
								if (datarray.length() > 0) {
									// badge.setText(datarray.length() + "");
									chatcount.setText(datarray.length() + "");
									// badge.show();
									chatcount.setVisibility(View.VISIBLE);
								} else {
									// badge.hide();
									chatcount.setVisibility(View.GONE);
								}
							}
						} catch (Exception e) {
							Log.e("get user data error", "" + e);
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// mProgressDialog.dismiss();
						VolleyLog.e("FinishTrip Error",
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
				return params;
			}
		};
		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		// Adding request to request queue
		ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
				tag_json_obj);

		/*
		 * SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
		 * MODE_PRIVATE); String user_id = mPrefs.getString("USER_ID", "");
		 * 
		 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost;
		 * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		 * 
		 * httppost = new HttpPost(ApplicationData.serviceURL +
		 * "get_seller_trip_location.php"); StringBuffer sb = new
		 * StringBuffer(); try { // Add your data nameValuePairs.add(new
		 * BasicNameValuePair("userid", user_id)); httppost.setEntity(new
		 * UrlEncodedFormEntity(nameValuePairs));
		 * 
		 * // Execute HTTP Post Request HttpResponse response =
		 * httpclient.execute(httppost); BufferedReader in = new
		 * BufferedReader(new InputStreamReader(
		 * response.getEntity().getContent())); sb = new StringBuffer("");
		 * String line = ""; while ((line = in.readLine()) != null) {
		 * sb.append(line); } in.close(); Log.e("get share Data", "" +
		 * sb.toString()); // return sb.toString(); } catch (Exception e) {
		 * Log.e("problem share data getting", "" + e); // return ""; }
		 * 
		 * // }
		 * 
		 * // @Override // protected void onPostExecute(String result) {
		 * shareUserID = new ArrayList<String>(); shareLatitude = new
		 * ArrayList<String>(); shareLongitude = new ArrayList<String>();
		 * shareComment = new ArrayList<String>(); shareUserName = new
		 * ArrayList<String>(); shareTripID = new ArrayList<String>();
		 * shareUserImage = new ArrayList<String>(); sharePhone = new
		 * ArrayList<String>(); sharing_type = new ArrayList<String>();
		 * location_name = new ArrayList<String>(); try { JSONObject jsob = new
		 * JSONObject(sb.toString()); if
		 * (jsob.getString("msg").equalsIgnoreCase("Success")) { JSONArray
		 * datarray = jsob.getJSONArray("data"); if(trackMarker!=null) for(int
		 * j=0;j<trackMarker.size();j++){ trackMarker.remove(j); }
		 * 
		 * // Log.e("array length", "" + datarray.length()); for (int i = 0; i <
		 * datarray.length(); i++) { JSONObject dataOb =
		 * datarray.getJSONObject(i);
		 * shareUserID.add(dataOb.getString("userid"));
		 * shareLatitude.add(dataOb.getString("latitude"));
		 * shareLongitude.add(dataOb.getString("longitude"));
		 * shareComment.add(dataOb.getString("comments"));
		 * shareUserName.add(dataOb.getString("username"));
		 * shareTripID.add(dataOb.getString("share_trip_id"));
		 * shareUserImage.add(dataOb.getString("user_image"));
		 * sharePhone.add(dataOb.getString("phone"));
		 * sharing_type.add(dataOb.getString("sharing_type"));
		 * location_name.add(dataOb.getString("location_name"));
		 * 
		 * 
		 * messageID.add(dataOb.getString("id"));
		 * shareUserID.add(dataOb.getString("userid"));
		 * shareLatitude.add(dataOb.getString("latitude"));
		 * shareLongitude.add(dataOb.getString("longitude"));
		 * shareComment.add(dataOb.getString("comments"));
		 * shareUserName.add(dataOb.getString("username"));
		 * shareTripID.add(dataOb.getString("share_trip_id"));
		 * sharing_type.add(dataOb.getString("sharing_type")); location_name
		 * .add(dataOb.getString("location_name"));
		 * sharePhone.add(dataOb.getString("phone"));
		 * 
		 * // if (trip_id.equalsIgnoreCase(dataOb //
		 * .getString("share_trip_id"))) { Log.e("tracking location matched", ""
		 * + shareUserImage.get(i));
		 * 
		 * setUpMap(shareLatitude.get(i), shareLongitude.get(i),
		 * shareUserName.get(i), shareUserImage.get(i), "TRACK", trip_id,
		 * shareTripID.get(i));
		 * 
		 * // } } if (datarray.length() > 0) { //
		 * badge.setText(datarray.length() + "");
		 * chatcount.setText(datarray.length() + ""); // badge.show();
		 * chatcount.setVisibility(View.VISIBLE); } else { // badge.hide();
		 * chatcount.setVisibility(View.GONE); } } } catch (Exception e) {
		 * Log.e("get user data error", "" + e); e.printStackTrace(); }
		 */
		// }
		// }.execute();

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

	public class MapTypeAdapter extends BaseAdapter {
		ArrayList<String> typeList;
		ArrayList<Integer> imageList;
		LayoutInflater inflater;

		public MapTypeAdapter(ArrayList<String> typeList,
				ArrayList<Integer> imageList) {
			this.typeList = typeList;
			this.imageList = imageList;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return typeList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView txvMapDetail;
			ImageView mapImage;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_map_type, parent,
						false);
				holder = new ViewHolder();
				holder.txvMapDetail = (TextView) convertView
						.findViewById(R.id.txvMapName);
				holder.mapImage = (ImageView) convertView
						.findViewById(R.id.imvMapImage);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txvMapDetail.setTypeface(mediumFont);
			holder.txvMapDetail.setText(typeList.get(position));
			holder.mapImage.setImageResource(imageList.get(position));
			return convertView;
		}

	}

	public void sendFrequentData() {

		final Handler handler = new Handler();
		Timer timer = new Timer();

		doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {

							getUserData(selected_trip_id);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 10000);

	}

	public void getMySharedLocation(final String user_id) {

		String tag_json_obj = "json_obj_req";

		String url = ApplicationData.serviceURL
				+ "get_seller_trip_location.php";
		Log.e("url", url + "");
		// final ProgressDialog mProgressDialog = new ProgressDialog(
		// MainActivity.this);
		// mProgressDialog.setTitle("");
		// mProgressDialog.setCanceledOnTouchOutside(false);
		// mProgressDialog.setMessage("Please Wait...");
		// mProgressDialog.show();

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("get_seller_trip_location", response.toString());
						// mProgressDialog.dismiss();
						shareUserID = new ArrayList<String>();
						shareLatitude = new ArrayList<String>();
						shareLongitude = new ArrayList<String>();
						shareComment = new ArrayList<String>();
						shareUserName = new ArrayList<String>();
						shareTripID = new ArrayList<String>();
						sharing_type = new ArrayList<String>();
						location_name = new ArrayList<String>();
						sharePhone = new ArrayList<String>();
						messageID = new ArrayList<String>();
						try {
							JSONObject jsob = new JSONObject(response
									.toString());
							if (jsob.getString("msg").equalsIgnoreCase(
									"Success")) {
								JSONArray datarray = jsob.getJSONArray("data");
								// Log.e("array length", "" +
								// datarray.length());
								for (int i = 0; i < datarray.length(); i++) {
									JSONObject dataOb = datarray
											.getJSONObject(i);
									messageID.add(dataOb.getString("id"));
									shareUserID.add(dataOb.getString("userid"));
									shareLatitude.add(dataOb
											.getString("latitude"));
									shareLongitude.add(dataOb
											.getString("longitude"));
									shareComment.add(dataOb
											.getString("comments"));
									shareUserName.add(dataOb
											.getString("username"));
									shareTripID.add(dataOb
											.getString("share_trip_id"));
									sharing_type.add(dataOb
											.getString("sharing_type"));
									location_name.add(dataOb
											.getString("location_name"));
									sharePhone.add(dataOb.getString("phone"));
								}

								if (datarray.length() > 0) {
									// badge.setText(datarray.length() + "");
									chatcount.setText(datarray.length() + "");
									chatcount.setVisibility(View.VISIBLE);
									// badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
									// badge.setX(-30);

									// badge.show();
								} else {
									chatcount.setVisibility(View.GONE);
									// badge.hide();
								}
							}
						} catch (Exception e) {
							Log.e("getMySharedLocation set error", "" + e);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// mProgressDialog.dismiss();
						VolleyLog.e("get_seller_trip_location Error", "Error: "
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
		 * new AsyncTask<Void, Void, String>() { // ProgressDialog
		 * mProgressDialog;
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override protected String doInBackground(Void... params) { // TODO
		 * Auto-generated method stub
		 * 
		 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost;
		 * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
		 * 1);
		 * 
		 * httppost = new HttpPost(ApplicationData.serviceURL +
		 * "get_seller_trip_location.php"); StringBuffer sb = new
		 * StringBuffer(); try { // Add your data nameValuePairs .add(new
		 * BasicNameValuePair("userid", user_id)); httppost.setEntity(new
		 * UrlEncodedFormEntity(nameValuePairs));
		 * 
		 * // Execute HTTP Post Request HttpResponse response =
		 * httpclient.execute(httppost); BufferedReader in = new BufferedReader(
		 * new InputStreamReader(response.getEntity() .getContent())); sb = new
		 * StringBuffer(""); String line = ""; while ((line = in.readLine()) !=
		 * null) { sb.append(line); } in.close();
		 * Log.e("getMySharedLocation Data", "" + sb.toString()); return
		 * sb.toString(); } catch (Exception e) {
		 * Log.e("getMySharedLocation error", "" + e); return ""; }
		 * 
		 * }
		 * 
		 * protected void onPostExecute(String result) { //
		 * mProgressDialog.dismiss(); // googleMap.clear(); shareUserID = new
		 * ArrayList<String>(); shareLatitude = new ArrayList<String>();
		 * shareLongitude = new ArrayList<String>(); shareComment = new
		 * ArrayList<String>(); shareUserName = new ArrayList<String>();
		 * shareTripID = new ArrayList<String>(); sharing_type = new
		 * ArrayList<String>(); location_name = new ArrayList<String>();
		 * sharePhone = new ArrayList<String>(); messageID = new
		 * ArrayList<String>(); try { JSONObject jsob = new
		 * JSONObject(result.toString()); if
		 * (jsob.getString("msg").equalsIgnoreCase("Success")) { JSONArray
		 * datarray = jsob.getJSONArray("data"); // Log.e("array length", "" +
		 * datarray.length()); for (int i = 0; i < datarray.length(); i++) {
		 * JSONObject dataOb = datarray.getJSONObject(i);
		 * messageID.add(dataOb.getString("id"));
		 * shareUserID.add(dataOb.getString("userid"));
		 * shareLatitude.add(dataOb.getString("latitude"));
		 * shareLongitude.add(dataOb.getString("longitude"));
		 * shareComment.add(dataOb.getString("comments"));
		 * shareUserName.add(dataOb.getString("username"));
		 * shareTripID.add(dataOb.getString("share_trip_id"));
		 * sharing_type.add(dataOb.getString("sharing_type")); location_name
		 * .add(dataOb.getString("location_name"));
		 * sharePhone.add(dataOb.getString("phone")); }
		 * 
		 * if (datarray.length() > 0) { // badge.setText(datarray.length() +
		 * ""); chatcount.setText(datarray.length() + "");
		 * chatcount.setVisibility(View.VISIBLE); //
		 * badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT); //
		 * badge.setX(-30);
		 * 
		 * // badge.show(); } else { chatcount.setVisibility(View.GONE); //
		 * badge.hide(); } } } catch (Exception e) {
		 * Log.e("getMySharedLocation set error", "" + e); } } }.execute();
		 */

	}

	public void getSaveLocationData() {

		String tag_json_obj = "json_obj_req";

		String url = ApplicationData.serviceURL + "get_user_location.php";
		Log.e("url", url + "");

		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		final String user_id = mPrefs.getString("USER_ID", "");

		final ProgressDialog mProgressDialog = new ProgressDialog(
				MainActivity.this);
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
								JSONArray dataArr = object.getJSONArray("data");
								for (int i = 0; i < dataArr.length(); i++) {
									JSONObject dataOb = dataArr
											.getJSONObject(i);
									// location_id_list.add(dataOb.getString("location_id"));
									// location_name_list.add(dataOb
									// .getString("location_name"));
									// latitude_list.add(dataOb.getString("latitude"));
									// longitude_list.add(dataOb.getString("longitude"));
									googleMap.addMarker(new MarkerOptions()
											.position(
													new LatLng(
															Double.parseDouble(dataOb
																	.getString("latitude")),
															Double.parseDouble(dataOb
																	.getString("longitude"))))
											.title(dataOb
													.getString("location_name"))
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.ic_green_marker)));

								}
								// adapter = new
								// SaveLocationDataAdapter(location_id_list,
								// location_name_list, latitude_list,
								// longitude_list);
								// savelocationlist.setAdapter(adapter);
							} else {
								Toast.makeText(
										MainActivity.this,
										"Sorry! we can't find any saved location",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(
									MainActivity.this,
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
		 * Log.e("get saved Data", "" + sb.toString()); return sb.toString(); }
		 * catch (Exception e) { Log.e("get saved data error", "" + e); return
		 * ""; } }
		 * 
		 * @Override protected void onPostExecute(String result) { // TODO
		 * Auto-generated method stub super.onPostExecute(result); //
		 * location_name_list = new ArrayList<String>(); // latitude_list = new
		 * ArrayList<String>(); // longitude_list = new ArrayList<String>(); //
		 * location_id_list=new ArrayList<String>(); try {
		 * 
		 * mProgressDialog.dismiss(); JSONObject object = new
		 * JSONObject(result.toString());
		 * 
		 * String msg = object.getString("msg"); if
		 * (msg.equalsIgnoreCase("Success")) { JSONArray dataArr =
		 * object.getJSONArray("data"); for (int i = 0; i < dataArr.length();
		 * i++) { JSONObject dataOb = dataArr.getJSONObject(i); //
		 * location_id_list.add(dataOb.getString("location_id")); //
		 * location_name_list.add(dataOb // .getString("location_name")); //
		 * latitude_list.add(dataOb.getString("latitude")); //
		 * longitude_list.add(dataOb.getString("longitude")); googleMap
		 * .addMarker(new MarkerOptions() .position( new LatLng(
		 * Double.parseDouble(dataOb .getString("latitude")),
		 * Double.parseDouble(dataOb .getString("longitude")))) .title(dataOb
		 * .getString("location_name")) .icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.ic_green_marker)));
		 * 
		 * } // adapter = new // SaveLocationDataAdapter(location_id_list, //
		 * location_name_list, latitude_list, // longitude_list); //
		 * savelocationlist.setAdapter(adapter); } else {
		 * Toast.makeText(MainActivity.this,
		 * "Sorry! we can't find any saved location",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); Toast.makeText( MainActivity.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } }
		 * 
		 * @Override protected void onPreExecute() { // TODO Auto-generated
		 * method stub super.onPreExecute(); mProgressDialog = new
		 * ProgressDialog(MainActivity.this); mProgressDialog.setTitle("");
		 * mProgressDialog.setCanceledOnTouchOutside(false);
		 * mProgressDialog.setMessage("Please Wait..."); mProgressDialog.show();
		 * 
		 * } }.execute();
		 */
	}

	public void userForwardList(final String lat, final String log,
			final String location_id) {

		String tag_json_obj = "json_obj_req";
		String url = ApplicationData.serviceURL + "all_bonjour_user.php";
		Log.e("url", url + "");

		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		final String user_id = mPrefs.getString("USER_ID", "");

		final ProgressDialog mProgressDialog = new ProgressDialog(
				MainActivity.this);
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
										MainActivity.this,
										"Sorry! we are stuff to fetching data. \n Please try again!",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(
									MainActivity.this,
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
		 * } else { Toast.makeText( MainActivity.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); Toast.makeText( MainActivity.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } }
		 * 
		 * @Override protected void onPreExecute() { // TODO Auto-generated
		 * method stub super.onPreExecute(); mProgressDialog = new
		 * ProgressDialog(MainActivity.this); mProgressDialog.setTitle("");
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

		picker = new Dialog(MainActivity.this);
		picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picker.setContentView(R.layout.list_select_person_dialog);

		final ListView itemlist = (ListView) picker
				.findViewById(R.id.lsvItemDialog);
		TextView selectPersonTitle = (TextView) picker
				.findViewById(R.id.txvSelectPerson);
		Button forward = (Button) picker.findViewById(R.id.btnShareLocation);
		Button share = (Button) picker.findViewById(R.id.btnShareViaOtherApps);
		final AutoCompleteTextView search_user = (AutoCompleteTextView) picker
				.findViewById(R.id.autoCompleteTextView1);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, forward_name);
		search_user.setAdapter(adapter1);
		forward.setText("Share Location");
		forward.setTypeface(boldFont);

		share.setTypeface(boldFont);
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
					Toast.makeText(MainActivity.this,
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
			/*holder.chbForwardUser
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							checkbox_val.set(position, isChecked + "");
						}
					});*/
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
		String url = ApplicationData.serviceURL + "share_location.php";
		Log.e("url", url + "");

		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		final String user_id = mPrefs.getString("USER_ID", "");

		final ProgressDialog mProgressDialog = new ProgressDialog(
				MainActivity.this);
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
							JSONObject object = new JSONObject(response
									.toString());

							String msg = object.getString("msg");
							if (msg.equalsIgnoreCase("Success")) {
								JSONObject dataob = object
										.getJSONObject("data");
								picker.dismiss();
								SharedPreferences mPrefs = getSharedPreferences(
										"LOGIN_DETAIL", MODE_PRIVATE);
								Editor edit = mPrefs.edit();
								edit.putString("SHARE_TRIP_ID",
										dataob.getString("last_share_id"));
								edit.commit();
								Toast.makeText(
										MainActivity.this,
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
								setRecurringAlarm(MainActivity.this);
							} else {
								Toast.makeText(
										MainActivity.this,
										"Sorry! we are stuff to fetching data. \n Please try again!",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(
									MainActivity.this,
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

		/*
		 * new AsyncTask<Void, Void, String>() { ProgressDialog mProgressDialog;
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override protected String doInBackground(Void... params) { // TODO
		 * Auto-generated method stub SharedPreferences mPrefs =
		 * getSharedPreferences("LOGIN_DETAIL", MODE_PRIVATE); String user_id =
		 * mPrefs.getString("USER_ID", "");
		 * 
		 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost =
		 * new HttpPost(ApplicationData.serviceURL + "share_location.php"); try
		 * { Log.e("sharing latlong", lat + "::" + log); // Add your data
		 * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
		 * 1); nameValuePairs .add(new BasicNameValuePair("userid", user_id));
		 * nameValuePairs.add(new BasicNameValuePair("to_userid",
		 * selected_user_id.substring(1, selected_user_id.length())));
		 * nameValuePairs.add(new BasicNameValuePair("latitude", lat));
		 * nameValuePairs .add(new BasicNameValuePair("longitude", log));
		 * nameValuePairs.add(new BasicNameValuePair("comment", "nothing"));
		 * nameValuePairs.add(new BasicNameValuePair("sharing_type",
		 * share_type)); nameValuePairs.add(new
		 * BasicNameValuePair("visible_time", selectedtime));
		 * nameValuePairs.add(new BasicNameValuePair("location_id",
		 * location_id));
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
		 * checkbox_val = new ArrayList<String>(); try {
		 * 
		 * mProgressDialog.dismiss(); JSONObject object = new
		 * JSONObject(result.toString());
		 * 
		 * String msg = object.getString("msg"); if
		 * (msg.equalsIgnoreCase("Success")) { JSONObject dataob =
		 * object.getJSONObject("data"); picker.dismiss(); SharedPreferences
		 * mPrefs = getSharedPreferences( "LOGIN_DETAIL", MODE_PRIVATE); Editor
		 * edit = mPrefs.edit(); edit.putString("SHARE_TRIP_ID",
		 * dataob.getString("last_share_id")); edit.commit();
		 * Toast.makeText(MainActivity.this,
		 * "Thank you! your location shared successfully",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * LocationManager locationManager = (LocationManager)
		 * getSystemService(LOCATION_SERVICE); if (locationManager
		 * .isProviderEnabled(LocationManager.GPS_PROVIDER)) { //
		 * Toast.makeText(this, // "GPS is Enabled in your device", //
		 * Toast.LENGTH_SHORT).show(); } else { showGPSDisabledAlertToUser(); }
		 * setRecurringAlarm(MainActivity.this); } else { Toast.makeText(
		 * MainActivity.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); Toast.makeText( MainActivity.this,
		 * "Sorry! we are stuff to fetching data. \n Please try again!",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } }
		 * 
		 * @Override protected void onPreExecute() { // TODO Auto-generated
		 * method stub super.onPreExecute(); mProgressDialog = new
		 * ProgressDialog(MainActivity.this); mProgressDialog.setTitle("");
		 * mProgressDialog.setCanceledOnTouchOutside(false);
		 * mProgressDialog.setMessage("Please Wait..."); mProgressDialog.show();
		 * 
		 * } }.execute();
		 */
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
}