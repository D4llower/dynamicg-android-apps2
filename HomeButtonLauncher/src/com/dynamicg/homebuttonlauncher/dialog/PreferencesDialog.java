package com.dynamicg.homebuttonlauncher.dialog;

import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.header.HeaderAbstract;
import com.dynamicg.homebuttonlauncher.dialog.header.HeaderPreferences;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class PreferencesDialog extends Dialog {

	private static final Logger log = new Logger(PreferencesDialog.class);

	private static final int TAG_OLD_VALUE = R.id.buttonCancel;
	private static final int TAG_NEW_VALUE = R.id.buttonOk;

	private final PreferencesManager preferences;
	private final PrefSettings prefSettings;
	private final MainActivityHome activity;

	private int selectedLayout;
	private SeekBar seekbarLabelSize;
	private SeekBar seekbarIconSize;
	private SeekBar seekbarNumTabs;
	private CheckBox highRes;
	private CheckBox autoStartSingle;
	private CheckBox backgroundIconLoader;
	private CheckBox semiTransparent;

	public PreferencesDialog(MainActivityHome activity, PreferencesManager preferences) {
		super(activity);
		this.activity = activity;
		this.preferences = preferences;
		this.prefSettings = preferences.prefSettings;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.preferences);

		DialogHelper.prepareCommonDialog(this, R.layout.preferences_body, R.layout.button_panel_2, true);

		HeaderAbstract header = new HeaderPreferences(this, activity);
		header.attach(R.string.preferences);

		seekbarLabelSize = attachSeekBar(R.id.prefsLabelSize, R.id.prefsLabelSizeIndicator, SizePrefsHelper.LABEL_SIZES, prefSettings.getLabelSize());
		seekbarIconSize = attachSeekBar(R.id.prefsIconSize, R.id.prefsIconSizeIndicator, SizePrefsHelper.ICON_SIZES, prefSettings.getIconSize());
		seekbarNumTabs = attachSeekBar(R.id.prefsNumTabs, R.id.prefsNumTabsIndicator, SizePrefsHelper.NUM_TABS, prefSettings.getNumTabs());

		highRes = attachCheckbox(R.id.prefsHighResIcon, prefSettings.isHighResIcons());
		autoStartSingle = attachCheckbox(R.id.prefsAutoStartSingle, prefSettings.isAutoStartSingle());
		backgroundIconLoader = attachCheckbox(R.id.prefsBackgroundIconLoader, prefSettings.isBackgroundIconLoader());
		semiTransparent = attachCheckbox(R.id.prefsSemiTransparent, prefSettings.isSemiTransparent());

		setupLayoutToggle();

		findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				dismiss();
			}
		});

		findViewById(R.id.buttonOk).setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				saveSettings();
			}
		});

	}

	private CheckBox attachCheckbox(int id, boolean checked) {
		CheckBox box = (CheckBox)findViewById(id);
		box.setChecked(checked);
		return box;
	}

	private SeekBar attachSeekBar(final int id, final int indicatorId, final int[] values, final int initialValue) {
		final SeekBar bar = (SeekBar)findViewById(id);
		SizePrefsHelper.setSeekBar(bar, initialValue, values);
		bar.setTag(TAG_NEW_VALUE, initialValue);
		bar.setTag(TAG_OLD_VALUE, initialValue);

		final TextView indicator = (TextView)findViewById(indicatorId);
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					int selectedValue = SizePrefsHelper.getSelectedValue(bar, values);
					indicator.setText("["+selectedValue+"]");
					bar.setTag(TAG_NEW_VALUE, selectedValue);
				}
			}
		});

		return bar;
	}

	private void setLayoutSelection(View parent, int which) {
		selectedLayout = which;
		for (int i=0;i<PrefSettings.NUM_LAYOUTS;i++) {
			View toggle = parent.findViewWithTag("toggle_"+i);
			toggle.setBackgroundResource(which==i?android.R.color.holo_blue_light:0);
		}
	}

	private void setupLayoutToggle() {
		final ViewGroup parent = (ViewGroup)findViewById(R.id.prefLayoutToggle);
		final View.OnClickListener clickListener = new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				int which = Integer.parseInt(v.getTag().toString());
				setLayoutSelection(parent, which);
			}
		};
		for (int i=0;i<PrefSettings.NUM_LAYOUTS;i++) {
			View image = parent.findViewWithTag(Integer.toString(i));
			image.setOnClickListener(clickListener);
		}
		setLayoutSelection(parent, prefSettings.getLayoutType());
	}

	private static int getNewValue(SeekBar bar) {
		return (Integer)bar.getTag(TAG_NEW_VALUE);
	}

	private void saveSettings() {

		final boolean transparencyChanged = prefSettings.isSemiTransparent()!=semiTransparent.isChecked();
		final int oldNumTabs = prefSettings.getNumTabs();
		final int newNumTabs = getNewValue(seekbarNumTabs);
		final int currentTabIndex = preferences.getTabIndex();
		log.debug("saveSettings", oldNumTabs, newNumTabs, currentTabIndex);

		if (currentTabIndex>=newNumTabs) {
			// reset to first tab if current is above max
			preferences.updateCurrentTabIndex(0);
		}

		saveSharedPrefs();

		if (oldNumTabs!=newNumTabs) {
			// redraw tabs when changed
			activity.redrawTabContainer();
		}

		GlobalContext.resetCache();
		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(getContext());

		if (transparencyChanged) {
			Toast.makeText(activity, R.string.prefsPleaseRestart, Toast.LENGTH_SHORT).show();
			dismiss();
			activity.finish();
		}
		else {
			dismiss();
		}
	}

	private void saveSharedPrefs() {
		Editor edit = prefSettings.sharedPrefs.edit();
		edit.putInt(PrefSettings.KEY_LAYOUT, selectedLayout);
		edit.putInt(PrefSettings.KEY_LABEL_SIZE, getNewValue(seekbarLabelSize));
		edit.putInt(PrefSettings.KEY_ICON_SIZE, getNewValue(seekbarIconSize));
		edit.putInt(PrefSettings.KEY_NUM_TABS, getNewValue(seekbarNumTabs));
		edit.putBoolean(PrefSettings.KEY_HIGH_RES, highRes.isChecked());
		edit.putBoolean(PrefSettings.KEY_AUTO_START_SINGLE, autoStartSingle.isChecked());
		edit.putBoolean(PrefSettings.KEY_BACKGROUND_ICON_LOADER, backgroundIconLoader.isChecked());
		edit.putBoolean(PrefSettings.KEY_SEMI_TRANSPARENT, semiTransparent.isChecked());
		edit.commit();
	}

}
