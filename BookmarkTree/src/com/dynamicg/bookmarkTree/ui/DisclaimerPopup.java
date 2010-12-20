package com.dynamicg.bookmarkTree.ui;

import android.view.View;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.PreferencesWrapper;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.main.ContextUtil;
import com.dynamicg.common.main.StringUtil;
import com.dynamicg.common.ui.SimpleAlertDialog;

public class DisclaimerPopup {

	private static final int CURRENT_DISCLAIMER_VERSION = 2; 
	
	public static void showOnce(BookmarkTreeContext ctx) {
		PreferencesWrapper prefs = ctx.preferencesWrapper;
		if ( CURRENT_DISCLAIMER_VERSION == prefs.prefsBean.getDisclaimerLastDisplayed() ) {
			return;
		}
		show(ctx);
		prefs.storeDisclaimerLastDisplayed(CURRENT_DISCLAIMER_VERSION);
	}

	public static void show(final BookmarkTreeContext ctx) {

		final String[] appinfo = ContextUtil.getVersion(ctx.activity);

		new SimpleAlertDialog(ctx.activity, "Disclaimer", "Close") {
			@Override
			public View getBody() {
				View body = ctx.getLayoutInflater().inflate(R.layout.disclaimer, null);

				TextView titleItem = (TextView)body.findViewById(R.id.disclaimerTitle);
				String title = titleItem.getText().toString();
				title = StringUtil.replaceFirst(title, "{version}", appinfo[0]);
				titleItem.setText(title);
				
				TextView revisionItem = (TextView)body.findViewById(R.id.disclaimerSvnRevision);
				String revisionText = revisionItem.getText().toString();
				revisionText = StringUtil.replaceFirst(revisionText, "${revision}", appinfo[1]);
				revisionItem.setText(revisionText.trim());
				
				return body;
			}
		};
	}


}
