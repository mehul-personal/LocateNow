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
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends Activity {
	ListView historyList;
	ArrayList<String> user_id_list, sharing_type, share_status, user_list,
			phone_list, location_name_list, sharing_time, history_id_list;
	HistoryDataAdapter adapter;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.myPrimaryColor)));
		//actionBar.setTitle("History");
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		historyList = (ListView) findViewById(R.id.lsvHistoryList);
		
		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		SpannableString s = new SpannableString("History");
	    s.setSpan(new TypefaceSpan("Lato-Medium.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    actionBar.setTitle(s);
		
		getHistoryDetail();
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

	public void getHistoryDetail() {
		new AsyncTask<Void, Void, String>() {
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
						+ "history.php");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs
							.add(new BasicNameValuePair("userid", user_id));

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
					Log.e("get history Data", "" + sb.toString());
					return sb.toString();
				} catch (Exception e) {
					Log.e("history error", "" + e);
					return "";
				}
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				user_id_list = new ArrayList<String>();
				sharing_type = new ArrayList<String>();
				share_status = new ArrayList<String>();
				user_list = new ArrayList<String>();
				phone_list = new ArrayList<String>();
				location_name_list = new ArrayList<String>();
				sharing_time = new ArrayList<String>();
				history_id_list = new ArrayList<String>();
				try {

					mProgressDialog.dismiss();
					JSONObject object = new JSONObject(result.toString());

					String msg = object.getString("msg");
					if (msg.equalsIgnoreCase("Success")) {
						JSONArray dataArr = object.getJSONArray("data");
						for (int i = 0; i < dataArr.length(); i++) {
							JSONObject dataOb = dataArr.getJSONObject(i);
							user_id_list.add(dataOb.getString("userid"));
							location_name_list.add(dataOb
									.getString("location_name"));
							sharing_type.add(dataOb.getString("sharing_type"));
							share_status.add(dataOb.getString("share_status"));
							user_list.add(dataOb.getString("username"));
							phone_list.add(dataOb.getString("phone"));
							history_id_list.add(dataOb.getString("id"));
							sharing_time.add(dataOb.getString("created_at"));
						}
						adapter = new HistoryDataAdapter(user_id_list,
								sharing_type, share_status, user_list,
								phone_list, location_name_list, sharing_time,history_id_list);
						historyList.setAdapter(adapter);
					} else {
						Toast.makeText(HistoryActivity.this,
								"Sorry! we can't find any History",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(
							HistoryActivity.this,
							"Sorry! we are stuff to fetching data. \n Please try again!",
							Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(HistoryActivity.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please Wait...");
				mProgressDialog.show();

			}
		}.execute();
	}

	public class HistoryDataAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<String> uid_list, stype, statusList, unamelist, pnamelist,
				lnamelist, timelist,hid;

		int m = 0;

		public HistoryDataAdapter(ArrayList<String> uid_list,
				ArrayList<String> stype, ArrayList<String> statusList,
				ArrayList<String> unamelist, ArrayList<String> pnamelist,
				ArrayList<String> lnamelist, ArrayList<String> timelist,ArrayList<String> hid) {
			// TODO Auto-generated constructor stub
			this.uid_list = uid_list;
			this.stype = stype;
			this.statusList = statusList;
			this.unamelist = unamelist;
			this.pnamelist = pnamelist;
			this.lnamelist = lnamelist;
			this.timelist = timelist;
			this.hid=hid;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			TextView StatusName, StatusTime;
			ImageView status;

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

				convertView = inflater.inflate(R.layout.row_history_data,
						parent, false);
				holder = new ViewHolder();
				holder.StatusName = (TextView) convertView
						.findViewById(R.id.txvHistoryStatusName);
				holder.status = (ImageView) convertView
						.findViewById(R.id.imvHistoryStatus);
				holder.StatusTime = (TextView) convertView
						.findViewById(R.id.txvHistoryStatusTime);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.StatusTime.setText(timelist.get(position));
			holder.StatusName.setTypeface(mediumFont);
			holder.StatusTime.setTypeface(regularFont);
			if (stype.get(position).equalsIgnoreCase("NAVIGATE")) {
				holder.StatusName.setText(unamelist.get(position)
						+ " has shared you a location -"
						+ lnamelist.get(position));
				Log.e("navigate msg",
						unamelist.get(position)
								+ " has shared you a location -"
								+ lnamelist.get(position));
			} else {
				holder.StatusName.setText("Track " + unamelist.get(position)
						+ "'s mobile location");
			}
			if (statusList.get(position).equalsIgnoreCase("Finished")) {
				holder.status.setImageResource(R.drawable.ic_red_notifier);
			} else {
				holder.status.setImageResource(R.drawable.ic_green_notifier);
			}
			holder.StatusName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRestartLocationSharing(hid.get(position));
				}
			});
			holder.status.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRestartLocationSharing(hid.get(position));
				}
			});
			holder.StatusTime.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRestartLocationSharing(hid.get(position));
				}
			});
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return uid_list.size();
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
	public void setRestartLocationSharing(final String history_id) {
		new AsyncTask<Void, Void, String>() {
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
						+ "restart_share.php");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs
							.add(new BasicNameValuePair("userid", user_id));
					nameValuePairs
					.add(new BasicNameValuePair("history_id", history_id));

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
					Log.e("get history Data", "" + sb.toString());
					return sb.toString();
				} catch (Exception e) {
					Log.e("history error", "" + e);
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

					String msg = object.getString("msg");
					if (msg.equalsIgnoreCase("Success")) {
						Toast.makeText(HistoryActivity.this,
								"Your location sharing successfully restarted",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(HistoryActivity.this,
								"Sorry! we can't restart your shared location",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(
							HistoryActivity.this,
							"Sorry! we are stuff to fetching data. \n Please try again!",
							Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(HistoryActivity.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please Wait...");
				mProgressDialog.show();

			}
		}.execute();
	}
}
