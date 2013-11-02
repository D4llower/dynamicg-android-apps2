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
import com.dynamicg.bookmarkTree.backup.BackupPrefs;

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
	
	public void bindSpinnerItems(Spinner spinner, int currentKey, ArrayList<KeyValue> items, int prompt) {
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
	}
	
	public void bind(final int spinnerResId, int currentKey, ArrayList<KeyValue> items, int prompt) {
		Spinner spinner = (Spinner)dialog.findViewById(spinnerResId);
		bindSpinnerItems(spinner, currentKey, items, prompt);
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
		public SpinnerItems() {
			this.context = null;
		}
		public void add(int key, int titleRes) {
			list.add(new KeyValue(key,context.getString(titleRes)));
		}
		public void add(int key, String title) {
			list.add(new KeyValue(key,title));
		}
	}
	
	public static ArrayList<KeyValue> getListStyleItems(Context context) {
		SpinnerItems items = new SpinnerItems(context);
		items.add ( PreferencesWrapper.LIST_SIZE_LARGE, R.string.domainListStyleLarge);
		items.add ( PreferencesWrapper.LIST_SIZE_MEDIUM, R.string.domainListStyleMedium );
		items.add ( PreferencesWrapper.LIST_SIZE_SMALL, R.string.domainListStyleSmall );
		return items.list;
	}
	
	public static ArrayList<KeyValue> getSortOptionItems(Context context) {
		SpinnerItems items = new SpinnerItems(context);
		items.add ( PreferencesWrapper.SORT_ALPHA, R.string.domainSortAlphaOverall );
		items.add ( PreferencesWrapper.SORT_FOLDERS_FIRST, R.string.domainSortFoldersFirst );
		items.add ( PreferencesWrapper.SORT_BOOKMARKS_FIRST, R.string.domainSortBookmarksFirst );
		return items.list;
	}
	
	public static ArrayList<KeyValue> getShortcutBitmapDensity() {
		SpinnerItems items = new SpinnerItems();
		items.add (  80, "2.0x (80dpi)" );
		items.add ( 120, "1.5x (120dpi)" );
		items.add ( 160, "Default (160dpi)" );
		items.add ( 240, "0.75x (240dpi)" );
		items.add ( 320, "0.5x (320dpi)" );
		return items.list;
	}
	
	public static ArrayList<KeyValue> getIconScalingItems(Context context) {
		SpinnerItems items = new SpinnerItems();
		items.add ( PreferencesWrapper.ICON_SCALING_NONE, context.getString(R.string.commonNone) );
		items.add ( PreferencesWrapper.ICON_SCALING_160, "160dpi" );
		items.add ( PreferencesWrapper.ICON_SCALING_160_120, "160dpi, 120dpi" );
		items.add ( PreferencesWrapper.ICON_SCALING_160_100, "160dpi, 100dpi" );
		items.add ( PreferencesWrapper.ICON_SCALING_160_80, "160dpi, 80dpi" );
		items.add ( PreferencesWrapper.ICON_SCALING_240, "240dpi" );
		items.add ( PreferencesWrapper.ICON_SCALING_240_160, "240dpi, 160dpi" );
		return items.list;
	}
	
	public static ArrayList<KeyValue> getAutoBackupItems(Context context) {
		SpinnerItems items = new SpinnerItems();
		items.add ( BackupPrefs.BCK_OFF, context.getString(R.string.commonNone) );
		items.add ( BackupPrefs.BCK_5, "5" );
		items.add ( BackupPrefs.BCK_10, "10" );
		items.add ( BackupPrefs.BCK_20, "20" );
		return items.list;
	}
	
}
