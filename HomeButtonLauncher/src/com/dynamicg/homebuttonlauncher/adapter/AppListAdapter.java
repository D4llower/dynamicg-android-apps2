package com.dynamicg.homebuttonlauncher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.GlobalContext;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.dialog.SizePrefsHelper;
import com.dynamicg.homebuttonlauncher.preferences.PrefSettings;
import com.dynamicg.homebuttonlauncher.tools.BackgroundIconLoader;
import com.dynamicg.homebuttonlauncher.tools.DialogHelper;
import com.dynamicg.homebuttonlauncher.tools.IconProvider;
import com.dynamicg.homebuttonlauncher.tools.LargeIconLoader;

public abstract class AppListAdapter extends BaseAdapter {

	protected final AppListContainer applist;
	protected final LayoutInflater inflater;
	protected final boolean forMainScreen;
	protected final boolean useBackgroundLoader;

	protected int labelSize;
	protected int iconSizePx;
	protected LargeIconLoader largeIconLoader;
	protected int appEntryLayoutId;

	protected int noLabelGridPadding;
	private LocalViewBinder localViewBinder;
	private boolean dataSetChanged;

	private AppListAdapter(Activity activity, AppListContainer apps, boolean forMainScreen) {
		this.applist = apps;
		this.inflater = activity.getLayoutInflater();
		this.forMainScreen = forMainScreen;
		this.useBackgroundLoader = GlobalContext.prefSettings.isBackgroundIconLoader();
	}

	/*
	 * for main screen
	 */
	public AppListAdapter(Activity activity, AppListContainer apps) {
		this(activity, apps, true);
		PrefSettings settings = GlobalContext.prefSettings;
		this.labelSize = settings.getLabelSize();
		this.iconSizePx = IconProvider.getSizePX(settings.getIconSize());
		this.largeIconLoader = LargeIconLoader.createInstance(activity, settings);
		postInit(activity, settings.getAppEntryLayoutId());
	}

	/*
	 * for config screens
	 */
	public AppListAdapter(Activity activity, AppListContainer apps, int viewId) {
		this(activity, apps, false);
		this.labelSize = SizePrefsHelper.DEFAULT_LABEL_SIZE;
		this.iconSizePx = IconProvider.getDefaultSizePX();
		this.largeIconLoader = null;
		postInit(activity, viewId);
	}

	private void postInit(Context context, int layoutId) {

		this.localViewBinder = useBackgroundLoader ? new LocalViewBinderAsync(context.getResources()) : new LocalViewBinderDefault();

		appEntryLayoutId = layoutId;
		if (useBackgroundLoader) {
			switch (layoutId) {
			case R.layout.app_entry_default: appEntryLayoutId=R.layout.app_entry_default_bl; break;
			case R.layout.app_entry_compact: appEntryLayoutId=R.layout.app_entry_compact_bl; break;
			}
		}

		if (labelSize==0 && (appEntryLayoutId==R.layout.app_entry_compact||appEntryLayoutId==R.layout.app_entry_compact_bl)) {
			noLabelGridPadding = DialogHelper.getDimension(context, R.dimen.gridViewNoLabelIconPadding);
		}
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	@Override
	public int getCount() {
		return applist.size();
	}

	@Override
	public Object getItem(int position) {
		return applist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getOrCreateView(View convertView) {
		return localViewBinder.provideView(convertView);
	}

	protected void setLabel(TextView label, AppEntry appEntry) {
		if (this.labelSize==0) {
			label.setText("");
		}
		else {
			label.setText(appEntry.getLabel());
		}
	}

	public void bindView(int position, AppEntry appEntry, View row) {
		localViewBinder.bindView(position, appEntry, row);
	}

	public abstract class LocalViewBinder {
		public abstract View provideView(View convertView);
		public abstract void bindView(int position, AppEntry appEntry, View row);
	}

	public class LocalViewBinderDefault extends LocalViewBinder {

		@Override
		public View provideView(View convertView) {
			if (convertView==null) {
				final TextView row = (TextView)inflater.inflate(appEntryLayoutId, null);
				row.setTextSize(labelSize);
				if (noLabelGridPadding>0) {
					row.setCompoundDrawablePadding(noLabelGridPadding);
				}
				return row;
			}
			return (TextView)convertView;
		}

		@Override
		public void bindView(int position, AppEntry appEntry, View row) {
			TextView label = (TextView)row;
			setLabel(label, appEntry);
			Drawable icon = appEntry.getIcon(iconSizePx, largeIconLoader, forMainScreen);
			if (appEntryLayoutId==R.layout.app_entry_compact) {
				// icon on top
				label.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
			}
			else {
				// icon left
				label.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
			}
		}

	}

	public class LocalViewBinderAsync extends LocalViewBinder {

		private final BackgroundIconLoader backgroundIconLoader;
		private final Drawable defaultIcon;

		public LocalViewBinderAsync(Resources res) {
			this.backgroundIconLoader = new BackgroundIconLoader(applist, iconSizePx, largeIconLoader, forMainScreen);
			this.defaultIcon = IconProvider.scale(res.getDrawable(R.drawable.android), iconSizePx);
		}

		@Override
		public View provideView(View convertView) {
			if (convertView==null) {
				// create view and holder
				final LinearLayout row = (LinearLayout)inflater.inflate(appEntryLayoutId, null);
				ViewHolder holder = new ViewHolder(row);
				row.setTag(holder);
				// label size init
				holder.label.setTextSize(labelSize);
				if (noLabelGridPadding>0) {
					holder.image.setPadding(0, 0, 0, noLabelGridPadding);
				}
				return row;
			}
			return (LinearLayout)convertView;
		}

		@Override
		public void bindView(int position, AppEntry appEntry, View row) {
			final ViewHolder holder = (ViewHolder)row.getTag();
			holder.position = position;
			setLabel(holder.label, appEntry);

			Drawable icon;
			if (dataSetChanged) {
				// once "search" has been applied do not use bg loader any more
				// (syncing the potentially running bg loader thread to the new applist is too complicated ...)
				icon = appEntry.getIcon(iconSizePx, largeIconLoader, forMainScreen);
			}
			else if (appEntry.isIconLoaded()) {
				icon = appEntry.getIconDrawable();
			}
			else {
				icon = defaultIcon;
			}
			holder.image.setImageDrawable(icon);

			if (!appEntry.isIconLoaded()) {
				backgroundIconLoader.queue(holder);
			}
		}

	}

	@Override
	public void notifyDataSetChanged() {
		dataSetChanged = true;
		super.notifyDataSetChanged();
	}

}