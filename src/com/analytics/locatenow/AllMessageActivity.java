package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class AllMessageActivity extends Activity {
	static ArrayList<String> shareUserID, shareLatitude, shareLongitude,
			shareComment, shareUserName, shareTripID, sharing_type,
			location_name, sharePhone, shareUserImage, messageID
			;
	ListView allmessagelist;
	static String selectedphone = "", selectedshareid = "",
			CLICKED_USERID = "", selected_trip_id = "";
	public static TimerTask doAsynchronousTask;
	Button cancel;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	TextView messageTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_all_messages);
		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");

		messageTitle = (TextView) findViewById(R.id.txvAllMsgTitle);
		allmessagelist = (ListView) findViewById(R.id.lsvAllMessageList);
		cancel = (Button) findViewById(R.id.btnAllMessageCancel);

		messageTitle.setTypeface(boldFont);
		cancel.setTypeface(boldFont);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.putExtra("RESPONSE_CALL", "CANCEL");
				i.putExtra("MESSAGE_COUNT", location_name.size() + "");
				setResult(11, i);
				finish();
			}
		});
		SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
				MODE_PRIVATE);
		String user_id = mPrefs.getString("USER_ID", "");
		getMySharedLocation(user_id);

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
								ShareLocationAdapter adapter = new ShareLocationAdapter(
										shareUserID, shareLatitude,
										shareLongitude, shareComment,
										shareUserName, shareTripID,
										sharing_type, location_name,
										sharePhone, messageID);
								allmessagelist.setAdapter(adapter);
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
	}

	public class ShareLocationAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<String> shareUserID, shareLatitude, shareLongitude,
				shareComment, shareUserName, shareTripId, shareType,
				sharePhone, messageid, shareLocationname;

		int m = 0;

		public ShareLocationAdapter(ArrayList<String> shareUserID,
				ArrayList<String> shareLatitude,
				ArrayList<String> shareLongitude,
				ArrayList<String> shareComment,
				ArrayList<String> shareUserName, ArrayList<String> shareTripID,
				ArrayList<String> shareType,
				ArrayList<String> shareLocationname,
				ArrayList<String> sharePhone, ArrayList<String> messageID) {
			// TODO Auto-generated constructor stub
			this.shareUserID = shareUserID;
			this.shareLatitude = shareLatitude;
			this.shareLongitude = shareLongitude;
			this.shareComment = shareComment;
			this.shareUserName = shareUserName;
			this.shareTripId = shareTripID;
			this.shareLocationname = shareLocationname;
			this.shareType = shareType;
			this.sharePhone = sharePhone;
			this.messageid = messageID;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void removeRow(int position) {

			shareUserID.remove(position);
			shareLatitude.remove(position);
			shareLongitude.remove(position);
			shareComment.remove(position);
			shareUserName.remove(position);
			shareTripId.remove(position);
			shareType.remove(position);
			shareLocationname.remove(position);
			sharePhone.remove(position);
			messageid.remove(position);
			notifyDataSetChanged();

		}

		class ViewHolder {
			TextView txvShareDesc;
			ImageButton delete;
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

				convertView = inflater.inflate(R.layout.row_all_message_item,
						parent, false);
				holder = new ViewHolder();
				holder.txvShareDesc = (TextView) convertView
						.findViewById(R.id.txvShareLocationDescription);
				holder.delete = (ImageButton) convertView
						.findViewById(R.id.imbShareLocationDelete);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txvShareDesc.setTypeface(regularFont);
			if (shareType.get(position).equalsIgnoreCase("NAVIGATE")) {
				holder.txvShareDesc.setText(shareUserName.get(position)
						+ " has shared you a location -"
						+ shareLocationname.get(position));
			} else {
				holder.txvShareDesc.setText("Track "
						+ shareUserName.get(position) + "'s mobile location");
			}
			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					removeMessage(messageid.get(position), position);
				}
			});
			holder.txvShareDesc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent i = new Intent();

					i.putExtra("RESPONSE_CALL", "CALL");
					i.putExtra("SHARE_TYPE", "" + shareType.get(position));
					i.putExtra("SELECTED_TRIP_ID", shareTripId.get(position)
							+ "");
					i.putExtra("SHARE_USER_ID", shareUserID.get(position) + "");
					i.putExtra("SHARE_PHONE", sharePhone.get(position) + "");
					i.putExtra("SHARE_LATITUDE", shareLatitude.get(position)
							+ "");
					i.putExtra("SHARE_LONGITUDE", shareLongitude.get(position)
							+ "");
					i.putExtra("MESSAGE_COUNT", shareLocationname.size() + "");
					i.putExtra("SHARE_USER_NAME", shareUserName.get(position)
							+ "");
					setResult(11, i);
					finish();

				}
			});
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return shareUserID.size();
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

		public void removeMessage(final String messageID, final int position) {

			new AsyncTask<Void, Void, String>() {
				ProgressDialog mProgressDialog;

				@SuppressWarnings("deprecation")
				@Override
				protected String doInBackground(Void... params) {
					// TODO Auto-generated method stub

					SharedPreferences mPrefs = getSharedPreferences(
							"LOGIN_DETAIL", MODE_PRIVATE);
					String user_id = mPrefs.getString("USER_ID", "");

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost;
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);

					httppost = new HttpPost(ApplicationData.serviceURL
							+ "remove_message.php");
					StringBuffer sb = new StringBuffer();
					try {
						// Add your data
						nameValuePairs.add(new BasicNameValuePair("message_id",
								messageID));
						httppost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));

						// Execute HTTP Post Request
						HttpResponse response = httpclient.execute(httppost);
						BufferedReader in = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						sb = new StringBuffer("");
						String line = "";
						while ((line = in.readLine()) != null) {
							sb.append(line);
						}
						in.close();
						Log.e("remove_message Data", "" + sb.toString());
						return sb.toString();
					} catch (Exception e) {
						Log.e("remove_message error", "" + e);
						return "";
					}

				}

				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					try {
						mProgressDialog.dismiss();
						JSONObject jsob = new JSONObject(result.toString());
						if (jsob.getString("msg").equalsIgnoreCase("Success")) {
							removeRow(position);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
					mProgressDialog = new ProgressDialog(
							AllMessageActivity.this);
					mProgressDialog.setTitle("");
					mProgressDialog.setCanceledOnTouchOutside(false);
					mProgressDialog.setMessage("Please Wait...");
					mProgressDialog.show();

				}
			}.execute();

		}
	}
	// public void sendFrequentData() {
	//
	// final Handler handler = new Handler();
	// Timer timer = new Timer();
	//
	// doAsynchronousTask = new TimerTask() {
	// @Override
	// public void run() {
	// handler.post(new Runnable() {
	// public void run() {
	// try {
	//
	// getUserData(selected_trip_id);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	// };
	// timer.schedule(doAsynchronousTask, 0, 10000);
	//
	// }
	// @SuppressWarnings("deprecation")
	// public void getUserData(final String trip_id) {
	// // new AsyncTask<Void, Void, String>() {
	// // ProgressDialog mProgressDialog;
	// //
	// // @SuppressWarnings("deprecation")
	// // @Override
	// // protected String doInBackground(Void... params) {
	// // // TODO Auto-generated method stub
	//
	// SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
	// MODE_PRIVATE);
	// String user_id = mPrefs.getString("USER_ID", "");
	//
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost;
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	//
	// httppost = new HttpPost(ApplicationData.serviceURL
	// + "get_seller_trip_location.php");
	// StringBuffer sb = new StringBuffer();
	// try {
	// // Add your data
	// nameValuePairs.add(new BasicNameValuePair("userid", user_id));
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	//
	// // Execute HTTP Post Request
	// HttpResponse response = httpclient.execute(httppost);
	// BufferedReader in = new BufferedReader(new InputStreamReader(
	// response.getEntity().getContent()));
	// sb = new StringBuffer("");
	// String line = "";
	// while ((line = in.readLine()) != null) {
	// sb.append(line);
	// }
	// in.close();
	// Log.e("get share Data", "" + sb.toString());
	// // return sb.toString();
	// } catch (Exception e) {
	// Log.e("problem share data getting", "" + e);
	// // return "";
	// }
	//
	// // }
	//
	// // @Override
	// // protected void onPostExecute(String result) {
	// shareUserID = new ArrayList<String>();
	// shareLatitude = new ArrayList<String>();
	// shareLongitude = new ArrayList<String>();
	// shareComment = new ArrayList<String>();
	// shareUserName = new ArrayList<String>();
	// shareTripID = new ArrayList<String>();
	// shareUserImage = new ArrayList<String>();
	//
	// try {
	// JSONObject jsob = new JSONObject(sb.toString());
	// if (jsob.getString("msg").equalsIgnoreCase("Success")) {
	// JSONArray datarray = jsob.getJSONArray("data");
	// // Log.e("array length", "" + datarray.length());
	// for (int i = 0; i < datarray.length(); i++) {
	// JSONObject dataOb = datarray.getJSONObject(i);
	// shareUserID.add(dataOb.getString("userid"));
	// shareLatitude.add(dataOb.getString("latitude"));
	// shareLongitude.add(dataOb.getString("longitude"));
	// shareComment.add(dataOb.getString("comments"));
	// shareUserName.add(dataOb.getString("username"));
	// shareTripID.add(dataOb.getString("share_trip_id"));
	// shareUserImage.add(dataOb.getString("user_image"));
	// // if (trip_id.equalsIgnoreCase(dataOb
	// // .getString("share_trip_id"))) {
	// Log.e("tracking location matched",
	// "" + shareUserImage.get(i));
	//
	// setUpMap(shareLatitude.get(i), shareLongitude.get(i),
	// shareUserName.get(i), shareUserImage.get(i));
	//
	// // }
	// }
	// if (datarray.length() > 0) {
	// badge.setText(datarray.length() + "");
	// // badge.setX(-30);
	// badge.show();
	// } else {
	// badge.hide();
	// }
	// }
	// } catch (Exception e) {
	// Log.e("get user data error", "" + e);
	// e.printStackTrace();
	// }
	// // }
	// // }.execute();
	//
	// }

}
