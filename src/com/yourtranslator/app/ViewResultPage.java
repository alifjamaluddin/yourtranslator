package com.yourtranslator.app;
/**
 * 
 * @author Alif
 * @email alif.jamaluddin@siswa.um.edu.my
 * @class_decription This class is used to show the result of translation
 */
import com.yourtranslator.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewResultPage extends Activity {
	TextView tv1;
	TextView tv2;
	Bundle textResult;

	
	@Override /* Called when the activity is first created. */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewresultpage);
	}

	//get input for search and result, then display it
	protected void onStart() {
		super.onStart();
		textResult = this.getIntent().getExtras();
		String Result = textResult.getString("TranslateResult");
		String SourceText = textResult.getString("SourceText");

		tv1 = (TextView) findViewById(R.id.result);
		tv1.setText(Result);
		tv2 = (TextView) findViewById(R.id.source);
		tv2.setText(SourceText);
	}

}
