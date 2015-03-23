package com.yourtranslator.app;

/**
 * 
 * @author Alif
 * @email alif.jamaluddin@siswa.um.edu.my
 * @class_decription This class is used to load web view for about page.
 */
import com.yourtranslator.app.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public final class AboutActivity extends Activity {

	public static final String ABOUT_PAGE = "about.html";
	private static final String BASE_URL = "file:///android_asset/html/";

	private WebView webView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.help);

		webView = (WebView) findViewById(R.id.help_contents);
		webView.loadUrl(BASE_URL + ABOUT_PAGE);

	}

}