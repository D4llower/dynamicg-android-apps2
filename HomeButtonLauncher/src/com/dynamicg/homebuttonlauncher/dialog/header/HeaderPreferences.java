package com.dynamicg.homebuttonlauncher.dialog.header;

import android.widget.PopupMenu;

import com.dynamicg.common.AppSignature;
import com.dynamicg.homebuttonlauncher.HBLConstants;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.PreferencesDialog;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;
import com.dynamicg.homebuttonlauncher.tools.drive.HBLBackupRestore;

public class HeaderPreferences extends HeaderAbstract {

	private static String PUBKEY_DYNAMIC_G = "b634fd91174b8252eb20f9ee1cfba7e2dc5fbee5";

	private final MainActivityHome activity;
	private final PreferencesDialog dialog;

	public HeaderPreferences(PreferencesDialog dialog, MainActivityHome activity) {
		super(dialog);
		this.activity = activity;
		this.dialog = dialog;
	}

	@Override
	public void attach() {
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(PopupMenu popupMenu, int id) {
				if (id!=HBLConstants.MENU_BLANK) {
					HBLBackupRestore.create(activity, dialog, id);
				}
			}
		};
		final PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, iconNode, listener);
		menuWrapper.attachToAnchorClick();
		if (isMatchingCertificate()) {
			menuWrapper.addItem(HBLConstants.MENU_DRIVE_BACKUP, R.string.prefsDriveBackup);
			menuWrapper.addItem(HBLConstants.MENU_DRIVE_RESTORE, R.string.prefsDriveRestore);
			menuWrapper.addItem(HBLConstants.MENU_BLANK, "");
		}
		menuWrapper.addItem(HBLConstants.MENU_SDCARD_BACKUP, R.string.prefsSdCardBackup);
		menuWrapper.addItem(HBLConstants.MENU_SDCARD_RESTORE, R.string.prefsSdCardRestore);
	}

	/*
	 * backup/restore over google drive only works if the app is compiled with the DynamicG certificate
	 * (permission com.dynamicg.timerec.plugin3.ACCESS with android:protectionLevel="signature")
	 * hence we hide these items on other builds (f-droid)
	 */
	private boolean isMatchingCertificate() {
		return PUBKEY_DYNAMIC_G.equals(AppSignature.get(activity));
	}
}
