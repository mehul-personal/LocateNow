package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.File;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ChatUserList extends Activity {
	ListView userlist;

	private boolean checkDataBase() {
		File PATH = getDatabasePath("CHAT_DATABASE.db");
		if (!PATH.exists()) {
			return false;
		} else {
			return true;
		}
	}

	int cc = 0;
	// private String[] allColumns = { "iID", "chat_seller_id", "chat_user_id",
	// "chat_offer_id", "chat_id", "chat_comment" };
	String selected_to_id = "", selected_from_id = "";
	ArrayList<String> chat_table_to_id, chat_table_from_id, chat_id,
			chat_comment, all_user_id, all_user_name, all_user_image_list,
			CHAT_USER_LIST, CHAT_TOID, CHAT_FROMID, CHAT_ID, CHAT_COUNT,
			CHAT_IMAGE;
	ChatUserDataAdapter adapter;
	ActionBar actionBar;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_user_list);

		actionBar = getActionBar();
		
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.myPrimaryColor)));
		//actionBar.setTitle("Welcome to Locate Now"); // Darshit 09 May
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		
		SpannableString s = new SpannableString("Welcome to Locate Now");
	    s.setSpan(new TypefaceSpan("Lato-Medium.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    actionBar.setTitle(s);
	    
		userlist = (ListView) findViewById(R.id.lsvChatUserList);
		chat_table_to_id = new ArrayList<String>();
		chat_table_from_id = new ArrayList<String>();
		// chat_table_share_id = new ArrayList<String>();
		chat_id = new ArrayList<String>();
		chat_comment = new ArrayList<String>();
		all_user_id = new ArrayList<String>();
		all_user_name = new ArrayList<String>();
		CHAT_USER_LIST = new ArrayList<String>();
		CHAT_TOID = new ArrayList<String>();
		CHAT_IMAGE = new ArrayList<String>();
		CHAT_FROMID = new ArrayList<String>();
		CHAT_ID = new ArrayList<String>();
		CHAT_COUNT = new ArrayList<String>();

		// share_id pass to gpsActivityonline page
		Intent i = getIntent();
		selected_to_id = i.getStringExtra("TO_ID");
		selected_from_id = i.getStringExtra("FROM_ID");

		getChatUserData();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		userlist = (ListView) findViewById(R.id.lsvChatUserList);
		chat_table_to_id = new ArrayList<String>();
		chat_table_from_id = new ArrayList<String>();
		// chat_table_share_id = new ArrayList<String>();
		chat_id = new ArrayList<String>();
		chat_comment = new ArrayList<String>();
		all_user_id = new ArrayList<String>();
		all_user_name = new ArrayList<String>();
		CHAT_USER_LIST = new ArrayList<String>();
		CHAT_TOID = new ArrayList<String>();
		CHAT_IMAGE = new ArrayList<String>();
		CHAT_FROMID = new ArrayList<String>();
		CHAT_ID = new ArrayList<String>();
		CHAT_COUNT = new ArrayList<String>();
		getChatUserData();
	}

	public void getChatUserData() {
		boolean chkDb = checkDataBase();
		if (chkDb == true) {

			SQLiteDatabase DB = openOrCreateDatabase("CHAT_DATABASE.db",
					Context.MODE_PRIVATE, null);
			Cursor c = DB.rawQuery(
					"SELECT name FROM sqlite_master WHERE type='table'", null);
			cc = 0;
			while (c.moveToNext()) {
				String s = c.getString(0);
				if (s.compareTo("CHAT_TABLE") == 0) {
					cc++;
				}
			}
			c.close();
			DB.close();
		}

		if (cc == 0 || chkDb == false) {

		} else {

			// datasource.open();
			SQLiteDatabase db = openOrCreateDatabase("CHAT_DATABASE.db",
					Context.MODE_PRIVATE, null);
			try {

				Cursor cursor = db.rawQuery("SELECT * FROM CHAT_TABLE", null);

				cursor.moveToFirst();
				Log.e("chat user list size", "count:" + cursor.getCount());
				while (!cursor.isAfterLast()) {
					Log.e("to_id", cursor.getString(1));
					chat_table_to_id.add(cursor.getString(1));
					Log.e("from_id", cursor.getString(2));
					chat_table_from_id.add(cursor.getString(2));
					// Log.e("share_id", cursor.getString(3));
					// chat_table_share_id.add(cursor.getString(3));

					Log.e("chat_comment", cursor.getString(3));
					chat_comment.add(cursor.getString(3));
					Log.e("chat_id", cursor.getString(4));
					chat_id.add(cursor.getString(4));
					cursor.moveToNext();
				}
				cursor.close();
				chatAllUserList();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void chatAllUserList() {
		new AsyncTask<Void, Void, String>() {
			ProgressDialog mProgressDialog;

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub

				SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
						MODE_PRIVATE);
				String user_id = mPrefs.getString("USER_ID", "");

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(ApplicationData.serviceURL
						+ "all_bonjour_user.php");
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
					Log.e("Alluser Data", "" + sb.toString());
					return sb.toString();
				} catch (Exception e) {
					Log.e("all user problem", "" + e);
					return "";
				}
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				all_user_id = new ArrayList<String>();
				all_user_name = new ArrayList<String>();
				all_user_image_list = new ArrayList<String>();
				try {

					mProgressDialog.dismiss();
					JSONObject object = new JSONObject(result.toString());

					String msg = object.getString("msg");
					if (msg.equalsIgnoreCase("Success")) {
						JSONArray dataArr = object.getJSONArray("data");
						for (int i = 0; i < dataArr.length(); i++) {
							JSONObject dataOb = dataArr.getJSONObject(i);
							all_user_name.add(dataOb.getString("name"));
							all_user_id.add(dataOb.getString("id"));
							all_user_image_list.add(dataOb.getString("image"));
						}
						SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
								MODE_PRIVATE);
						String user_id = mPrefs.getString("USER_ID", "");
					
						
						for (int j = 0; j < chat_table_from_id.size(); j++) {
							for (int k = 0; k < all_user_id.size(); k++) {
								Log.e("msg", "chat_from_id"
										+ chat_table_from_id.get(j)
										+ "||all_user_id" + all_user_id.get(k));
								String fromid=chat_table_from_id.get(j);
								String toid=chat_table_to_id.get(j);
								String matchid="";
								if(fromid.equalsIgnoreCase(user_id)){
									matchid=toid;
								}else{
									matchid=fromid;
								}
								if (matchid.equalsIgnoreCase(
										all_user_id.get(k))) {
									if (CHAT_USER_LIST.size() == 0) {
										CHAT_USER_LIST
												.add(all_user_name.get(k));
										Log.e("usernme",
												"" + all_user_name.get(k));
										CHAT_IMAGE.add(all_user_image_list
												.get(k));
										CHAT_TOID.add(chat_table_to_id.get(j));
										CHAT_FROMID.add(chat_table_from_id
												.get(j));
										CHAT_ID.add(chat_id.get(j));
									} else {
										boolean chk = false;
										for (int p = 0; p < CHAT_USER_LIST
												.size(); p++) {
											if (all_user_name.get(k)
													.equalsIgnoreCase(
															CHAT_USER_LIST
																	.get(p))) {
												chk = true;
											}
										}
										if (chk == false) {
											CHAT_USER_LIST.add(all_user_name
													.get(k));
											CHAT_IMAGE.add(all_user_image_list
													.get(k));
											CHAT_TOID.add(chat_table_to_id
													.get(j));
											CHAT_FROMID.add(chat_table_from_id
													.get(j));
											CHAT_ID.add(chat_id.get(j));
										}
									}
								}
							}
						}
						ArrayList<String> selected_chat_id = new ArrayList<String>();
						for (int i = 0; i < CHAT_ID.size(); i++) {
							SQLiteDatabase db = openOrCreateDatabase(
									"CHAT_DATABASE.db", Context.MODE_PRIVATE,
									null);
							try {
								// "chat_seller_id", "chat_user_id",
								// "chat_offer_id", "chat_id",
								Cursor cursor = db.rawQuery(
										"SELECT * FROM CHAT_COUNT WHERE chat_to_id='"
												+ CHAT_TOID.get(i)
												+ "' AND chat_from_id='"
												+ CHAT_FROMID.get(i)
												+ "' AND chat_id='"
												+ CHAT_ID.get(i) + "'", null);
								cursor.moveToFirst();
								Log.e("chat user list size", "chat userid:"
										+ CHAT_FROMID.get(i) + ",count:"
										+ cursor.getCount());
								if (cursor.getCount() > 0) {
									CHAT_COUNT.add("" + cursor.getCount());
									selected_chat_id.add(""
											+ cursor.getString(4));
								} else {
									CHAT_COUNT.add("");
									selected_chat_id.add("");
								}
								cursor.close();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						adapter = new ChatUserDataAdapter(CHAT_USER_LIST,
								CHAT_COUNT, CHAT_ID,CHAT_IMAGE);
						userlist.setAdapter(adapter);
					} else {
						Toast.makeText(
								ChatUserList.this,
								"Sorry! we are stuff to fetching data. \n Please try again!",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(
							ChatUserList.this,
							"Sorry! we are stuff to fetching data. \n Please try again!",
							Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(ChatUserList.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please Wait...");
				mProgressDialog.show();

			}
		}.execute();
	}

	public class ChatUserDataAdapter extends BaseAdapter {
		LayoutInflater inflater;
		ArrayList<String> user_name_list, chat_count, chat_id_list,chat_u_image;

		public ChatUserDataAdapter(ArrayList<String> user_name_list,
				ArrayList<String> chat_count, ArrayList<String> chat_id_list,ArrayList<String> chat_u_image) {
			// TODO Auto-generated constructor stub
			this.user_name_list = user_name_list;
			this.chat_count = chat_count;
			this.chat_id_list = chat_id_list;
			this.chat_u_image=chat_u_image;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			TextView userName, chatCount;
			ImageView userImage, chatCountBackImage;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return user_name_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_chat_user_detail,
						parent, false);
				holder = new ViewHolder();
				holder.userName = (TextView) convertView
						.findViewById(R.id.txvChatUserName);
				holder.chatCount = (TextView) convertView
						.findViewById(R.id.txvChatCount);
				holder.userImage = (ImageView) convertView
						.findViewById(R.id.imvChatUserImage);
				holder.chatCountBackImage = (ImageView) convertView
						.findViewById(R.id.imvChatCountImage);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.userName.setTypeface(mediumFont);
			holder.chatCount.setTypeface(mediumFont);
			if(!chat_u_image.get(position).isEmpty()){
				Picasso.with(ChatUserList.this).load(chat_u_image.get(position))
				.placeholder(R.drawable.no_image_profile)
				.transform(new CircleTransform()).into(holder.userImage);
			
			}
			else{
			Picasso.with(ChatUserList.this).load(R.drawable.no_image_profile)
					.placeholder(R.drawable.no_image_profile)
					.transform(new CircleTransform()).into(holder.userImage);
			}
			holder.userName.setText(user_name_list.get(position));
			if (chat_count.get(position).isEmpty()) {
				holder.chatCount.setVisibility(View.INVISIBLE);
				holder.chatCountBackImage.setVisibility(View.INVISIBLE);
			} else {
				holder.chatCount.setVisibility(View.VISIBLE);
				holder.chatCountBackImage.setVisibility(View.VISIBLE);
				holder.chatCount.setText(chat_count.get(position));
			}
			holder.userName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(ChatUserList.this,
							ChatRoomDetails.class);
					i.putExtra("STATUS", "MULTI_USER_CHAT");
					i.putExtra("TO_ID", CHAT_TOID.get(position));
					i.putExtra("FROM_ID", CHAT_FROMID.get(position));
				 i.putExtra("CHAT_USERNAME", user_name_list.get(position));
					i.putExtra("CHAT_ID", chat_id_list.get(position));
					startActivity(i);
				}
			});
			return convertView;
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
}
