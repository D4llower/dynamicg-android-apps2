package com.dynamicg.homebuttonlauncher;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

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

	// there is this nasty 4.2 bug which, when a sub activity is started, will throw the user back to the main activity on resume,
	// that is unless we close the menu beforehand and do some plain "wait()"
	//	private void itemClicked(final PopupMenuItemListener listener, final int id) {
	//		Handler handler = new Handler() {
	//			@Override
	//			public void handleMessage(Message msg) {
	//
	//				// there is this nasty 4.2 bug which, when a sub activity is started, will throw the user back to the main activity on resume,
	//				// that is unless we close the menu beforehand and do some plain "wait()"
	//				if (android.os.Build.VERSION.SDK_INT==17) {
	//					SystemUtil.sleep(350);
	//				}
	//
	//				listener.popupMenuItemSelected(id);
	//			}
	//		};
	//
	//		handler.sendEmptyMessage(0);
	//	}

	public void attachToAnchorClick() {
		anchor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupMenu.show();
			}
		});
	}

	public void showMenu() {
		popupMenu.show();
	}

	public void addItem(int id, int titleResId) {
		menu.add(0, id, 0, titleResId);
	}

}
