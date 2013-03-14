package com.dynamicg.homebuttonlauncher.tools;

import java.util.ConcurrentModificationException;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.AppEntry;

@SuppressLint("HandlerLeak")
public class BackgroundIconLoader {

	private static final Logger log = new Logger(BackgroundIconLoader.class);

	private final int iconSizePx;
	private final LargeIconLoader largeIconLoader;
	private final HashSet<TextView> queue = new HashSet<TextView>();
	private final Handler handler;

	private final boolean forMainScreen;
	private final boolean iconsLeft;

	protected Thread thread;
	protected boolean isRunning;

	public BackgroundIconLoader(final int iconSizePx, final LargeIconLoader largeIconLoader, final boolean forMainScreen, final boolean iconsLeft) {
		this.iconSizePx = iconSizePx;
		this.largeIconLoader = largeIconLoader;
		this.forMainScreen = forMainScreen;
		this.iconsLeft = iconsLeft;

		this.handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				TextView row = (TextView)msg.obj;
				synchronized (row) {
					// by now the app icon should have been loaded so this is really fast:
					// TODO use appEntry.getIcon() ?
					setIcon(row, getIconFromViewTag(row));
				}
			}
		};
	}

	protected Drawable getIconFromViewTag(TextView row) {
		AppEntry appEntry = (AppEntry)row.getTag();
		return appEntry.getIcon(iconSizePx, largeIconLoader, forMainScreen);
	}

	protected void setIcon(TextView row, Drawable icon) {
		if (iconsLeft) {
			row.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		}
		else {
			row.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
		}
	}

	void runLoader() {
		if (isRunning) {
			return;
		}

		log.debug("##>> START LOADER", queue.size());

		isRunning = true;

		thread = new Thread(new Runnable() {

			protected TextView getNext() {
				try {
					return queue.iterator().next();
				}
				catch (ConcurrentModificationException e) {
					return queue.iterator().next();
				}
			}

			@Override
			public void run() {
				try {
					SystemUtil.sleep(50); // initial delay
					while (true) {
						while (queue.size()>0) {
							TextView row = getNext();
							queue.remove(row);
							getIconFromViewTag(row);
							handler.sendMessage(handler.obtainMessage(0, row));
						}
						SystemUtil.sleep(50);
						log.debug("WAIT AND CHECK QUEUE", queue.size());
						if (queue.size()==0) {
							isRunning = false;
							break;
						}
					}
				}
				catch (Throwable t) {
					// most probably a "concurrent access" error at queue.iterator().next()
					/*
					03-14 09:43:02.929: W/System.err(7688): java.util.ConcurrentModificationException
					03-14 09:43:02.929: W/System.err(7688): 	at java.util.HashMap$HashIterator.nextEntry(HashMap.java:792)
					03-14 09:43:02.941: W/System.err(7688): 	at java.util.HashMap$KeyIterator.next(HashMap.java:819)
					03-14 09:43:02.957: W/System.err(7688): 	at com.dynamicg.homebuttonlauncher.tools.BackgroundIconLoader$2.run(BackgroundIconLoader.java:71)
					03-14 09:43:02.964: W/System.err(7688): 	at java.lang.Thread.run(Thread.java:856)
					 */
					isRunning = false;
					if (log.isDebugEnabled) {
						t.printStackTrace();
					}
				}
			}
		});

		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void queue(final AppEntry appEntry, final TextView row) {

		row.setTag(appEntry);
		if (appEntry.isIconLoaded()) {
			setIcon(row, appEntry.getIcon());
			return;
		}

		synchronized (row) {
			row.setTag(appEntry);
		}
		queue.add(row);

		runLoader();
	}

}
