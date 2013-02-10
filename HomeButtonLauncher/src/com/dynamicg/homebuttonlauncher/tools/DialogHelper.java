package com.dynamicg.homebuttonlauncher.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	/*
	 * return the popup menu anchor (i.e. dropdown icon)
	 */
	public static View prepareCustomHeader(Dialog dialog, int label) {
		// set container width and title
		((TextView)dialog.findViewById(R.id.headerTitle)).setText(label);
		int width = (int)dialog.getContext().getResources().getDimension(R.dimen.widthDefault);
		View container = dialog.findViewById(R.id.headerContainer);
		container.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));

		return dialog.findViewById(R.id.headerIcon);
	}

}
