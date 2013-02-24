package com.dynamicg.homebuttonlauncher.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerDialogWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper;
import com.dynamicg.homebuttonlauncher.tools.PopupMenuWrapper.PopupMenuItemListener;

public class ConfigHeaderSortReset extends ConfigHeaderAbstract {

	public ConfigHeaderSortReset(AppConfigDialog dialog) {
		super(dialog);
	}

	@Override
	public void attach() {
		final PopupMenuItemListener listener = new PopupMenuItemListener() {
			@Override
			public void popupMenuItemSelected(int id) {
				if (id==MenuGlobals.RESET) {
					confirmSortReset();
				}
			}
		};
		final PopupMenuWrapper menuWrapper = new PopupMenuWrapper(context, iconNode, listener);
		menuWrapper.attachToAnchorClick();
		menuWrapper.addItem(MenuGlobals.RESET, R.string.menuReset);
	}

	private void confirmSortReset() {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		String label = context.getString(R.string.menuReset)+"?";
		b.setTitle(label);
		b.setPositiveButton(R.string.buttonOk, new OnClickListenerDialogWrapper(context) {
			@Override
			public void onClickImpl(DialogInterface d, int which) {
				dialog.doSortReset();
			}
		} );
		b.setNegativeButton(R.string.buttonCancel, null);
		b.show();
	}

}
