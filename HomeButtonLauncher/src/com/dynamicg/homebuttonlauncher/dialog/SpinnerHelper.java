package com.dynamicg.homebuttonlauncher.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SpinnerHelper {

	private final Spinner spinner;

	public SpinnerHelper(Dialog dialog, int id) {
		this.spinner = (Spinner)dialog.findViewById(id);
	}

	public void bind(ArrayList<String> items, int selectedPosition) {
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(selectedPosition);
	}

	public int getSelectedPosition() {
		return spinner.getSelectedItemPosition();
	}

}
