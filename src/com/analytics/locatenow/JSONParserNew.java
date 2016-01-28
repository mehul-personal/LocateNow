package com.analytics.locatenow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

public class JSONParserNew {

	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParserNew() {

	}

	public String getJSONFromUrl(String url) {

		// Making HTTP request
//		try {
//			// defaultHttpClient
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			is = httpEntity.getContent();			
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		
		try {
			URL url1 = new URL(url);
			HttpURLConnection connection;
			connection = (HttpURLConnection)url1.openConnection();
			Log.i("System out","url:"+url);
			InputStream inputStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			json = sb.toString();
			Log.i("System out","res: "+json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// try parse the string to a JSON object
//		try {
//			jObj = new JSONObject(json);
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}

		// return JSON String
		return json;

	}
}
