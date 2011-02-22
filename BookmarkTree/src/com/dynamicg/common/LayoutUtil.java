package com.dynamicg.common;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

public class LayoutUtil {

	public static void underline(TextView view) {
		SpannableString content = new SpannableString(view.getText());
	    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
	    view.setText(content);
	}
	
//	public static void maximizeDialog(Dialog dialog) {
//		// see http://devstream.stefanklumpp.com/2010/07/android-display-dialogs-in-fullscreen.html
//		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//	}

	public static void indentedFocusable(TextView node, String prefix, String text) {
		String fulltext = prefix+text;
		node.setFocusable(true);
		SpannableString str = new SpannableString(fulltext);
		str.setSpan(new UnderlineSpan(), prefix.length(), fulltext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		node.setText(str,TextView.BufferType.SPANNABLE);
	}
	
}
