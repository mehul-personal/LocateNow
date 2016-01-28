package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ChatRoomDetails extends Activity implements OnClickListener {
	EditText editText;
	Button buttonSend;

	private static final String TAG_ID = "from_id";
	private static final String TAG_to_id = "to_id";
	private static final String TAG_COMMENT = "comment";
	private static final String TAG_USERNAME = "user_name";
	private static final String TAG_CHAT_ID = "chat_id";
	private static final String TAG_SENDER_PHOTO = "profile_sender_image";

	private static final String TAG_RECEVIER_PHOTO = "profile_receiver_image";

	String strMsg = "";

	// {"id":"32","to_id":"8","comment":"darshittest9","user_name":"mehul patel","chat_id":"5"}

	// Call
	// http://mydemoprojects.com/bonjour/user_chat.php?chat_id=1

	// Insert
	// http://mydemoprojects.com/bonjour/seller_chat.php?to_id=1&from_id=8&comment=user&count_images=0&image1=%22%22&image2=%22%22&chat_id=3

	JSONArray contacts = null, contactslaters = null;
	ArrayList<HashMap<String, String>> contactList;

	private ProgressDialog progressDialog;

	private SharedPreferences preferences;
	private Editor editor;

	int index;

	String stringStr = "", indexStr = "";
	Thread t;

	ListView commentlist;
	CommnetAdapter adapter;
	JSONParserNew jParser = new JSONParserNew();
	public static String from_id = "", to_id = "", chat_id = "",
			categoryid = "", chat_status = "", ID = "", TO_ID = "",
			share_id = "";
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_layout);
		ActionBar actionBar = getActionBar();
//		actionBar.setIcon(new ColorDrawable(getResources().getColor(
//				android.R.color.transparent)));
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.myPrimaryColor)));
		 // Darshit 09 May
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		preferences = getSharedPreferences("CHAT_COMMENT_PREFS", 0);
		editor = preferences.edit();
		editText = (EditText) findViewById(R.id.edtCommentBox);
		buttonSend = (Button) findViewById(R.id.btnSend);
		buttonSend.setOnClickListener(this);

		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		buttonSend.setTypeface(boldFont);
		editText.setTypeface(regularFont);
		
		Intent i = getIntent();
		chat_status = i.getStringExtra("STATUS");
		StringBuilder rackingSystemSb = new StringBuilder(i.getStringExtra("CHAT_USERNAME"));
		rackingSystemSb.setCharAt(0,
				Character.toUpperCase(rackingSystemSb.charAt(0)));
		
