package com.dynamicg.homebuttonlauncher.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.ViewStub;

import com.dynamicg.common.ErrorSender;
import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.OnClickListenerDialogWrapper;
import com.dynamicg.homebuttonlauncher.R;

public class DialogHelper {

	private static final Logger log = new Logger(DialogHelper.class);

	public static void showError(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.buttonOk, null);
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

	public static void confirm(Context context, int labelId, OnClickListenerDialogWrapper okListener) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		String label = context.getString(labelId)+"?";
		b.setTitle(label);
		b.setPositiveButton(R.string.buttonOk, okListener);
		b.setNegativeButton(R.string.buttonCancel, null);
		b.show();
	}

	public static int getDimension(Context context, int dimensionId) {
		return (int)context.getResources().getDimension(dimensionId);
	}

	public static int getDimension(Resources res, int dimensionId) {
		return (int)res.getDimension(dimensionId);
	}

}
