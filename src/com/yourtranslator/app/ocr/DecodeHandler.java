package com.yourtranslator.app.ocr;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yourtranslator.app.R;
import com.googlecode.tesseract.android.TessBaseAPI;

final class DecodeHandler extends Handler {

	private final CaptureActivity activity;
	private boolean running = true;
	private final TessBaseAPI baseApi;

	private static boolean isDecodePending;

	DecodeHandler(CaptureActivity activity, TessBaseAPI baseApi) {
		this.activity = activity;
		this.baseApi = baseApi;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
		case R.id.ocr_continuous_decode:
			// Only request a decode if a request is not already pending.
			if (!isDecodePending) {
				isDecodePending = true;
				ocrContinuousDecode((byte[]) message.obj, message.arg1,
						message.arg2);
			}
			break;
		case R.id.ocr_decode:
			ocrDecode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	static void resetDecodeState() {
		isDecodePending = false;
	}

	// Perform an OCR decode for single-shot mode.
	private void ocrDecode(byte[] data, int width, int height) {
		// Log.d(TAG, "ocrDecode: Got R.id.ocr_decode message.");
		// Log.d(TAG, "width: " + width + ", height: " + height);

		// Set up the indeterminate progress dialog box
		ProgressDialog indeterminateDialog = new ProgressDialog(activity);
		indeterminateDialog.setTitle("Please wait");
		String ocrEngineModeName = activity.getOcrEngineModeName();
		if (ocrEngineModeName.equals("Both")) {
			indeterminateDialog
					.setMessage("Performing OCR using Cube and Tesseract...");
		} else {
			indeterminateDialog.setMessage("Performing OCR using "
					+ ocrEngineModeName + "...");
		}
		indeterminateDialog.setCancelable(false);
		indeterminateDialog.show();

		// Asyncrhonously launch the OCR process
		PlanarYUVLuminanceSource source = activity.getCameraManager()
				.buildLuminanceSource(data, width, height);
		new OcrRecognizeAsyncTask(activity, baseApi, indeterminateDialog,
				source.renderCroppedGreyscaleBitmap()).execute();

	}

	// Perform an OCR decode for continuous recognition mode.
	private void ocrContinuousDecode(byte[] data, int width, int height) {
		// Asyncrhonously launch the OCR process
		PlanarYUVLuminanceSource source = activity.getCameraManager()
				.buildLuminanceSource(data, width, height);

		new OcrRecognizeAsyncTask(activity, baseApi,
				source.renderCroppedGreyscaleBitmap()).execute();
	}
}
