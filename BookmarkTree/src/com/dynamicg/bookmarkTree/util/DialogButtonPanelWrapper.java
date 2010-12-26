package com.dynamicg.bookmarkTree.util;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import com.dynamicg.bookmarkTree.R;

public abstract class DialogButtonPanelWrapper {

	public static final int TYPE_SAVE_CANCEL = 1;
	public static final int TYPE_CLOSE = 2;
	
	private final Dialog dialog;
	
	private final int button1 = R.id.button1;
	private final int button2 = R.id.button2;

	public DialogButtonPanelWrapper(Dialog dialog, int what) {
		this.dialog = dialog;
		if (what==TYPE_SAVE_CANCEL) {
			setup(R.string.commonSave, R.string.commonCancel);
		}
		else if (what==TYPE_CLOSE) {
			setup(R.string.commonClose, 0 );
		}
	}
	
	private Button getButton(int res) {
		return (Button)dialog.findViewById(res);
	}
	
	private void setup ( int titleButton1, int titleButton2 ) {
		Button positiveButton = getButton(button1);
		positiveButton.setOnClickListener ( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onPositiveButton();
			}
		} );
		positiveButton.setText(titleButton1);

		Button negativeButton = getButton(button2);
		if (titleButton2>0) {
			negativeButton.setOnClickListener ( new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onNegativeButton();
				}
			} );
			negativeButton.setText(titleButton2);
		}
		else {
			negativeButton.setVisibility(View.GONE);
		}
		
	}
	
	public abstract void onPositiveButton();
	
	public void onNegativeButton() {
		dialog.dismiss();
	}
	
}
