package com.dynamicg.homebuttonlauncher.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dynamicg.homebuttonlauncher.MainActivityHome;

public class ShortcutDebugger extends Dialog {

	private Intent intent;
	private Context context;

	public ShortcutDebugger(Context context, Intent intent) {
		super(context);
		this.context = context;
		this.intent = intent;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Button b1 = new Button(context);
		b1.setText("Test A");
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.startActivity(intent);
			}
		});

		Button b2 = new Button(context);
		b2.setText("Test B");
		b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = intent.toUri(0);
				try {
					Intent parseUri = Intent.parseUri(uri, 0);
					context.startActivity(parseUri);
				}
				catch (Throwable t) {
					DialogHelper.showCrashReport(context, t);
				}
			}
		});

		Button b3 = new Button(context);
		b3.setText("Test C");
		b3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = intent.toUri(Intent.URI_INTENT_SCHEME);
				try {
					Intent parseUri = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
					context.startActivity(parseUri);
				}
				catch (Throwable t) {
					DialogHelper.showCrashReport(context, t);
				}
			}
		});

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(b1);
		layout.addView(b2);
		layout.addView(b3);

		setTitle("Shortcut Debugger");
		setContentView(layout);
	}


	public static void debug(MainActivityHome activity, Intent intent) {
		new ShortcutDebugger(activity, intent).show();
	}

}
