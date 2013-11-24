package com.dynamicg.homebuttonlauncher.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.R;

public class ViewHolder {

	public final TextView label;
	public final ImageView image;

	public int position;
	public int imgPosition = -1;

	public ViewHolder(ViewGroup layout) {
		this.label = (TextView)layout.findViewById(R.id.rowlabel);
		this.image = (ImageView)layout.findViewById(R.id.rowicon);
	}

}
