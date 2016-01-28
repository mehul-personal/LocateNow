package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	EditText name, phone, email, password, confirmpassword, countrycode;
	Button register;
	ImageView uploadphoto;
	private static final int PICK_IMAGE = 1;
	public static String selectedImagePath;
	Activity main_act;
	TextView heading;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// ActionBar actionBar = getActionBar();
		// actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
		// .getColor(R.color.myPrimaryColor)));
heading=(TextView) findViewById(R.id.txvLocateHeading);
		name = (EditText) findViewById(R.id.edtUserName);
		uploadphoto = (ImageView) findViewById(R.id.imvUploadPhoto);

		email = (EditText) findViewById(R.id.edtUserEmail);
		phone = (EditText) findViewById(R.id.edtPhone);
		password = (EditText) findViewById(R.id.edtPassword);
		confirmpassword = (EditText) findViewById(R.id.edtConfirmPassword);
		countrycode = (EditText) findViewById(R.id.edtCountryCode);
		register = (Button) findViewById(R.id.btnSignUp);

		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		heading.setTypeface(boldFont);
		name.setTypeface(regularFont);
		email.setTypeface(regularFont);
		phone.setTypeface(regularFont);
		password.setTypeface(regularFont);
		confirmpassword.setTypeface(regularFont);
		countrycode.setTypeface(regularFont);
		register.setTypeface(boldFont);
		
