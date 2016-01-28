package com.analytics.locatenow;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by techiestown on 17/6/15.
 */
public class GcmIntentService extends IntentService {
	static final String PACKAGE_NAME = "com.analytics.locatenow";

	public static final String ACTIVITY_NAME = "com.analytics.locatenow.MainActivity";
	public static final String CHAT_ACTIVITY_NAME = "com.analytics.locatenow.ChatRoomDetails";
	public static int notify_id = 0;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (extras != null && !extras.isEmpty()) { // has effect of unparcelling
													// Bundle
			// Since we're not using two way messaging, this is all we really to
			// check for
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				Logger.getLogger("GCM_RECEIVED").log(Level.INFO,
						extras.toString());

				// showToast(extras.getString("message"));
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);

		String message = extras.getString("message");
		Log.e("push notiifcation received msg", message + "");
		try {
			if (message.contains("||")) {
				if (message != null || !message.isEmpty()
						|| message.length() != 0) {
					ArrayList<String> strmsg = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(message, "||");
					while (st.hasMoreTokens()) {
						strmsg.add(st.nextToken());
					}
					String comment = "", to_id = "", from_id = "", chat_id = "", chat_username = "";
					try {
						comment = strmsg.get(0);
					} catch (Exception e) {
						comment = "";
					}
					try {
						to_id = strmsg.get(1);
					} catch (Exception e) {
						to_id = "";
					}
					try {
						from_id = strmsg.get(2);
					} catch (Exception e) {
						from_id = "";
					}
					try {
						chat_id = strmsg.get(3);
					} catch (Exception e) {
						chat_id = "";
					}
					try {
						chat_username = strmsg.get(4);

					} catch (Exception e) {
						chat_username = "";
					}

					SharedPreferences chat_prefence = getSharedPreferences(
							"CHAT_PREFERENCE", MODE_PRIVATE);
					Editor edit = chat_prefence.edit();

					edit.putString("CHAT_TO_ID", "" + to_id); // seller_id
					edit.putString("CHAT_FROM_ID", "" + from_id); // user_id
					// edit.putString("CHAT_SHARE_ID", "" + share_id);
					edit.putString("CHAT_ID", "" + chat_id);
					edit.putString("CHAT_USER_NAME", "" + chat_username);
					edit.commit();

					SQLiteDatabase mdatabase = openOrCreateDatabase(
							"CHAT_DATABASE.db", Context.MODE_PRIVATE, null);

					String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
							+ "CHAT_TABLE"
							+ "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_to_id TEXT,chat_from_id TEXT,chat_comment TEXT,chat_id TEXT,chat_username TEXT)";

					String DATABASE_COUNT = "CREATE TABLE IF NOT EXISTS "
							+ "CHAT_COUNT"
							+ "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_to_id TEXT,chat_from_id TEXT,chat_comment TEXT,chat_id TEXT,chat_username TEXT)";

					mdatabase.execSQL(DATABASE_CREATE);
					mdatabase.execSQL(DATABASE_COUNT);
					mdatabase.beginTransaction();
					try {

						ContentValues values = new ContentValues();
						values.put("chat_to_id", to_id);
						values.put("chat_from_id", from_id);
						// values.put("chat_share_id", share_id);
						values.put("chat_id", chat_id);
						values.put("chat_comment", comment);
						values.put("chat_username", chat_username);

						mdatabase.insert("CHAT_TABLE", null, values);
						mdatabase.insert("CHAT_COUNT", null, values);
						mdatabase.setTransactionSuccessful();
						Log.e("chat database", "CHAT COMMENT INSERTED");
					} catch (Exception e) {
						Log.e("chat database", "CHAT NOT INSERTED");

					} finally {
						mdatabase.endTransaction();
						mdatabase.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		generateNotification(getApplicationContext(), message);
	}

	protected void showToast(final String message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public static void generateNotification(Context context, String message) {
		ArrayList<String> strmsg = new ArrayList<String>();
		notify_id++;
		int icon = R.drawable.app_icon;
		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification;

		String title = context.getString(R.string.app_name);
		try {
			if (message != null || !message.isEmpty() || message.length() != 0) {

				if (message.contains("||")) {

					StringTokenizer st = new StringTokenizer(message, "||");
					while (st.hasMoreTokens()) {
						strmsg.add(st.nextToken());
					}
					notification = new Notification(icon, strmsg.get(0), when);
					// Intent notificationIntent = new Intent(context,
					// PushActivity.class);
					Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
					notificationIntent.setComponent(new ComponentName(
							PACKAGE_NAME, CHAT_ACTIVITY_NAME));
					// set intent so it does not start a new activity
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP);
					notificationIntent.putExtra("STATUS", "PUSH");
					try {
						notificationIntent
								.putExtra("TO_ID", "" + strmsg.get(1));
					} catch (Exception e) {
					}
					try {
						notificationIntent.putExtra("FROM_ID",
								"" + strmsg.get(2));
					} catch (Exception e) {
					}
					try {
						notificationIntent.putExtra("CHAT_ID",
								"" + strmsg.get(3));
					} catch (Exception e) {
					}
					StringBuilder rackingSystemSb = new StringBuilder(strmsg
							.get(4).toLowerCase());
					rackingSystemSb.setCharAt(0,
							Character.toUpperCase(rackingSystemSb.charAt(0)));
					try {
						notificationIntent.putExtra("CHAT_USERNAME", ""
								+ rackingSystemSb.toString());
					} catch (Exception e) {
					}
					PendingIntent intent = PendingIntent.getActivity(context,
							0, notificationIntent, 0);
					notification.setLatestEventInfo(context, title,
							strmsg.get(0), intent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					// Play default notification sound
					notification.defaults |= Notification.DEFAULT_SOUND;

					// notification.sound = Uri.parse("android.resource://"
					// +context.getPackageName() + "/raw/sounds");

					// chat_username = rackingSystemSb.toString();
					// Vibrate if vibrate is enabled
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					// notificationManager.notify(0, notification);
					NotificationCompat.Builder builder = new NotificationCompat.Builder(
							context);
					builder.setLargeIcon(
							BitmapFactory.decodeResource(
									context.getResources(), R.drawable.app_icon))
							.setSmallIcon(R.drawable.app_icon)
							.setAutoCancel(true)
							.setContentTitle(rackingSystemSb.toString())
							.setContentText(strmsg.get(0))
							.setWhen(when)
							.setContentIntent(
									PendingIntent.getActivity(context, 0,
											notificationIntent,
											PendingIntent.FLAG_UPDATE_CURRENT))
							// .setDeleteIntent(PendingIntent.getBroadcast(context,
							// 0, new Intent(Intent.ACTION_CLEAR_NOTIFICATION),
							// PendingIntent.FLAG_CANCEL_CURRENT))
							.setDefaults(
									Notification.DEFAULT_LIGHTS
											| Notification.DEFAULT_VIBRATE)
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
					notificationManager.notify(notify_id, builder.build());
				} else {
					try {
						SharedPreferences mPrefs = context
								.getSharedPreferences("LOGIN_DETAIL",
										MODE_PRIVATE);
						String user_id = mPrefs.getString("USER_ID", "");
						MainActivity ma = new MainActivity();
						ma.getMySharedLocation(user_id);
					} catch (Exception e) {
						e.printStackTrace();
					}
					notification = new Notification(icon, message, when);
					Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
					notificationIntent.setComponent(new ComponentName(
							PACKAGE_NAME, ACTIVITY_NAME));
					// set intent so it does not start a new activity
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP);

					PendingIntent intent = PendingIntent.getActivity(context,
							0, notificationIntent, 0);
					notification.setLatestEventInfo(context, title, message,
							intent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					// Play default notification sound
					notification.defaults |= Notification.DEFAULT_SOUND;
					// Vibrate if vibrate is enabled
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					// notificationManager.notify(0, notification);
					NotificationCompat.Builder builder = new NotificationCompat.Builder(
							context);
					builder.setLargeIcon(
							BitmapFactory.decodeResource(
									context.getResources(), R.drawable.app_icon))
							.setSmallIcon(R.drawable.app_icon)
							.setAutoCancel(true)
							.setContentTitle(title)
							.setContentText(message)
							.setWhen(when)
							.setContentIntent(
									PendingIntent.getActivity(context, 0,
											notificationIntent,
											PendingIntent.FLAG_UPDATE_CURRENT))
							// .setDeleteIntent(PendingIntent.getBroadcast(context,
							// 0,
							// new Intent(Intent.ACTION_CLEAR_NOTIFICATION),
							// PendingIntent.FLAG_CANCEL_CURRENT))
							.setDefaults(
									Notification.DEFAULT_LIGHTS
											| Notification.DEFAULT_VIBRATE)
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
					notificationManager.notify(notify_id, builder.build());
				}
			}
		} catch (Exception e) {
			Log.e("exception notification", "" + e);
		}
	}

	/*
	 * public void generateNotification(Context context, String message) {
	 * notify_id++; int icon = R.drawable.app_icon; long when =
	 * System.currentTimeMillis(); NotificationManager notificationManager =
	 * (NotificationManager) context
	 * .getSystemService(Context.NOTIFICATION_SERVICE); Notification
	 * notification = new Notification(icon, message, when);
	 * 
	 * String title = context.getString(R.string.app_name);
	 * 
	 * //Intent notificationIntent = new Intent(context, PushActivity.class);
	 * Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
	 * notificationIntent.setComponent(new ComponentName(PACKAGE_NAME,
	 * ACTIVITY_NAME)); // set intent so it does not start a new activity
	 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	 * Intent.FLAG_ACTIVITY_SINGLE_TOP); PendingIntent intent =
	 * PendingIntent.getActivity(context, 0, notificationIntent, 0);
	 * notification.setLatestEventInfo(context, title, message, intent);
	 * notification.flags |= Notification.FLAG_AUTO_CANCEL;
	 * 
	 * // Play default notification sound notification.defaults |=
	 * Notification.DEFAULT_SOUND;
	 * 
	 * // notification.sound = Uri.parse("android.resource://" //
	 * +context.getPackageName() + "/raw/sounds");
	 * 
	 * // Vibrate if vibrate is enabled notification.defaults |=
	 * Notification.DEFAULT_VIBRATE; //notificationManager.notify(0,
	 * notification); NotificationCompat.Builder builder = new
	 * NotificationCompat.Builder(context);
	 * builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
	 * R.drawable.app_icon)) .setSmallIcon(R.drawable.app_icon)
	 * .setAutoCancel(true) .setContentTitle(title) .setContentText(message)
	 * .setWhen(when) .setContentIntent(PendingIntent.getActivity(context, 0,
	 * notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
	 * //.setDeleteIntent(PendingIntent.getBroadcast(context, 0, new
	 * Intent(Intent.ACTION_CLEAR_NOTIFICATION),
	 * PendingIntent.FLAG_CANCEL_CURRENT))
	 * .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
	 * .
	 * setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
	 * ); notificationManager.notify(notify_id, builder.build()); //
	 * getListOfNotification(); }
	 */

	/*
	 * public void getListOfNotification() {
	 * 
	 * String tag_json_obj = "json_obj_req"; SharedPreferences prefers =
	 * getApplicationContext().getSharedPreferences( "LOGIN_DETAIL", 0); final
	 * String user_id = prefers.getString("USER_ID", ""); String url =
	 * ApplicationData.serviceURL;
	 * 
	 * 
	 * StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
	 * new Response.Listener<String>() {
	 * 
	 * @Override public void onResponse(String response) {
	 * Log.e("ListOfNotification", response.toString());
	 * 
	 * try { JSONObject object = new JSONObject(response.toString()); String msg
	 * = object.getString("msg");
	 * 
	 * if (msg.equalsIgnoreCase("Success")) { JSONArray findarray =
	 * object.getJSONArray("data");
	 * 
	 * SharedPreferences prefers = getApplicationContext().getSharedPreferences(
	 * "LOGIN_DETAIL", 0); SharedPreferences.Editor edit = prefers.edit();
	 * edit.putString("NOTIFY_COUNT",
	 * object.getString("recent_notification_count")); edit.commit(); try { if
	 * (Integer.parseInt(object.getString("recent_notification_count")) > 0) {
	 * BaseActivity.badge.setText(object.getString("recent_notification_count")
	 * + ""); BaseActivity.badge.show(); try {
	 * AddExpFunctionalityActivity.badge.
	 * setText(object.getString("recent_notification_count") + "");
	 * AddExpFunctionalityActivity.badge.show(); }catch (Exception e) {
	 * e.printStackTrace(); } NotificationQuickAction.getListOfNotification(); }
	 * else { BaseActivity.badge.hide();
	 * AddExpFunctionalityActivity.badge.hide(); } } catch (Exception e) {
	 * e.printStackTrace(); } startService(new Intent(getApplicationContext(),
	 * AppBudgeService.class)); } else if
	 * (object.getString("msg").equalsIgnoreCase( "Failure")) { }
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } } }, new
	 * Response.ErrorListener() {
	 * 
	 * @Override public void onErrorResponse(VolleyError error) {
	 * //mProgressDialog.dismiss();
	 * 
	 * VolleyLog.e("ListOfNotification Error", "Error: " + error.getMessage());
	 * // hide the progress dialog error.getCause(); error.printStackTrace(); }
	 * }) {
	 * 
	 * @Override protected Map<String, String> getParams() { Map<String, String>
	 * params = new HashMap<String, String>(); params.put("action",
	 * "ListOfNotification"); params.put("userid", user_id); return params; } };
	 * jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f)); //
	 * Adding request to request queue
	 * ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
	 * tag_json_obj); }
	 */
}