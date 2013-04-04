package com.dynamicg.homebuttonlauncher.dialog;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.common.MarketLinkHelper;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.OnLongClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class AboutDialog extends Dialog {

	private static final Logger log = new Logger(AboutDialog.class);

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

		{
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

		setLine(R.id.aboutSrc, REPOSITORY);

		{
			TextView rateNode = (TextView)findViewById(R.id.aboutRate);
			SpannableString rateLabel = new SpannableString("\u21d2 "+context.getString(R.string.aboutPleaseRate)+" \u21d0");
			DialogHelper.underline(rateLabel, 2, rateLabel.length()-2);
			rateNode.setText(rateLabel);
			rateNode.setFocusable(true);
			rateNode.setOnClickListener(new OnClickListenerWrapper() {
				@Override
				public void onClickImpl(View v) {
					MarketLinkHelper.openMarketIntent(context, SystemUtil.PACKAGE);
				}
			});
		}

		findViewById(R.id.buttonOk).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View view) {
				dismiss();
			}
		});

		SpannableString creditsLabel = new SpannableString("Credits, in chronological order");
		DialogHelper.underline(creditsLabel, 0, creditsLabel.length());
		setLine(R.id.aboutCredits, creditsLabel);

		setupErrorLogViewer();
	}

	private void setupErrorLogViewer() {
		final TextView node = (TextView)findViewById(R.id.aboutErrorlog);
		String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		boolean myDevice = log.isDebugEnabled
				|| "4c5dabb70b45a67".equals(androidId)
				;

		if (!myDevice || SystemUtil.recentError==null) {
			node.setVisibility(View.GONE);
			return;
		}

		node.setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View view) {
				String body = SystemUtil.getFullStackTrace(SystemUtil.recentError);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Error log");
				builder.setMessage(body);
				builder.setPositiveButton("Close", null);
				builder.show();
			}
		});

		node.setOnLongClickListener(new OnLongClickListenerWrapper() {
			@Override
			public boolean onLongClickImpl(View v) {
				SystemUtil.recentError = null;
				node.setVisibility(View.GONE);
				return true;
			}
		});
		node.setLongClickable(true);
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
