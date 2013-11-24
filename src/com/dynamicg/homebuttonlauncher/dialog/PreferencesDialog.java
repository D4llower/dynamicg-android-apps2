package com.dynamicg.homebuttonlauncher.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
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

@SuppressLint("HandlerLeak")
public class PreferencesDialog extends Dialog {

	private static final Logger log = new Logger(PreferencesDialog.class);

	private final PreferencesManager preferences;
	private final PrefSettings prefSettings;
	private final MainActivityHome activity;

	private int selectedLayout;

	private SeekBarHelper seekbarLabelSize;
	private SeekBarHelper seekbarIconSize;
	private SeekBarHelper seekbarNumTabs;
	private TransparencyAlphaHelper transparencyAlphaHelper;

	private CheckBox chkHighRes;
	private CheckBox chkAutoStartSingle;
	private CheckBox chkBackgroundIconLoader;
	private CheckBox chkSemiTransparent;
	private CheckBox chkStatusLine;

	private SpinnerHelper homeTabHelper;

	public PreferencesDialog(MainActivityHome activity, PreferencesManager preferences) {
		super(activity);
		setCanceledOnTouchOutside(false);
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

		seekbarLabelSize = new SeekBarHelper(this, R.id.prefsLabelSize, SizePrefsHelper.LABEL_SIZES, prefSettings.getLabelSize());
		seekbarLabelSize.attachDefaultIndicator(R.id.prefsLabelSizeIndicator);

		seekbarIconSize = new SeekBarHelper(this, R.id.prefsIconSize, SizePrefsHelper.ICON_SIZES, prefSettings.getIconSize());
		seekbarIconSize.attachDefaultIndicator(R.id.prefsIconSizeIndicator);

		seekbarNumTabs = new SeekBarHelper(this, R.id.prefsNumTabs, SizePrefsHelper.NUM_TABS, prefSettings.getNumTabs());
		seekbarNumTabs.attachDefaultIndicator(R.id.prefsNumTabsIndicator);

		chkHighRes = attachCheckbox(R.id.prefsHighResIcon, prefSettings.isHighResIcons());
		chkAutoStartSingle = attachCheckbox(R.id.prefsAutoStartSingle, prefSettings.isAutoStartSingle());
		chkBackgroundIconLoader = attachCheckbox(R.id.prefsBackgroundIconLoader, prefSettings.isBackgroundIconLoader());
		chkSemiTransparent = attachCheckbox(R.id.prefsSemiTransparent, prefSettings.isSemiTransparent());
		chkStatusLine = attachCheckbox(R.id.prefsStatusLine, prefSettings.isShowStatusLine());

		transparencyAlphaHelper = new TransparencyAlphaHelper();
		transparencyAlphaHelper.setVisibility(chkSemiTransparent.isChecked()); // initial setting

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

		chkSemiTransparent.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				transparencyAlphaHelper.setVisibility(isChecked);
			}
		});

		attachHomeTab();
	}

	private void attachHomeTab() {
		homeTabHelper = new SpinnerHelper(this, R.id.prefsHomeTab);
		final View container = findViewById(R.id.prefsHomeTabContainer);

		final ValueChangeListener spinnerUpdateHandler = new ValueChangeListener() {
			@Override
			public void valueChanged(final int previousHomeTab) {
				final int maxTabs = seekbarNumTabs.getNewValue(); // this is 0,2,3,...
				final int newHomeTab = maxTabs>=previousHomeTab?previousHomeTab:0; // previousHomeTab is tabnum not tabindex.

				log.debug("tabs max/currentHome", maxTabs, newHomeTab);
				SpinnerHelper.SpinnerEntries items = new SpinnerHelper.SpinnerEntries();
				items.add(0, ""); // pos 0 = none
				for (int idx=0;idx<maxTabs;idx++) {
					// pos 1 to n is "tabindex+1"
					items.addPadded(idx+1, idx+1);
				}
				homeTabHelper.bind(items, newHomeTab);

				// apply visibility
				container.setVisibility(maxTabs>0?View.VISIBLE:View.GONE);
			}
		};

		seekbarNumTabs.setOnValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(int newValue) {
				// reuse current selection if applicable
				spinnerUpdateHandler.valueChanged(homeTabHelper.getSelectedValue());
			}
		});

		// initial setup
		spinnerUpdateHandler.valueChanged(prefSettings.getHomeTabNum());
	}

	private CheckBox attachCheckbox(int id, boolean checked) {
		CheckBox box = (CheckBox)findViewById(id);
		box.setChecked(checked);
		return box;
	}

	public class TransparencyAlphaHelper {
		final int offset = 155;
		final int max = 50;
		final int mod = 2;
		final int original = prefSettings.getTransparencyAlpha();
		final SeekBar bar = (SeekBar)findViewById(R.id.prefsTransparencyAlpha);

		TransparencyAlphaHelper() {
			bar.setProgress((original-offset)/mod);
			bar.setMax(max);
		}
		int getNewValue() {
			return bar.getProgress()*mod+offset;
		}
		void setVisibility(boolean visible) {
			bar.setVisibility(visible?View.VISIBLE:View.GONE);
		}
		boolean isChanged() {
			return original!=getNewValue();
		}
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

	private int getNewHomeTabNum() {
		return seekbarNumTabs.getNewValue()>0 ? homeTabHelper.getSelectedValue() : 0;
	}

	private boolean updateCurrentTab() {
		final int currentTabIndex = preferences.getTabIndex();
		final int oldNumTabs = seekbarNumTabs.initialValue;
		final int newNumTabs = seekbarNumTabs.getNewValue();
		final int oldHomeTabNum = prefSettings.getHomeTabNum();
		final int newHomeTabNum = getNewHomeTabNum();

		if (currentTabIndex>=newNumTabs || (oldHomeTabNum>0 && newHomeTabNum==0)) {
			// reset to first tab
			preferences.updateCurrentTabIndex(0);
			return true;
		}
		else if (newHomeTabNum>0 && newHomeTabNum!=oldHomeTabNum) {
			// changed home tab
			preferences.updateCurrentTabIndex(newHomeTabNum-1);
			return true;
		}

		return oldNumTabs!=newNumTabs;
	}

	private void saveSettings() {
		final boolean transparencyChanged = prefSettings.isSemiTransparent()!=chkSemiTransparent.isChecked();
		final boolean statusLineChanged = prefSettings.isShowStatusLine()!=chkStatusLine.isChecked();
		final boolean tabRefreshRequired = updateCurrentTab();

		saveSharedPrefs();

		if (tabRefreshRequired) {
			activity.redrawTabContainer();
		}

		GlobalContext.resetCache();
		activity.refreshList();
		HomeLauncherBackupAgent.requestBackup(getContext());

		if (transparencyChanged || statusLineChanged) {
			Toast.makeText(activity, R.string.prefsPleaseRestart, Toast.LENGTH_SHORT).show();
			dismiss();
			activity.finish();
		}
		else {
			if (chkSemiTransparent.isChecked() && transparencyAlphaHelper.isChanged()) {
				activity.setBackgroundTransparency(false);
			}
			dismiss();
		}
	}

	private void saveSharedPrefs() {
		Editor edit = prefSettings.sharedPrefs.edit();

		edit.putInt(PrefSettings.KEY_LAYOUT, selectedLayout);
		edit.putInt(PrefSettings.KEY_LABEL_SIZE, seekbarLabelSize.getNewValue());
		edit.putInt(PrefSettings.KEY_ICON_SIZE, seekbarIconSize.getNewValue());
		edit.putInt(PrefSettings.KEY_NUM_TABS, seekbarNumTabs.getNewValue());
		edit.putInt(PrefSettings.KEY_TRANS_ALPHA, transparencyAlphaHelper.getNewValue());
		edit.putInt(PrefSettings.KEY_HOME_TAB_NUM, getNewHomeTabNum());

		edit.putBoolean(PrefSettings.KEY_HIGH_RES, chkHighRes.isChecked());
		edit.putBoolean(PrefSettings.KEY_AUTO_START_SINGLE, chkAutoStartSingle.isChecked());
		edit.putBoolean(PrefSettings.KEY_BACKGROUND_ICON_LOADER, chkBackgroundIconLoader.isChecked());
		edit.putBoolean(PrefSettings.KEY_SEMI_TRANSPARENT, chkSemiTransparent.isChecked());
		edit.putBoolean(PrefSettings.KEY_STATUS_LINE, chkStatusLine.isChecked());

		edit.apply();
	}

}
