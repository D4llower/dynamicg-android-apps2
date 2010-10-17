package com.dynamicg.bookmarkTree.util;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

public abstract class DialogButtonPanelWrapper {

	private final Dialog dialog;

	public DialogButtonPanelWrapper(final Dialog dialog
			, int residPositiveButton
			, int residNegativeButton
			) {
		this.dialog = dialog;

		Button positiveButton = (Button)dialog.findViewById(residPositiveButton);
		positiveButton.setOnClickListener ( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onPositiveButton();
			}
		} );

		Button negativeButton = (Button)dialog.findViewById(residNegativeButton);
		negativeButton.setOnClickListener ( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onNegativeButton();
			}
		} );
		
	}
	
	public abstract void onPositiveButton();
	
	public void onNegativeButton() {
		dialog.dismiss();
	}
	
}
