package com.dynamicg.common.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.R;

public abstract class SimpleAlertDialog {

	private final Context context;
	
	/**
	 * 
	 * @param context
	 * @param residAlertTitle
	 * @param residButtonTitles - positive, negative, neutral
	 */
	public SimpleAlertDialog ( Context context
			, int residAlertTitle
			, int... residButtonTitles
			)
	{
		this ( context, context.getString(residAlertTitle), residButtonTitles);
	}
	
	public SimpleAlertDialog ( Context context
			, String alertTitle
			, int... residButtonTitles
			)
	{
		this.context = context;
		String tPos = stringOrNull(residButtonTitles, 0);
		String tNeg = stringOrNull(residButtonTitles, 1);
		String tNeut = stringOrNull(residButtonTitles, 2);
		show ( alertTitle, tPos, tNeg, tNeut);
	}
	
	public SimpleAlertDialog ( Context context
			, String alertTitle
			, String... buttonTitles
			)
	{
		this.context = context;
		show ( alertTitle, stringOrNull(buttonTitles,0), stringOrNull(buttonTitles,1), stringOrNull(buttonTitles,2) );
	}
	
	private static String stringOrNull(String[] values, int pos) {
		return values.length>pos ? values[pos] : null;
	}
	
	private String stringOrNull(int[] resid, int pos) {
		return resid.length>pos ? context.getString(resid[pos]): null;
	}
	
	private void show ( String alertTitle
			, String positiveButtonTitle
			, String negativeButtonTitle
			, String neutralButtonTitle
			)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		if (this.showAsMessage()) {
			builder.setMessage(alertTitle);
		}
		else {
			builder.setTitle(alertTitle);
		}
		
		builder.setPositiveButton(positiveButtonTitle, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				onPositiveButton();
			}
		});
		
		if (negativeButtonTitle!=null) {
			builder.setNegativeButton(negativeButtonTitle, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					onNegativeButton();
				}
			});
		}
		
		if (neutralButtonTitle!=null) {
			builder.setNeutralButton(neutralButtonTitle, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					onNeutralButton();
				}
			});
		}
		
		View body = getBody();
		if (body!=null) {
			builder.setView(body);
		}
		else {
			String text = getPlainBodyText();
			if (text!=null && text.length()>0) {
				builder.setView(createTextView(text));
			}
		}
		
		builder.show();
		
//		AlertDialog dialog = builder.show();
//		System.err.println("-------------------1 "+dialog.getButton(0));
//		System.err.println("-------------------2 "+dialog.getButton(AlertDialog.BUTTON_POSITIVE));
//		System.err.println("-------------------3 "+dialog.getButton(AlertDialog.BUTTON1));
		
	}

	public void onPositiveButton() {
	}
	
	public void onNegativeButton() {
	}
	
	public void onNeutralButton() {
	}
	
	public View getBody() {
		return null;
	}
	
	public String getPlainBodyText() {
		return null;
	}
	
	public TextView createTextView(String text) {
		TextView textview = new TextView(context);
		textview.setText(text);
		textview.setPadding(5,5,5,5);
		return textview;
	}
	
	/*
	 * if alert title is too long for default popup, return "true" in overwritten subclass
	 */
	public boolean showAsMessage() {
		return false;
	}
	
	public static void plainInfo(Context context, int title) {
		new SimpleAlertDialog(context, title, R.string.commonClose) {
		};
	}
	
}
