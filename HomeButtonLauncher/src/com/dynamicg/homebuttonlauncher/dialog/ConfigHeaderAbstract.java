package com.dynamicg.homebuttonlauncher.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.R;

public abstract class ConfigHeaderAbstract {

	protected final AppConfigDialog dialog;
	protected final Context context;
	protected final TextView titleNode;
	protected final View iconNode;

	public ConfigHeaderAbstract(AppConfigDialog dialog) {
		this.dialog = dialog;
		this.context = dialog.getContext();
		this.titleNode = ((TextView)dialog.findViewById(R.id.headerTitle));
		this.iconNode = dialog.findViewById(R.id.headerIcon);
	}

	public abstract void attach();

	public void setTitleAndWidth(int label) {
		titleNode.setText(label);
		int width = (int)dialog.getContext().getResources().getDimension(R.dimen.widthAppConfig);
		View container = dialog.findViewById(R.id.headerContainer);
		container.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
	}

}