//		phone.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		phone.setTextIsSelectable(true);
//		name.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		name.setTextIsSelectable(true);
//		email.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		email.setTextIsSelectable(true);
//		password.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		password.setTextIsSelectable(true);
//		confirmpassword.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		confirmpassword.setTextIsSelectable(true);
//		countrycode.setRawInputType(InputType.TYPE_CLASS_TEXT);
//		countrycode.setTextIsSelectable(true);
		
		uploadphoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						PICK_IMAGE);
			}
		});
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (name.getText().toString().isEmpty()) {
					name.setError("Please fill out space");
					Toast.makeText(RegisterActivity.this,
							"Sorry! Name must be require", Toast.LENGTH_LONG)
							.show();
				} else if (email.getText().toString().isEmpty()) {
					email.setError("Please fill out space");
					Toast.makeText(RegisterActivity.this,
							"Sorry! Email must be require", Toast.LENGTH_LONG)
							.show();
				} else if (phone.getText().toString().isEmpty()) {
					phone.setError("Please fill out space");
					Toast.makeText(RegisterActivity.this,
							"Sorry! Mobile must be require", Toast.LENGTH_LONG)
							.show();
				} else if (password.getText().toString().isEmpty()) {
					password.setError("Please fill out space");
					Toast.makeText(RegisterActivity.this,
							"Sorry! Password must be require",
							Toast.LENGTH_LONG).show();
				} else if (confirmpassword.getText().toString().isEmpty()) {
					confirmpassword.setError("Please fill out space");
					Toast.makeText(RegisterActivity.this,
							"Sorry! Confirm password must be require",
							Toast.LENGTH_LONG).show();
				} else if (!isValidEmail(email.getText().toString())) {
					Toast.makeText(getApplicationContext(),
							"Please enter valid Email", Toast.LENGTH_LONG)
							.show();
				} else if (!password.getText().toString()
						.equalsIgnoreCase(confirmpassword.getText().toString())) {
					Toast.makeText(RegisterActivity.this,
							"Sorry! Please check your password",
							Toast.LENGTH_LONG).show();
				} else {
					UserRegister();
				}
			}
		});
	}

	public boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target)
				&& android.util.Patterns.EMAIL_ADDRESS.matcher(target)
						.matches();
	}

	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image

		if (resultCode == Activity.RESULT_OK) {
			Uri selectedImage = data.getData();
			System.out.println("Content Path : " + selectedImage.toString());
			selectedImagePath = getPath(RegisterActivity.this, selectedImage);
			Log.e("selected image", selectedImagePath + "");
			if (selectedImage != null) {
				uploadphoto
						.setImageBitmap(getCircleBitmap(getScaledBitmap(selectedImage)));
			} else {

				Toast.makeText(RegisterActivity.this, "Error getting Image",
						Toast.LENGTH_SHORT).show();

			}
		} else if (resultCode == Activity.RESULT_CANCELED) {

			Toast.makeText(RegisterActivity.this, "No Photo Selected",
					Toast.LENGTH_SHORT).show();

		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (!isKitKat) {
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = context.getContentResolver().query(uri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} else if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	private Bitmap getScaledBitmap(Uri uri) {
		Bitmap thumb = null, rotatedBitmap = null;
		try {
			ContentResolver cr = getContentResolver();
			InputStream in = cr.openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize=12;
			thumb = BitmapFactory.decodeStream(in, null, options);
			// Matrix matrix = new Matrix();
			// matrix.postRotate(90);
			// rotatedBitmap = Bitmap.createBitmap(thumb, 0, 0,
			// thumb.getWidth(), thumb.getHeight(), matrix, true);
		} catch (FileNotFoundException e) {

			Toast.makeText(RegisterActivity.this, "File not found",
					Toast.LENGTH_SHORT).show();

		}
		return thumb;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public Bitmap getCircleBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth() - 10,
				bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);

		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public void UserRegister() {

		new AsyncTask<Void, Void, String>() {
			ProgressDialog mProgressDialog;

			@SuppressWarnings("deprecation")
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub

				@SuppressWarnings("deprecation")
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(ApplicationData.serviceURL
						+ "register.php");
				try {
					// Add your data
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					entity.addPart("name", new StringBody(name.getText()
							.toString()));
					entity.addPart("email", new StringBody(email.getText()
							.toString()));
					entity.addPart("countrycode", new StringBody(countrycode
							.getText().toString()));
					entity.addPart("password", new StringBody(password
							.getText().toString()));
					entity.addPart("phone", new StringBody(phone.getText()
							.toString()));

					if (selectedImagePath != null) {

						Log.e("image path", "" + selectedImagePath);
						entity.addPart("image", new FileBody(new File(
								selectedImagePath)));
					}
					httppost.setEntity(entity);

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
					Log.e("register Data", "" + sb.toString());
					return sb.toString();

				} catch (Exception e) {
					Log.e("problem data register", "" + e);
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
					Log.e("2", "2");

					JSONArray dataArr = object.getJSONArray("data");
					JSONObject dataOb = dataArr.getJSONObject(0);
					String msg = object.getString("msg");
					if (msg.equalsIgnoreCase("Success")) {
						String id = dataOb.getString("id");
						String name = dataOb.getString("name");
						String countrycode = dataOb.getString("countrycode");
						String password = dataOb.getString("password");
						String image = dataOb.getString("image");
						String phone = dataOb.getString("phone");
						String email = dataOb.getString("email");

						SharedPreferences mPrefs = getSharedPreferences(
								"LOGIN_DETAIL", MODE_PRIVATE);
						Editor edit = mPrefs.edit();
						edit.putString("USER_ID", id);
						edit.putString("NAME", name);
						edit.putString("COUNTRYCODE", countrycode);
						edit.putString("PASSWORD", password);
						edit.putString("IMAGE", image);
						edit.putString("PHONE", phone);
						edit.putString("EMAIL", email);
						edit.commit();

						/**** PUSH NOTIFICATION ****/
//						main_act = RegisterActivity.this;
//						PushActivity p = new PushActivity();
//						p.pushNotiFication(main_act, id);
						 new GcmRegistrationAsyncTask(RegisterActivity.this).execute();
						
						Intent i = new Intent(RegisterActivity.this,
								MainActivity.class);
						startActivity(i);
						finish();

					}
					
					else {
						Toast.makeText(getApplicationContext(),
								"Oopss! Register failure please try again",
								Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Oopss! Register failure please try again",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(RegisterActivity.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please wait ..");
				mProgressDialog.show();

			}
		}.execute();
	}
}
