package com.yourtranslator.app.ocr;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.yourtranslator.app.ocr.CaptureActivity;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {

  private final CaptureActivity activity;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;
  private final TessBaseAPI baseApi;

  DecodeThread(CaptureActivity activity, TessBaseAPI baseApi) {
    this.activity = activity;
    this.baseApi = baseApi;
    handlerInitLatch = new CountDownLatch(1);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(activity, baseApi);
    handlerInitLatch.countDown();
    Looper.loop();
  }
}