//		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		if (actionBarTitleId > 0) {
//		    TextView title = (TextView) findViewById(actionBarTitleId);
//		    title.setText(rackingSystemSb.toString()+"");
//		    title.setTypeface(mediumFont);
//		}
		SpannableString s = new SpannableString(rackingSystemSb.toString()+"");
	    s.setSpan(new TypefaceSpan("Lato-Medium.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    actionBar.setTitle(s);
	    
		
		if (chat_status.equalsIgnoreCase("USER")) {
			to_id = i.getStringExtra("TO_ID");
			from_id = i.getStringExtra("FROM_ID");
			//share_id = i.getStringExtra("SHARE_ID");
			//actionBar.setTitle(rackingSystemSb.toString()+"");
			Log.e("usercaht","toid:"+to_id+",fromid:"+from_id+",chatid:"+share_id);
			editor.putString("chat_id_key", "");
			editor.commit();

		} else if (chat_status.equalsIgnoreCase("PUSH")) {
			/** This comes from GCMIntentService **/
			to_id = i.getStringExtra("TO_ID");
			from_id = i.getStringExtra("FROM_ID");
			// offer_id = i.getStringExtra("SHARE_ID");
			String chat_id = i.getStringExtra("CHAT_ID");
			//actionBar.setTitle(rackingSystemSb.toString()+"");
			Log.e("push chat","toid:"+to_id+",fromid:"+from_id+",chatid:"+chat_id);
			editor.putString("chat_id_key", chat_id + "");
			editor.commit();
			SQLiteDatabase mdatabase = openOrCreateDatabase("CHAT_DATABASE.db",
					Context.MODE_PRIVATE, null);
			mdatabase.delete("CHAT_COUNT",
					"chat_to_id=? AND chat_from_id=? AND chat_id=?",
					new String[] { to_id, from_id, chat_id });
		} else if (chat_status.equalsIgnoreCase("MULTI_USER_CHAT")) {
			/** This comes from ChatUserList **/
			to_id = i.getStringExtra("TO_ID");
			from_id = i.getStringExtra("FROM_ID");
			String chat_id = i.getStringExtra("CHAT_ID");
			
			//i.getStringExtra("USER_NAME");
			Log.e("multi user chat","toid:"+to_id+",fromid:"+from_id+",chatid:"+chat_id);
			editor.putString("chat_id_key", i.getStringExtra("CHAT_ID") + "");
			editor.commit();

			SQLiteDatabase mdatabase = openOrCreateDatabase("CHAT_DATABASE.db",
					Context.MODE_PRIVATE, null);
			mdatabase.delete("CHAT_COUNT",
					"chat_to_id=? AND chat_from_id=? AND chat_id=?",
					new String[] { to_id, from_id, chat_id });

		}

		// From Getting Data
		progressDialog = ProgressDialog.show(ChatRoomDetails.this, "",
				"Please wait...");
		new Thread(new Runnable() {
			public void run() {
				getCommnetData();
			}
		}).start();

		startThreadInBackground();

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

	@Override
	public void onClick(View arg0) {
		if (arg0 == buttonSend) {
			if (editText.getText().toString().equalsIgnoreCase("")) {
				Toast.makeText(ChatRoomDetails.this, "Please enter comment",
						Toast.LENGTH_SHORT).show();
			} else {
				progressDialog = ProgressDialog.show(ChatRoomDetails.this, "",
						"Please wait...");
				new Thread(new Runnable() {
					public void run() {
						insertCommnetdata(editText.getText().toString(),
								preferences.getString("chat_id_key", ""));
					}
				}).start();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void getCommnetData() {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost;
		String responseData = "";
		try {

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			httppost = new HttpPost(ApplicationData.serviceURL
					+ "get_comment.php");
			Log.e("chatid:",
					"chatid:>" + preferences.getString("chat_id_key", ""));
			entity.addPart("share_id",
					new StringBody(preferences.getString("chat_id_key", ""),
							Charset.forName("UTF-8")));

			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			Log.e("get comment data", sb.toString());
			responseData = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String url = "";
		contactList = new ArrayList<HashMap<String, String>>();
		//
		// url = ApplicationData.serviceURL + "get_comment.php?chat_id="
		// + preferences.getString("chat_id_key", "") + "";
		//
		// String res = jParser.getJSONFromUrl(url.replaceAll(" ", "%20")
		// .replaceAll("'", "%27"));
		//

		try {

			JSONObject jsonObj = new JSONObject(responseData);

			// Getting JSON Array node
			contacts = jsonObj.getJSONArray("data");
			if (contacts.length() > 0) {
				for (int i = 0; i < contacts.length(); i++) {
					JSONObject c = contacts.getJSONObject(i);

					String comment = c.getString(TAG_COMMENT);
					String room_username = c.getString(TAG_USERNAME);
					String id = c.getString(TAG_ID);
					String to_id = c.getString(TAG_to_id);

					HashMap<String, String> map = new HashMap<String, String>();
					map.put(TAG_COMMENT, comment);
					map.put(TAG_USERNAME, room_username);
					map.put(TAG_ID, id);
					map.put(TAG_to_id, to_id);
					map.put(TAG_SENDER_PHOTO, c.getString(TAG_SENDER_PHOTO));
					map.put(TAG_RECEVIER_PHOTO, c.getString(TAG_RECEVIER_PHOTO));

					// if (userid.equalsIgnoreCase(id)) {
					// ID = id;
					// TO_ID = to_id;
					// } else {
					// ID = to_id;
					// TO_ID = id;
					// }

					contactList.add(map);
				}

				mHandler.sendEmptyMessage(0);
			} else {
				mHandler.sendEmptyMessage(1);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			progressDialog.dismiss();
		}

	}

	// Refersh Evevry 10 second Chat API
	public void startThreadInBackground() {
		t = new Thread() {
			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(10000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								new Thread(new Runnable() {
									public void run() {
										getCommnetData();
									}
								}).start();
							}
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};
		t.start();
	}

	class CommnetAdapter extends ArrayAdapter<HashMap<String, String>> {
		public CommnetAdapter(Context context, int textViewResourceId,
				ArrayList<HashMap<String, String>> contactList) {
			super(context, textViewResourceId, contactList);
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_chatroom, null);

			// / Sender
			LinearLayout laySender = (LinearLayout) rowView
					.findViewById(R.id.LinearSender);
			LinearLayout layReceiver = (LinearLayout) rowView
					.findViewById(R.id.LinearReceiver);

			ImageView senderPhoto = (ImageView) rowView
					.findViewById(R.id.imvSenderPhoto);
			ImageView recevierPhoto = (ImageView) rowView
					.findViewById(R.id.imvRecevierPhoto);
			// if (!contactList.get(position).get(TAG_SENDER_PHOTO).isEmpty()) {
			// Picasso.with(ChatRoomDetails.this)
			// .load(contactList.get(position).get(TAG_SENDER_PHOTO))
			// .placeholder(R.drawable.no_image_profile)
			// .transform(new CircleTransform()).into(senderPhoto);
			// } else
			// if(contactList.get(position).get(TAG_SENDER_PHOTO).isEmpty()) {
			// Picasso.with(ChatRoomDetails.this)
			// .load(R.drawable.no_image_profile)
			//
			// .transform(new CircleTransform()).into(senderPhoto);
			// }
			// if (!contactList.get(position).get(TAG_RECEVIER_PHOTO).isEmpty())
			// {
			// Picasso.with(ChatRoomDetails.this)
			// .load(contactList.get(position).get(TAG_RECEVIER_PHOTO))
			// .placeholder(R.drawable.no_image_profile)
			// .transform(new CircleTransform()).into(recevierPhoto);
			// } else if
			// (contactList.get(position).get(TAG_RECEVIER_PHOTO).isEmpty()){
			// Picasso.with(ChatRoomDetails.this)
			// .load(R.drawable.no_image_profile)
			//
			// .transform(new CircleTransform()).into(recevierPhoto);
			// }
			if (contactList.size() > 0) {
				TextView senderCommnet = (TextView) rowView
						.findViewById(R.id.Textview_sender_comment);
				senderCommnet.setText(""
						+ contactList.get(position).get(TAG_COMMENT));
				senderCommnet.setTypeface(mediumFont);
				// Receiver
				TextView receiveCommnet = (TextView) rowView
						.findViewById(R.id.Textview_receiver_comment);
				receiveCommnet.setText(""
						+ contactList.get(position).get(TAG_COMMENT));
				receiveCommnet.setTypeface(mediumFont);
				
				SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
						MODE_PRIVATE);
				String userid = mPrefs.getString("USER_ID", "");

				if (contactList.get(position).get(TAG_ID).trim().toString()
						.equalsIgnoreCase(userid)) {
					laySender.setVisibility(View.VISIBLE);
					if (!contactList.get(position).get(TAG_SENDER_PHOTO)
							.isEmpty()) {
						Picasso.with(ChatRoomDetails.this)
								.load(contactList.get(position).get(
										TAG_SENDER_PHOTO))
								.placeholder(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(senderPhoto);
					} else {
						Picasso.with(ChatRoomDetails.this)
								.load(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(senderPhoto);
					}
					if (!contactList.get(position).get(TAG_RECEVIER_PHOTO)
							.isEmpty()) {
						Picasso.with(ChatRoomDetails.this)
								.load(contactList.get(position).get(
										TAG_RECEVIER_PHOTO))
								.placeholder(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(recevierPhoto);
					} else {
						Picasso.with(ChatRoomDetails.this)
								.load(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(recevierPhoto);
					}
				} else {
					layReceiver.setVisibility(View.VISIBLE);
					if (!contactList.get(position).get(TAG_SENDER_PHOTO)
							.isEmpty()) {
						Picasso.with(ChatRoomDetails.this)
								.load(contactList.get(position).get(
										TAG_SENDER_PHOTO))
								.placeholder(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(recevierPhoto);
					} else {
						Picasso.with(ChatRoomDetails.this)
								.load(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(recevierPhoto);
					}

					if (!contactList.get(position).get(TAG_RECEVIER_PHOTO)
							.isEmpty()) {
						Picasso.with(ChatRoomDetails.this)
								.load(contactList.get(position).get(
										TAG_RECEVIER_PHOTO))
								.placeholder(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(senderPhoto);
					} else {
						Picasso.with(ChatRoomDetails.this)
								.load(R.drawable.no_image_profile)
								.transform(new CircleTransform())
								.into(senderPhoto);
					}
				}
				editText.requestFocus();
			}
			return rowView;
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

	@SuppressWarnings("deprecation")
	public void insertCommnetdata(String commnet, String chat_id) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost;

		try {

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			if (!chat_status.equalsIgnoreCase("USER")) {
				// seller=19||user=17||offerid=26
				Log.e("api call", "receiver_chat call" + ":fromid:" + from_id
						+ ":toid:" + to_id);
				httppost = new HttpPost(ApplicationData.serviceURL
						+ "receiver_chat.php");
				entity.addPart("from_id", new StringBody(to_id));
				entity.addPart("chat_id", new StringBody(chat_id));
				entity.addPart("to_id", new StringBody(from_id));
				entity.addPart("comment",
						new StringBody(commnet, Charset.forName("UTF-8")));
				// "offer_id="
				// + offer_id + "&from_id=" + user_id + "&category_id="
				// + categoryid + "&comment=" + commnet + "&chat_id="
				// + chat_id + "&Action=TestPushNotification&to_id="
				// + seller_id;
			} else {
				// //seller=19||user=17||offerid=blank
				httppost = new HttpPost(ApplicationData.serviceURL
						+ "sender_chat.php");
				Log.e("api call", "sender_chat call" + ":fromid:" + from_id
						+ ":toid:" + to_id);
				entity.addPart("from_id", new StringBody(from_id));
				entity.addPart("chat_id", new StringBody(chat_id));
				entity.addPart("to_id", new StringBody(to_id));
				entity.addPart("comment",
						new StringBody(commnet, Charset.forName("UTF-8")));
				// url = ApplicationData.serviceURL + "user_chat.php?from_id="
				// + seller_id + "&to_id=" + user_id + "&chat_id=" + chat_id
				// + "&comment=" + commnet
				// + "&Action=TestPushNotification&offer_id=" + offer_id;
			}

			// if (selectedImagePath != null) {
			// if (!selectedImagePath.isEmpty()) {
			// Log.e("image path", "image:" + selectedImagePath);
			// entity.addPart("photo", new FileBody(new File(
			// selectedImagePath)));
			// }
			// }
			/*
			 * entity.addPart("firstname", new StringBody(name));
			 * entity.addPart("lastname", new StringBody(lastName));
			 * entity.addPart("gender", new StringBody(varGender));
			 * entity.addPart("birthdate", new StringBody(
			 * txvRegisterMonth.getText().toString() + "/" +
			 * txvRegisterDay.getText().toString() + "/" +
			 * txvRegisterYear.getText().toString()));
			 * entity.addPart("password", new StringBody(password));
			 */

			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			Log.e("insert comment", sb.toString());

			/*
			 * String url = ""; JSONParserNew jParser = new JSONParserNew();
			 * 
			 * if (chat_status.equalsIgnoreCase("USER")) { //
			 * seller=19||user=17||offerid=26 url = ApplicationData.serviceURL +
			 * "seller_chat.php?offer_id=" + offer_id + "&from_id=" + user_id +
			 * "&category_id=" + categoryid + "&comment=" + commnet +
			 * "&chat_id=" + chat_id + "&Action=TestPushNotification&to_id=" +
			 * seller_id; } else { // //seller=19||user=17||offerid=blank url =
			 * ApplicationData.serviceURL + "user_chat.php?from_id=" + seller_id
			 * + "&to_id=" + user_id + "&chat_id=" + chat_id + "&comment=" +
			 * commnet + "&Action=TestPushNotification&offer_id=" + offer_id; }
			 * String res = jParser.getJSONFromUrl(url.replaceAll(" ", "%20")
			 * .replaceAll("'", "%27")); Log.e("caht response", "" + res);
			 * String finlRes = "[" + res + "]";
			 */

			JSONObject object = new JSONObject(sb.toString());

			strMsg = object.getString("msg");
			if (strMsg.trim().equalsIgnoreCase("Success")) {
				String chatid = object.getString("chat_id");

				editor.putString("chat_id_key", chatid);
				editor.commit();

				getCommnetData();
			} else {
				editor.putString("chat_id_key", "");
				editor.commit();

			}

			mHandler.sendEmptyMessage(2);
		} catch (Exception e) {
			mHandler.sendEmptyMessage(2);
		}

	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				commentlist = (ListView) findViewById(R.id.lsvChatList);
				commentlist.requestFocus();

				adapter = new CommnetAdapter(ChatRoomDetails.this,
						R.layout.list_chatroom, contactList);
				commentlist.setAdapter(adapter);

				adapter.notifyDataSetChanged();
				commentlist.invalidate();

				if (contactList.size() > 0) {
					commentlist.setSelection(contactList.size() - 1);
				}

				break;

			case 1:
				progressDialog.dismiss();
				// Toast.makeText(ChatRoomDetails.this, "No Chat Here..",
				// Toast.LENGTH_SHORT).show();
				break;

			case 2:
				editText.setText("");
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				// Activity Refresh After Send Message
				/*
				 * Intent intent = new
				 * Intent(ChatRoomDetails.this,ChatRoomDetails.class);
				 * startActivity(intent); finish();
				 */
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			t.interrupt();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
