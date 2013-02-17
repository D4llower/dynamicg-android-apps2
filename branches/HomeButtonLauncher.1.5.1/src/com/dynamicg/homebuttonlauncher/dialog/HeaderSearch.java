package com.dynamicg.homebuttonlauncher.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.dynamicg.common.Logger;
import com.dynamicg.homebuttonlauncher.AppEntry;

public class HeaderSearch extends HeaderAbstract {

	private static final Logger log = new Logger(HeaderSearch.class);

	private final List<AppEntry> baseAppList;

	private String[] baseSearchLabels = null; // lazy

	public HeaderSearch(AppConfigDialog dialog) {
		super(dialog);
		this.baseAppList = dialog.appList.getApps();
	}

	private void initSearchLabels() {
		baseSearchLabels = new String[baseAppList.size()];
		for (int i=0;i<baseAppList.size();i++) {
			baseSearchLabels[i] = baseAppList.get(i).label.toLowerCase(Locale.getDefault());
		}
	}

	@Override
	public void attach() {

		OnQueryTextListener onQueryTextListener = new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return true;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				log.debug("onQueryTextChange", newText);
				updateAppList(newText);
				return true;
			}
		};

		OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
			private boolean titleVisible = true;
			@Override
			public void onFocusChange(View view, boolean queryTextFocused) {
				log.debug("onFocusChange", queryTextFocused);
				if (titleVisible) {
					titleNode.setVisibility(View.GONE);
					titleVisible = false;
				}
			}
		};

		final SearchView search = new SearchView(context);
		search.setOnQueryTextListener(onQueryTextListener);
		search.setOnQueryTextFocusChangeListener(onFocusChangeListener);

		iconNode.setVisibility(View.GONE);
		ViewGroup container = ((ViewGroup)iconNode.getParent());
		container.addView(search, container.indexOfChild(iconNode));
	}

	private void updateAppList(String query) {

		log.debug("updateAppList", query);

		if (baseAppList.size()==0) {
			return;
		}

		if (query==null || query.length()==0) {
			dialog.updateAppList(baseAppList);
			return;
		}

		if (baseSearchLabels==null) {
			initSearchLabels();
		}

		final String searchstr = query.toLowerCase(Locale.getDefault());
		List<AppEntry> matchingApps = new ArrayList<AppEntry>();
		for (int i=0;i<baseSearchLabels.length;i++) {
			if (baseSearchLabels[i].contains(searchstr)) {
				log.debug("matching app", baseSearchLabels[i]);
				matchingApps.add(baseAppList.get(i));
			}
		}
		dialog.updateAppList(matchingApps);
	}

}