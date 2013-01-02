package com.dynamicg.homebuttonlauncher.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.R;

public class AboutDialog {

	private final String REPOSITORY = "https://dynamicg-android-apps2.googlecode.com/svn/trunk/HomeButtonLauncher";

	private ViewGroup body;

	public void show(final Context context, LayoutInflater inflater ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		String title = context.getString(R.string.app_name)+" "+SystemUtil.getVersion(context);
		builder.setTitle(title);

		builder.setPositiveButton(R.string.buttonClose, null);

		this.body = (ViewGroup)inflater.inflate(R.layout.about, null);
		builder.setView(body);

		setLine(R.id.aboutAuthor, SystemUtil.AUTHOR);
		setLine(R.id.aboutSrc, REPOSITORY);

		TextView rate = (TextView)body.findViewById(R.id.aboutRate);
		String pleaseRate = context.getString(R.string.aboutPleaseRate);
		rate.setText(pleaseRate);
		underline(rate, 2, pleaseRate.length()-2);
		rate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MarketLinkHelper.openMarketIntent(context, SystemUtil.PACKAGE);
			}
		});

		builder.show();
	}

	void setLine(int id, String text) {
		((TextView)body.findViewById(id)).setText(text);
	}

	void underline(TextView node, int underlineFrom, int underlineTo) {
		node.setFocusable(true);
		SpannableString str = new SpannableString(node.getText());
		str.setSpan(new UnderlineSpan(), underlineFrom, underlineTo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		node.setText(str,TextView.BufferType.SPANNABLE);
	}

}
