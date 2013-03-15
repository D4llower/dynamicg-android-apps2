package com.dynamicg.homebuttonlauncher.tools;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;

@SuppressLint("HandlerLeak")
public class BackgroundIconLoader {

	/*
	 * see
	 * http://android-developers.blogspot.ch/2010/07/multithreading-for-performance.html
	 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
	 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	 */

	private static final Logger log = new Logger(BackgroundIconLoader.class);

	private final AppListContainer applist;
	private final int iconSizePx;
	private final LargeIconLoader largeIconLoader;
	private final Handler handler;
	private final boolean forMainScreen;
	private final boolean iconsLeft;

	private final ArrayList<TextView> views = new ArrayList<TextView>();

	protected Thread thread;
	protected boolean running;
	protected int highWaterMark = 0;

	public BackgroundIconLoader(
			final AppListContainer applist
			, final int iconSizePx
			, final LargeIconLoader largeIconLoader
			, final boolean forMainScreen
			, final boolean iconsLeft
			)
	{
		this.applist = applist;
		this.iconSizePx = iconSizePx;
		this.largeIconLoader = largeIconLoader;
		this.forMainScreen = forMainScreen;
		this.iconsLeft = iconsLeft;

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					updateIcon(msg.what);
				}
				catch (Throwable t) {
					Logger.dumpIfDevelopment(t);
				}
			}
		};
	}

	private void updateIcon(int index) {
		TextView row = views.get(index);
		views.set(index, null);

		int[] positions = (int[])row.getTag();
		final AppEntry appEntry = applist.get(positions[0]);
		if (positions[0]!=positions[1] && appEntry.isIconLoaded()) {
			positions[1] = positions[0];
			log.trace("-- UPDATE ICON --", positions[0], appEntry.label);
			Drawable icon = appEntry.getIcon();
			if (iconsLeft) {
				row.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
			}
			else {
				row.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
			}
		}
		else {
			log.trace("== SKIP UPDATE ==", positions[0], positions[1], appEntry.isIconLoaded());
		}
	}

	protected void runLoader() {
		if (running) {
			return;
		}

		log.debug("##>> START LOADER", views.size());

		running = true;

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					// initial delay
					SystemUtil.sleep(50);

					while (true) {
						int size1 = views.size();
						for (int i=highWaterMark;i<size1;i++) {

							// get next entry
							TextView row = views.get(i);
							if (row==null) {
								if (log.isDebugEnabled) {
									throw new RuntimeException("runLoader - NULL AT ["+highWaterMark+"]["+i+"]");
								}
								continue;
							}

							// process
							int[] positions = (int[])row.getTag();
							AppEntry appEntry = applist.get(positions[0]);
							if (!appEntry.isIconLoaded()) {
								appEntry.getIcon(iconSizePx, largeIconLoader, forMainScreen);
							}

							if (positions[0]!=positions[1]) {
								handler.sendEmptyMessage(i);
							}
							else {
								log.trace("## SKIP UPDATE ##", positions[0], positions[1], appEntry.isIconLoaded());
							}

							// move to next
							highWaterMark = i+1;
						}

						SystemUtil.sleep(50);
						log.debug("WAIT AND CHECK QUEUE", highWaterMark, views.size());

						if (highWaterMark>=views.size()) {
							running = false;
							break;
						}
					}
				}
				catch (Throwable t) {
					running = false;
					Logger.dumpIfDevelopment(t);
				}
			}
		});

		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/*
	 * note the row tag format:
	 * int[]{requested position, index position of most recent displayed icon}
	 * i.e. whenever p1!=p2 then view needs update
	 */
	public void queue(TextView row) {
		views.add(row);
		runLoader();
	}

}
