package com.dynamicg.common;

import android.app.Dialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class LayoutUtil {

	public static void underline(TextView view) {
		SpannableString content = new SpannableString(view.getText());
	    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
	    view.setText(content);
	}
	
	public static void maximizeDialog(Dialog dialog) {
		// see http://devstream.stefanklumpp.com/2010/07/android-display-dialogs-in-fullscreen.html
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

}
