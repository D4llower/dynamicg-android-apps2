package com.dynamicg.bookmarkTree.prefs;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.dynamicg.bookmarkTree.R;

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
	
	public void bind(final int spinnerResId, int currentKey, ArrayList<KeyValue> items, int prompt) {
		Spinner spinner = (Spinner)dialog.findViewById(spinnerResId);
		ArrayAdapter<KeyValue> adapter = new ArrayAdapter<KeyValue> ( spinner.getContext()
				, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setPromptId(prompt);
		
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
	
	public void bind(final int spinnerResId, PrefEntryInt prefEntry, ArrayList<KeyValue> items, int prompt) {
		bind(spinnerResId, prefEntry.value, items, prompt);
	}
	
	public int getCurrentValue(int spinnerResId) {
		Spinner spinner = (Spinner)dialog.findViewById(spinnerResId);
		KeyValue item = (KeyValue)spinner.getSelectedItem();
		return item==null ? 0 : item.key;
	}
	
	public boolean isChanged(int spinnerResId) {
		return dirtyItems.contains(spinnerResId);
	}
	
	private static class SpinnerItems {
		final ArrayList<KeyValue> list = new ArrayList<KeyValue>();
		final Context context;
		public SpinnerItems(Context context) {
			this.context = context;
		}
		public void add(int key, int title) {
			list.add(new KeyValue(key,context.getString(title)));
		}
	}
	
	public static ArrayList<KeyValue> getListStyleItems(Context context) {
		SpinnerItems items = new SpinnerItems(context);
		items.add ( PreferencesWrapper.LIST_STYLE_CLASSIC, R.string.domainStyleClassic );
		items.add ( PreferencesWrapper.LIST_STYLE_COMPACT, R.string.domainStyleCompact );
		return items.list;
	}
	
	public static ArrayList<KeyValue> getSortOptionItems(Context context) {
		SpinnerItems items = new SpinnerItems(context);
		items.add ( PreferencesWrapper.SORT_ALPHA, R.string.domainSortAlphaOverall );
		items.add ( PreferencesWrapper.SORT_FOLDERS_FIRST, R.string.domainSortFoldersFirst );
		items.add ( PreferencesWrapper.SORT_BOOKMARKS_FIRST, R.string.domainSortBookmarksFirst );
		return items.list;
	}
	
}
