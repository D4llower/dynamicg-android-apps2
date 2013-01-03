package com.dynamicg.homebuttonlauncher.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewStub;

import com.dynamicg.common.ErrorSender;
import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.R;

public class DialogHelper {

	private static final Logger log = new Logger(DialogHelper.class);

	public static void showError(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}

	public static void showCrashReport(Context context, Throwable t) {
		if (log.isDebugEnabled) {
			t.printStackTrace();
		}
		ErrorSender.notifyError(context, "ERROR", t);
	}

	public static void prepareCommonDialog(Dialog d, int bodyLayoutId, int buttonsLayoutId) {
		d.setContentView(R.layout.common_dialog);

		ViewStub body = (ViewStub)d.findViewById(R.id.commonDialogBody);
		body.setLayoutResource(bodyLayoutId);
		body.inflate();

		ViewStub buttons = (ViewStub)d.findViewById(R.id.commonDialogButtonPanel);
		buttons.setLayoutResource(buttonsLayoutId);
		buttons.inflate();
	}

}
