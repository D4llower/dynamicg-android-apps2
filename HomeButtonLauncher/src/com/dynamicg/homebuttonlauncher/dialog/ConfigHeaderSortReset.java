package com.dynamicg.homebuttonlauncher.dialog;

import android.content.DialogInterface;

import com.dynamicg.homebuttonlauncher.MenuGlobals;
import com.dynamicg.homebuttonlauncher.OnClickListenerDialogWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
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
		OnClickListenerDialogWrapper okListener = new OnClickListenerDialogWrapper(context) {
			@Override
			public void onClickImpl(DialogInterface d, int which) {
				dialog.doSortReset();
			}
		};
		DialogHelper.confirm(context, R.string.menuReset, okListener);
	}

}
