package com.dynamicg.homebuttonlauncher.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.EditText;

import com.dynamicg.common.ErrorSender;
import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.GlobalContext;
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

	public static void prepareCommonDialog(Dialog d, int bodyLayoutId, int buttonsLayoutId, boolean customHeader) {
		d.setContentView(R.layout.common_dialog);

		ViewStub body = (ViewStub)d.findViewById(R.id.commonDialogBody);
		body.setLayoutResource(bodyLayoutId);
		body.inflate();

		ViewStub buttons = (ViewStub)d.findViewById(R.id.commonDialogButtonPanel);
		buttons.setLayoutResource(buttonsLayoutId);
		buttons.inflate();

		if (!customHeader) {
			d.findViewById(R.id.headerContainer).setVisibility(View.GONE);
		}
	}

	public static void confirm(Context context, int labelId, OnClickListenerDialogWrapper okListener) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		String label = context.getString(labelId)+"?";
		b.setTitle(label);
		b.setPositiveButton(R.string.buttonOk, okListener);
		b.setNegativeButton(R.string.buttonCancel, null);
		b.show();
	}

	public static int getDimension(int dimensionId) {
		return (int)GlobalContext.resources.getDimension(dimensionId);
	}

	public static void underline(SpannableString str, int underlineFrom, int underlineTo) {
		str.setSpan(new UnderlineSpan(), underlineFrom, underlineTo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public static void bold(SpannableString str, int underlineFrom, int underlineTo) {
		str.setSpan(new StyleSpan(Typeface.BOLD), underlineFrom, underlineTo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public static interface TextEditorListener {
		public void onTextChanged(String text);
	}

	public static void openLabelEditor(final Context context
			, final String defaultLabel
			, final int inputType // e.g. InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
			, final TextEditorListener callback
			)
	{
		final String label = defaultLabel!=null ? defaultLabel : "";
		final EditText editor = new EditText(context);
		editor.setText(label);
		editor.setSingleLine();
		if (label.length()>0) {
			editor.setSelection(label.length());
		}
		editor.setInputType(inputType);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		DialogInterface.OnClickListener okListener = new OnClickListenerDialogWrapper(context) {
			@Override
			public void onClickImpl(DialogInterface dialog, int which) {
				String newLabel = editor.getText().toString();
				newLabel = newLabel!=null ? newLabel.trim() : "";
				callback.onTextChanged(newLabel);
			}
		};
		builder.setPositiveButton(R.string.buttonOk, okListener );
		builder.setNegativeButton(R.string.buttonCancel, null);
		builder.setView(editor);
		AlertDialog dialog = builder.show();

		// auto open keyboard
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

}
