package com.yourtranslator.app;

/**
 * 
 * @author Alif
 * @email alif.jamaluddin@siswa.um.edu.my
 * @class_decription This it the main class of the application. 
 *  
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yourtranslator.app.R;
import com.yourtranslator.app.ocr.CaptureActivity;
import com.yourtranslator.app.ocr.PreferencesActivity;

public class YourTranslatorActivity extends Activity {
	public static final String PACKAGE_NAME = "com.yourtranslator.app.ocr";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/FYP-YourTranslator/";
	private static final String TAG = "YourTranslator";
	private String searchedText;
	private int temp = -1;
	private final int MAX_RECENT_SEARCH_TERMS = 7;
	String[] test = new String[MAX_RECENT_SEARCH_TERMS];
	SharedPreferences recentSearchTerms;
	SharedPreferences.Editor editor;
	private static final int SETTINGS_ID = Menu.FIRST;
	private static final int ABOUT_ID = Menu.FIRST + 1;

	protected Button ocrButton;
	protected EditText searchTextBox;
	protected static final String SEARCH_QUERY = "search_query";
	private static final String RECENT_SEARCH_TERMS = "Recent Search Terms";
	private boolean insert = false;
	private String translate_lang;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		recentSearchTerms = getSharedPreferences(RECENT_SEARCH_TERMS,
				MODE_PRIVATE);// getPreferences(MODE_PRIVATE);
		editor = recentSearchTerms.edit();
		Log.i(TAG, "onCreate");

	}

	//initialized buttons and searched words
	@Override
	protected void onStart() {
		super.onStart();

		if (insert && !(test[0].equalsIgnoreCase(searchedText))) {
			Log.w(TAG, "IN INSERT");
			for (int i = 0; i < MAX_RECENT_SEARCH_TERMS; i++) {
				if (test[i].equalsIgnoreCase(searchedText)) {
					temp = i + 1; // since in array it is 0-4 and in shared
									// preferences it is 1-5
					break;
				}
			}
			if (temp != -1) {

				for (int i = temp; i >= 1; i--) {
					editor.putString(Integer.toString(i), recentSearchTerms
							.getString(Integer.toString(i - 1), ""));
				}
				editor.putString(Integer.toString(1), searchedText);
				editor.commit();
				temp = -1;
			} else {
				for (int i = 7; i >= 2; i--) {
					editor.putString(Integer.toString(i), recentSearchTerms
							.getString(Integer.toString(i - 1), ""));
				}
				editor.putString(Integer.toString(1), searchedText);
				editor.commit();
			}
			insert = false;
		}
		for (int i = 1; i <= MAX_RECENT_SEARCH_TERMS; i++) {
			test[i - 1] = recentSearchTerms.getString(Integer.toString(i), "");
		}

		ListAdapter adapter = new ArrayAdapter<String>(this,
				R.layout.recent_search_list_item, test);
		final ListView recentSearchList = (ListView) findViewById(R.id.recentSearchList);
		recentSearchList.setAdapter(adapter);

		recentSearchList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				insert = true;
				searchedText = recentSearchList.getItemAtPosition(position)
						.toString();
				setPrefTransLang();

				new TranslateHandle(YourTranslatorActivity.this, searchedText,
						translate_lang).execute();

			}
		});

		try {
			boolean isWifi = checkWifiConnection();
			boolean is3G = check3gConnection();

			Log.i(TAG, "onStart");

			ocrButton = (Button) findViewById(R.id.ocrButton);
			ocrButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Log.v(TAG, "Starting OCR");
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					Intent intent = new Intent(YourTranslatorActivity.this,
							CaptureActivity.class);
					startActivityForResult(intent, 0);
				}

			});

			if ((isWifi || is3G) == false) {
				Toast toast = Toast.makeText(this, R.string.no_network,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				searchTextBox = (EditText) findViewById(R.id.searchTextBox);

				Button bsearch = (Button) findViewById(R.id.searchButton);
				bsearch.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.d(TAG, "onClick: starting service");
						Log.d(TAG, "search term sent:"
								+ searchTextBox.getText().toString());
						if (searchTextBox.getText().toString().length() == 0) {
							Toast.makeText(YourTranslatorActivity.this,
									R.string.null_search_term,
									Toast.LENGTH_SHORT).show();
							Log.i(TAG, "SEARCH TERM IS EMPTY");
						} else {

							searchedText = searchTextBox.getText().toString()
									.trim();
							insert = true;

							Log.i(TAG,
									"YourTranslatorActivity: Button for translate pressed");
							setPrefTransLang();

							new TranslateHandle(YourTranslatorActivity.this,
									searchedText, translate_lang).execute();

						}
					}
				});

			}

		} catch (Exception e) {
			Log.e(TAG, "error is = " + e.toString());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	protected void onResume() {

		super.onResume();
		Log.i(TAG, "onResume");
		onStart();

	}

	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();

	}

	//describe process after received result from ocr
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			// Reset to user-pref orientation
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			String ocrResult = data.getStringExtra("ocrResult").trim();

			if (searchTextBox.getText().length() != 0) {
				searchTextBox.append(" " + ocrResult);
			} else {
				searchTextBox.setText(ocrResult);
			}
		}
	}

	//set translation language 
	public void setPrefTransLang() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		translate_lang = prefs.getString(
				PreferencesActivity.KEY_TRANSLATE_LANGUAGE_MODE,
				CaptureActivity.DEFAULT_TRANSLATE_LANGUAGE_CODE);
	}

	// function to check for wifi connectivity
	public boolean checkWifiConnection() {

		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
			return true;
		} else {
			Log.d(TAG, "Wifi connection not present");
			return false;
		}
	}

	// function to check 3G connection
	public boolean check3gConnection() {
		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conman.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnected()) {
			return true;
		} else {
			Log.d(TAG, "3G connection not present");
			return false;
		}
	}

	//Create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.options_menu, menu);
		super.onCreateOptionsMenu(menu);
		menu.add(0, SETTINGS_ID, 0, "Settings").setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, ABOUT_ID, 0, "About").setIcon(
				android.R.drawable.ic_menu_info_details);
		return true;
	}

	//tells what happen when menu clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case SETTINGS_ID: {
			// To-Do
			// intent = new Intent().setClass(this, SettingActivity.class);
			intent = new Intent().setClass(this, PreferencesActivity.class);
			startActivity(intent);
			break;
		}
		case ABOUT_ID: {
			intent = new Intent(this, AboutActivity.class);
			// intent.putExtra(HelpActivity.REQUESTED_PAGE_KEY,
			// HelpActivity.ABOUT_PAGE);
			startActivity(intent);
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
