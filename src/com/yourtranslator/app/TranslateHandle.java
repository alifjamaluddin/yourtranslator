package com.yourtranslator.app;

/**
 * 
 * @author Alif
 * @email alif.jamaluddin@siswa.um.edu.my
 * @class_decription This class is intended to call the function
 *                     of Google Translate API in RESTful JSON form
 */
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class TranslateHandle extends AsyncTask<Void, Void, Void> {
	private static final String TAG = null;
	private String translatedTxt = "No result";
	private Bundle sendBundle;
	private String urlString = null;
	private String text = null;
	private String transLang = "ar"; // Default translate language
	Context ctx;
	public volatile boolean parsingComplete = true;
	private static java.util.Scanner scanner;

	public TranslateHandle(Context ctx, String text, String transLang) {
		this.text = text;
		this.ctx = ctx;
		// this.sourceLang = sourceLang;
		this.transLang = transLang;

	}

	public TranslateHandle(String url, String text) {
		this.urlString = url;
		this.text = text;
	}

	public String getSourceText() {
		return text;
	}

	public String getTranslatedTxt() {
		return translatedTxt;
	}

	//Parse the JSON Object and array, and send it to ViewResultPage activity
	public void readAndParseJSON(String in) {
		String translatedText = "";
		try {
			Log.i(TAG, "readANDParseJSON method");

			JSONObject reader = new JSONObject(in);

			JSONObject data = reader.getJSONObject("data");

			JSONArray translate = (JSONArray) data.get("translations");

			ArrayList<JSONObject> al = new ArrayList<JSONObject>();
			for (int i = 0; i < translate.length(); i++) {
				al.add((JSONObject) translate.get(i));
				JSONObject jo = (JSONObject) al.get(i);
				translatedText = jo.getString("translatedText");
				translatedTxt = translatedText;
				Log.i(TAG, "MainActivity: Result " + translatedTxt);
			}
			sendBundle = new Bundle();
			sendBundle.putString("TranslateResult", getTranslatedTxt());
			sendBundle.putString("SourceText", getSourceText());
			Intent intent1 = new Intent(ctx, ViewResultPage.class);
			intent1.putExtras(sendBundle);
			intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.sendBroadcast(intent1, null);

			Log.d(TAG, "Asynctask finished");
			ctx.startActivity(intent1);
			parsingComplete = false;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//Get the data from Google Translate and parse to readAndParseJSON function
	public void fetchJSON() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(urlString);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setReadTimeout(10000 /* milliseconds */);
					conn.setConnectTimeout(15000 /* milliseconds */);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					// Starts the query
					conn.connect();

					InputStream stream = conn.getInputStream();

					String data = convertStreamToString(stream);
					stream.close();
					readAndParseJSON(data);

				} catch (Exception e) {
					System.out.println("Error:" + e.getMessage());
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	//Get the data stream from the result of translate query
	static String convertStreamToString(java.io.InputStream is) {
		scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	
	//SET API KEY and Query URL to Google Translate API
	@Override
	protected Void doInBackground(Void... params) {
		// TRANSLATE GOOGLE API KEY
		String key = "TRANSLATE GOOGLE API KEY";
		try {
			String encodedtext = URLEncoder.encode(text, "UTF-8");
			urlString = "https://www.googleapis.com/language/translate/v2?key="
					+ key + "&target=" + transLang + "&q=" + encodedtext;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Log.i(TAG, "MainActivity: DoInBackground with url = " + urlString);
		fetchJSON();

		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected void onPostExecute(Void unused) {
		Log.i(TAG, "MainActivity: PostExecute");

	}

}
