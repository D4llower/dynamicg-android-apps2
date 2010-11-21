package com.dynamicg.bookmarkTree.ui;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dynamicg.bookmarkTree.PreferencesWrapper;

public class SpinnerUtil {

	private final Dialog dialog;
	private final HashSet<Integer> dirtyItems = new HashSet<Integer>();

	public SpinnerUtil(Dialog dialog) {
		this.dialog = dialog;
	}
	
	public static class KeyValue {
		public int key;
		public String value;
		public KeyValue(int key, String value) {
			this.key = key;
			this.value = value;
		}
		public String toString() {
			return value; // for spinner
		}
	}
	
	public void bind(final int spinnerResId, int currentKey, ArrayList<KeyValue> items) {
		Spinner spinner = (Spinner)dialog.findViewById(spinnerResId);
		ArrayAdapter<KeyValue> adapter = new ArrayAdapter<KeyValue> ( spinner.getContext()
				, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		
		for (int i=0;i<items.size();i++) {
			if (items.get(i).key==currentKey) {
				spinner.setSelection(i);
				break;
			}
		}
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				dirtyItems.add(spinnerResId);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
	}
	
	public int getCurrentValue(int spinnerResId) {
		Spinner spinner = (Spinner)dialog.findViewById(spinnerResId);
		KeyValue item = (KeyValue)spinner.getSelectedItem();
		return item==null ? 0 : item.key;
	}
	
	public boolean isChanged(int spinnerResId) {
		return dirtyItems.contains(spinnerResId);
	}
	
	public static ArrayList<KeyValue> getListStyleItems() {
		ArrayList<KeyValue> list = new ArrayList<KeyValue>();
		list.add(new KeyValue(PreferencesWrapper.LIST_STYLE_CLASSIC, "Classic"));
		list.add(new KeyValue(PreferencesWrapper.LIST_STYLE_COMPACT, "Compact"));
		return list;
	}
	
	public static ArrayList<KeyValue> getSortOptionItems() {
		ArrayList<KeyValue> list = new ArrayList<KeyValue>();
		list.add(new KeyValue(PreferencesWrapper.SORT_ALPHA, "Alpha overall"));
		list.add(new KeyValue(PreferencesWrapper.SORT_FOLDERS_BEFORE_BM, "Folders first"));
		list.add(new KeyValue(PreferencesWrapper.SORT_BM_BEFORE_FOLDERS, "Bookmarks first"));
		return list;
	}
	
}
