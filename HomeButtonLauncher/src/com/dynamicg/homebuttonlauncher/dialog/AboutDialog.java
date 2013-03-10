package com.dynamicg.homebuttonlauncher.dialog;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class AboutDialog extends Dialog {

	private static final Logger log = new Logger(AboutDialog.class);

	private static final String REPOSITORY = "https://dynamicg-android-apps2.googlecode.com/svn/trunk/HomeButtonLauncher";

	private final Context context;

	private final HashMap<String, String> pendingTranslations = new HashMap<String, String>();
	{
		pendingTranslations.put("ko", "Korean");
		pendingTranslations.put("ja", "Japanese");
		pendingTranslations.put("tr", "Turkish");
		pendingTranslations.put("ru", "Russian");
		pendingTranslations.put("ar", "Arabic");
		pendingTranslations.put("es", "Spanish");
	}

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

		SpannableString creditsLabel = new SpannableString("Credits");
		creditsLabel.setSpan(new UnderlineSpan(), 0, creditsLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		setLine(R.id.aboutCredits, creditsLabel);

		setupTranslationRequest();
	}

	private void setupTranslationRequest() {
		final TextView node = (TextView)findViewById(R.id.aboutTranslationRequest);
		final String languageCd = log.isDebugEnabled ? "ko" : Locale.getDefault().getLanguage();
		if (pendingTranslations.containsKey(languageCd)) {
			String label1 = "Can you help with "+pendingTranslations.get(languageCd)+" translation? ";
			String label2 = "Please contact developer";
			SpannableString str = new SpannableString(label1+label2);
			underline(str, label1.length(), label1.length()+label2.length());
			node.setText(str);
			node.setOnClickListener(new OnClickListenerWrapper() {
				@Override
				public void onClickImpl(View view) {
					composeEmail();
				}
			});
		}
		else {
			node.setVisibility(View.GONE);
		}
	}

	private void setLine(int id, CharSequence str) {
		((TextView)findViewById(id)).setText(str);
	}

	private void setRateLabel(TextView node) {
		final String label = "\u21d2 "+context.getString(R.string.aboutPleaseRate)+" \u21d0";
		SpannableString str = new SpannableString(label);
		underline(str, 2, label.length()-2);
		node.setText(str, TextView.BufferType.SPANNABLE);
	}

	private void underline(SpannableString str, int underlineFrom, int underlineTo) {
		str.setSpan(new UnderlineSpan(), underlineFrom, underlineTo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
