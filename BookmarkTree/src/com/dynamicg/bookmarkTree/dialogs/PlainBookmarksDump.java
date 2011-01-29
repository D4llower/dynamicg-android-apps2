package com.dynamicg.bookmarkTree.dialogs;

import java.util.ArrayList;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.common.Logger;
import com.dynamicg.common.SimpleAlertDialog;

public abstract class PlainBookmarksDump {

	private static final Logger log = new Logger(PlainBookmarksDump.class);
	
	public static void show(final BookmarkTreeContext ctx) {
		ctx.reloadAndRefresh(); // so that the gui is really in sync with what we display here
		final ArrayList<RawDataBean> rows = BrowserBookmarkLoader.forInternalOps(ctx);
		
		final StringBuffer sb = new StringBuffer();
		final long div = 1000l * 1000l;
		String line;
		for (RawDataBean row:rows) {
			if (log.debugEnabled) {
				line =
					row.fullTitle 
					+ " ["+ (row.created/div) +"]" 
					+ "\n";
			}
			else {
				line = row.fullTitle + "\n";
			}
			sb.append(line);
		}
		
		new SimpleAlertDialog(ctx.activity, "Browser Bookmarks", "Close") {
			public View getBody() {
				TextView text = new TextView(ctx.activity);
				text.setText(sb.toString());

				HorizontalScrollView xscroll = new HorizontalScrollView(ctx.activity);
				xscroll.addView(text);
				
				ScrollView yscroll = new ScrollView(ctx.activity);
				yscroll.addView(xscroll);
				yscroll.setPadding(10, 0, 10, 0);
				
				return yscroll;
			}
		};
	}
	
}
