package com.dynamicg.bookmarkTree.util;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import com.dynamicg.bookmarkTree.R;

public abstract class DialogButtonPanelWrapper {

	private final Dialog dialog;

	public DialogButtonPanelWrapper(final Dialog dialog) {
		this(dialog, R.id.buttonOk, R.id.buttonCancel);
	}
	
	private DialogButtonPanelWrapper(final Dialog dialog
			, int residPositiveButton
			, int residNegativeButton
			) 
	{
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
