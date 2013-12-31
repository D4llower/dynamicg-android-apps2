package com.dynamicg.bookmarkTree.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.backup.BackupRestoreDialog;
import com.dynamicg.bookmarkTree.prefs.PreferencesUpdater;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.common.ContextUtil;
import com.dynamicg.common.SimpleAlertDialog;
import com.dynamicg.common.SystemUtil;

public abstract class AboutDialog {

	public static final String AUTHOR = "dynamicg.android@gmail.com";

	private static final int CURRENT_DISCLAIMER_VERSION = 2;

	public static void showOnce(BookmarkTreeContext ctx, boolean force) {
		int disclaimerLastDisplayed = BookmarkTreeContext.settings.getInt(PreferencesWrapper.KEY_DISCLAIMER, 0);
		if ( !force && CURRENT_DISCLAIMER_VERSION == disclaimerLastDisplayed) {
			return;
		}
		show(ctx);
		PreferencesUpdater.writeIntPref(PreferencesWrapper.KEY_DISCLAIMER, CURRENT_DISCLAIMER_VERSION);
	}

	public static void show(final BookmarkTreeContext ctx) {

		final Context context = ctx.activity;
		final String[] appinfo = ContextUtil.getVersion(context);
		final String apptitle = context.getString(R.string.app_name);

		new SimpleAlertDialog(context, apptitle, R.string.bckCreateBackup, R.string.commonClose) {
			@Override
			public View getBody() {
				View body = SystemUtil.getLayoutInflater(context).inflate(R.layout.about, null);

				String txtOpenSource = "\nThis app is open source:"
						+ "\nhttps://dynamicg-android-apps2.googlecode.com/svn/trunk/BookmarkTree"
						+ "\n";
				String txtAuthor = "\nProgrammed by "+AUTHOR
						+ "\nVersion: " + (appinfo[0])
						+ "\nSVN Revision: " + (appinfo[1])
						+ "\n"
						;

				String revisionText = txtOpenSource + txtAuthor;
				TextView revisionItem = (TextView)body.findViewById(R.id.aboutBodyLinks);
				revisionItem.setText(revisionText);

				//				View backupHint = body.findViewById(R.id.aboutBackupHint);
				//				backupHint.setOnClickListener(new View.OnClickListener() {
				//					@Override
				//					public void onClick(View v) {
				//						new BackupRestoreDialog(ctx);
				//					}
				//				});

				return body;
			}

			@Override
			public void onPositiveButton() {
				new BackupRestoreDialog(ctx, true);
			}

			@Override
			public void onNegativeButton() {
				// dismiss
			}

		};
	}

}
