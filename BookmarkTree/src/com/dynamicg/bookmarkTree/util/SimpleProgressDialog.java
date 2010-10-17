package com.dynamicg.bookmarkTree.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class SimpleProgressDialog {

	public SimpleProgressDialog(Context context, String title) {
		
		final ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(title);
		progressDialog.setCancelable(false);
		progressDialog.show();

		final Handler doneHandler = new Handler() {
			public void handleMessage(Message msg) {
				done();
				progressDialog.dismiss();
			}
		};

		Thread progressThread = new Thread(new Runnable() {
			public void run() {
				backgroundWork();
				doneHandler.sendEmptyMessage(0);
			}
		}) ;

		progressThread.start();

	}
	
	public abstract void backgroundWork();
	public abstract void done();
	
}
