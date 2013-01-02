package com.dynamicg.homebuttonlauncher.tools;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.dynamicg.homebuttonlauncher.OnClickListenerWrapper;

public class PopupMenuWrapper {

	public interface PopupMenuItemListener {
		public void popupMenuItemSelected(int id);
	}

	private final View anchor;
	private final PopupMenu popupMenu;
	private final Menu menu;

	public PopupMenuWrapper(final Context context, final View anchor, final PopupMenuItemListener listener) {
		this.anchor = anchor;
		this.popupMenu = new PopupMenu(context, anchor);
		this.menu = popupMenu.getMenu();

		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					listener.popupMenuItemSelected(item.getItemId());
				}
				catch (Throwable t) {
					ErrorHandler.showCrashReport(context, t);
				}
				return true;
			}
		});
	}

	public void attachToAnchorClick() {
		anchor.setOnClickListener(new OnClickListenerWrapper() {
			@Override
			public void onClickImpl(View v) {
				popupMenu.show();
			}
		});
	}

	public void showMenu() {
		popupMenu.show();
	}

	public void addItem(int id, int titleResId) {
		menu.add(id, id, 0, titleResId);
	}

	public void addItem(int id, int titleResId, boolean enabled) {
		menu.add(id, id, 0, titleResId);
		if (!enabled) {
			menu.setGroupEnabled(id, false);
		}
	}

}
