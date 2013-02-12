package com.dynamicg.homebuttonlauncher.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class AboutDialog extends Dialog {

	private final String REPOSITORY = "https://dynamicg-android-apps2.googlecode.com/svn/trunk/HomeButtonLauncher";

	final Context context;

	public AboutDialog(Activity activity) {
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String title = context.getString(R.string.app_name)+" "+SystemUtil.getVersion(context);
		setTitle(title);

		DialogHelper.prepareCommonDialog(this, R.layout.about_body, R.layout.button_panel_1);

		setLine(R.id.aboutAuthor, "\u00A9 "+SystemUtil.AUTHOR);
		setLine(R.id.aboutSrc, REPOSITORY);

		TextView rateNode = (TextView)findViewById(R.id.aboutRate);
		setRateLabel(rateNode);
		rateNode.setFocusable(true);
		rateNode.setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				MarketLinkHelper.openMarketIntent(context, SystemUtil.PACKAGE);
			}
		});

		findViewById(R.id.buttonOk).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View view) {
				dismiss();
			}
		});

	}

	private void setLine(int id, String text) {
		((TextView)findViewById(id)).setText(text);
	}

	private void setRateLabel(TextView node) {
		final String label = "\u21d2 "+context.getString(R.string.aboutPleaseRate)+" \u21d0";
		final int underlineFrom = 2;
		final int underlineTo = label.length()-2;
		SpannableString str = new SpannableString(label);
		str.setSpan(new UnderlineSpan(), underlineFrom, underlineTo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		node.setText(str,TextView.BufferType.SPANNABLE);
	}

}
