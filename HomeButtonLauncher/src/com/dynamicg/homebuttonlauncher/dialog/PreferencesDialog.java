package com.dynamicg.homebuttonlauncher.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.preferences.HomeLauncherBackupAgent;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;

public class PreferencesDialog extends Dialog {

	private final PrefSettings prefSettings;
	private final MainActivityHome activity;

	private SeekBar seekbarLabelSize;
	private SeekBar seekbarIconSize;
	private int selectedLayout;

	public PreferencesDialog(MainActivityHome activity, PreferencesManager preferences) {
		super(activity);
		this.activity = activity;
		this.prefSettings = preferences.prefSettings;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.preferences);

		DialogHelper.prepareCommonDialog(this, R.layout.preferences_body, R.layout.button_panel_2);

		seekbarLabelSize = (SeekBar)findViewById(R.id.prefsLabelSize);
		SizePrefsHelper.setLabelSize(seekbarLabelSize, prefSettings.getLabelSize());

		seekbarIconSize = (SeekBar)findViewById(R.id.prefsIconSize);
		SizePrefsHelper.setIconSize(seekbarIconSize, prefSettings.getIconSize());

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

	private void saveSettings() {
		int labelSize = SizePrefsHelper.getLabelSize(seekbarLabelSize);
		int iconSize = SizePrefsHelper.getIconSize(seekbarIconSize);
		prefSettings.writeAppSettings(selectedLayout, labelSize, iconSize);
		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(getContext());
	}

}
