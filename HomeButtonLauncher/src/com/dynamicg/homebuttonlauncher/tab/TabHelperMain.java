package com.dynamicg.homebuttonlauncher.tab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnClickListenerDialogWrapper;
import com.dynamicg.homebuttonlauncher.OnLongClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;

public class TabHelperMain extends TabHelper {

	private static final Logger log = new Logger(TabHelperMain.class);

	private final PreferencesManager preferences;

	public TabHelperMain(MainActivityHome activity, PreferencesManager preferences) {
		super(activity, preferences.prefSettings.getNumTabs());
		this.preferences = preferences;
	}

	@Override
	public TabHost bindTabs() {
		final int selectedIndex = preferences.getTabIndex();

		TabHost.OnTabChangeListener onTabChangeListener = new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				int index = Integer.parseInt(tabId);
				activity.updateOnTabSwitch(index);
			};
		};

		View.OnLongClickListener longClickListener = new OnLongClickListenerWrapper() {
			@Override
			public boolean onLongClickImpl(View v) {
				int index = (Integer)v.getTag();
				editLabel(index);
				return true;
			}
		};

		String[] labels = new String[numTabs];
		for (int i=0;i<numTabs;i++) {
			labels[i] = preferences.getTabTitle(i);
		}

		TabHost tabhost = bindTabs(selectedIndex, labels, onTabChangeListener, longClickListener);
		return tabhost;
	}

	protected void editLabel(final int index) {
		final String currentLabel = preferences.getTabTitle(index);

		final EditText editor = new EditText(context);
		editor.setText(currentLabel);
		editor.setSingleLine();
		if (currentLabel.length()>0) {
			editor.setSelection(currentLabel.length());
		}
		editor.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		DialogInterface.OnClickListener okListener = new OnClickListenerDialogWrapper(context) {
			@Override
			public void onClickImpl(DialogInterface dialog, int which) {
				String newLabel = editor.getText().toString();
				newLabel = newLabel!=null?newLabel:"";
				setLabel(index, newLabel);
				preferences.writeTabTitle(index, newLabel);
			}
		};
		builder.setPositiveButton(R.string.buttonOk, okListener );
		builder.setNegativeButton(R.string.buttonCancel, null);
		builder.setView(editor);
		AlertDialog dialog = builder.show();

		// auto open keyboard
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	private void setLabel(int index, String label) {
		log.debug("set label", label, index);
		//tabs[index].setIndicator(label); // does not work
		TextView title = (TextView)tabviews[index].findViewById(android.R.id.title);
		if (title!=null) {
			title.setText(label);
		}
	}

	public TabHost redraw() {
		View tabContainer = activity.findViewById(android.R.id.tabhost);
		if (tabContainer!=null) {
			((ViewGroup)tabContainer.getParent()).removeView(tabContainer);
		}

		if (numTabs==0) {
			return null;
		}

		return bindTabs();
	}

}
