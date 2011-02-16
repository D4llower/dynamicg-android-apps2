package com.dynamicg.bookmarkTree.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.dynamicg.common.ErrorNotification;

public abstract class SimpleProgressDialog {

	private static final int MSG_DONE = 0;
	private static final int MSG_ERROR = 1;
	private static final int MSG_UPDATE_TEXT = 2;
	
	private final Context context;
	private final ProgressDialog progressDialog;
	private final Handler doneHandler;

	public SimpleProgressDialog(Context context, int title) {
		this(context, context.getString(title));
	}
	
	public SimpleProgressDialog(Context context, String title) {
		
		this.context = context;
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(title);
		progressDialog.setCancelable(false);
		progressDialog.show();

		doneHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what==MSG_DONE) {
					done();
					progressDialog.dismiss();
				}
				else if (msg.what==MSG_ERROR) {
					progressDialog.dismiss();
					handleError ( (Throwable)msg.obj );
				}
			}
		};

		Thread progressThread = new Thread(new Runnable() {
			public void run() {
				try {
					backgroundWork();
					doneHandler.sendEmptyMessage(MSG_DONE);
				}
				catch (Throwable e) {
					Message msg = new Message();
					msg.what=MSG_ERROR;
					msg.obj=e;
					doneHandler.sendMessage(msg);
				}
			}
		}) ;

		progressThread.start();

	}
	
	public abstract void backgroundWork();
	public abstract void done();
	
	/*
	 * override in implementations if required
	 */
	public void handleError(final Throwable e) {
		ErrorNotification.notifyError(context, e);
	}
	
	public void updateText(String text) {
		Message msg = new Message();
		msg.what=MSG_UPDATE_TEXT;
		msg.obj=text;
		doneHandler.sendMessage(msg);
	}
	
}
