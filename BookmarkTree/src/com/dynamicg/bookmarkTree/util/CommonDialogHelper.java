package com.dynamicg.bookmarkTree.util;

import android.app.Dialog;
import android.view.ViewStub;

import com.dynamicg.bookmarkTree.R;

public class CommonDialogHelper {

	public static void expandContent(Dialog dialog, int layout) {
		dialog.setContentView(R.layout.common_popup_dialog);
		ViewStub stub = (ViewStub)dialog.findViewById(R.id.dialogScrollViewBody);
		stub.setLayoutResource(layout);
		stub.inflate();
	}

}
