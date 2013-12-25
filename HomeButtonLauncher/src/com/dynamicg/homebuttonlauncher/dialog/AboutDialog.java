package com.dynamicg.homebuttonlauncher.dialog;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class AboutDialog extends Dialog {

	private static final String REPOSITORY = "https://dynamicg-android-apps2.googlecode.com/svn/trunk/HomeButtonLauncher";

	private final Context context;

	public AboutDialog(Activity activity) {
		super(activity);
		setCanceledOnTouchOutside(false);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String title = context.getString(R.string.app_name)+" "+SystemUtil.getVersion(context);
		setTitle(title);

		DialogHelper.prepareCommonDialog(this, R.layout.about_body, R.layout.button_panel_1, false);

		putAuthorLink();

		setLine(R.id.aboutSrc, REPOSITORY);

		link(R.id.aboutRate, "\u21d2 "+context.getString(R.string.aboutPleaseRate)+" \u21d0", SystemUtil.PACKAGE);
		link(R.id.aboutDgSettings, "\u21d2 DG Settings \u21d0", "com.dynamicg.settings");

		SpannableString creditsLabel = new SpannableString("Credits, in chronological order");
		DialogHelper.underline(creditsLabel, 0, creditsLabel.length());
		setLine(R.id.aboutCredits, creditsLabel);

		findViewById(R.id.buttonOk).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View view) {
				dismiss();
			}
		});
	}

	private void putAuthorLink() {
		TextView authorNode = (TextView)findViewById(R.id.aboutAuthor);
		SpannableString authorLabel = new SpannableString("\u00A9 "+SystemUtil.AUTHOR);
		DialogHelper.underline(authorLabel, 2, authorLabel.length());
		authorNode.setText(authorLabel);
		authorNode.setFocusable(true);
		authorNode.setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View view) {
				composeEmail();
			}
		});
	}

	private void link(final int nodeId, final String title, final String pkg) {
		TextView node = (TextView)findViewById(nodeId);
		node.setFocusable(true);
		node.setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				MarketLinkHelper.openMarketIntent(context, pkg);
			}
		});
		SpannableString span = new SpannableString(title);
		DialogHelper.underline(span, 2, span.length()-2);
		node.setText(span);
	}

	private void setLine(int id, CharSequence str) {
		((TextView)findViewById(id)).setText(str);
	}

	private void composeEmail() {
		String label = "Home Button Launcher ("+Locale.getDefault().getLanguage()+")";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{SystemUtil.AUTHOR});
		intent.putExtra(Intent.EXTRA_SUBJECT, label);
		context.startActivity(Intent.createChooser(intent, "Send Email"));
	}

}
