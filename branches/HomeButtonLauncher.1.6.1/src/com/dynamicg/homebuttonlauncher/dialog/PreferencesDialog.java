package com.dynamicg.homebuttonlauncher.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
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

	public PreferencesDialog(MainActivityHome activity, PreferencesManager preferences) {
		super(activity);
		this.activity = activity;
		this.preferences = preferences;
		this.prefSettings = preferences.prefSettings;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.preferences);

		DialogHelper.prepareCommonDialog(this, R.layout.preferences_body, R.layout.button_panel_2);

		seekbarLabelSize = attachSeekBar(R.id.prefsLabelSize, R.id.prefsLabelSizeIndicator, SizePrefsHelper.LABEL_SIZES, prefSettings.getLabelSize());
		seekbarIconSize = attachSeekBar(R.id.prefsIconSize, R.id.prefsIconSizeIndicator, SizePrefsHelper.ICON_SIZES, prefSettings.getIconSize());
		seekbarNumTabs = attachSeekBar(R.id.prefsNumTabs, R.id.prefsNumTabsIndicator, SizePrefsHelper.NUM_TABS, prefSettings.getNumTabs());

		highRes = (CheckBox)findViewById(R.id.prefsHighResIcon);
		highRes.setChecked(prefSettings.isHighResIcons());

		autoStartSingle = (CheckBox)findViewById(R.id.prefsAutoStartSingle);
		autoStartSingle.setChecked(prefSettings.isAutoStartSingle());

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
				dismiss();
			}
		});

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
			toggle.setBackgroundResource(which==i?R.drawable.tools_selector_shape:0);
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

	private static boolean isChanged(SeekBar bar) {
		int i1 = (Integer)bar.getTag(TAG_OLD_VALUE);
		int i2 = (Integer)bar.getTag(TAG_NEW_VALUE);
		return i1!=i2;
	}

	private void saveSettings() {
		int numTabs = (Integer)seekbarNumTabs.getTag(TAG_NEW_VALUE);
		prefSettings.writeAppSettings(
				selectedLayout
				, getNewValue(seekbarLabelSize)
				, getNewValue(seekbarIconSize)
				, highRes.isChecked()
				, autoStartSingle.isChecked()
				, getNewValue(seekbarNumTabs)
				);

		if (isChanged(seekbarNumTabs)) {
			// redraw tabs only if "numTabs" has changed
			log.debug("saveSettings", "redraw tabs", numTabs, seekbarNumTabs.getTag());
			preferences.switchShortlist(preferences.getTabIndex());
			activity.redrawTabContainer();
		}

		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(getContext());
	}

}