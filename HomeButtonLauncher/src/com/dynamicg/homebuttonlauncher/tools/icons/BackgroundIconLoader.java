package com.dynamicg.homebuttonlauncher.tools.icons;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.AppEntry;
import com.dynamicg.homebuttonlauncher.AppListContainer;
import com.dynamicg.homebuttonlauncher.adapter.ViewHolder;

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

	private final ArrayList<ViewHolder> views = new ArrayList<ViewHolder>();

	protected Thread thread;
	protected boolean running;
	protected int highWaterMark = 0;

	public BackgroundIconLoader(
			final AppListContainer applist
			, final int iconSizePx
			, final LargeIconLoader largeIconLoader
			, final boolean forMainScreen
			)
	{
		this.applist = applist;
		this.iconSizePx = iconSizePx;
		this.largeIconLoader = largeIconLoader;
		this.forMainScreen = forMainScreen;

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					updateIcon(msg.what);
				}
				catch (Throwable t) {
					SystemUtil.dumpIfDevelopment(t);
				}
			}
		};
	}

	private void updateIcon(int index) {
		ViewHolder row = views.get(index);
		views.set(index, null);

		final AppEntry appEntry = applist.get(row.position);
		if (row.position!=row.imgPosition && appEntry.isIconLoaded()) {
			row.imgPosition = row.position;
			log.trace("-- UPDATE ICON --", row.position, appEntry.label);
			Drawable icon = appEntry.getIconDrawable();
			row.image.setImageDrawable(icon);
		}
		else {
			log.trace("== SKIP UPDATE ==", row.position, row.imgPosition, appEntry.isIconLoaded());
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
							ViewHolder row = views.get(i);
							if (row==null) {
								if (log.isDebugEnabled) {
									throw new RuntimeException("runLoader - NULL AT ["+highWaterMark+"]["+i+"]");
								}
								highWaterMark++; // avoid endless loop (regardless of why we got a null item)
								continue;
							}

							// process
							AppEntry appEntry = applist.get(row.position);
							if (!appEntry.isIconLoaded()) {
								appEntry.getIcon(iconSizePx, largeIconLoader, forMainScreen);
							}

							if (row.position!=row.imgPosition) {
								handler.sendEmptyMessage(i);
							}
							else {
								log.trace("## SKIP UPDATE ##", row.position, row.imgPosition, appEntry.isIconLoaded());
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
					SystemUtil.dumpIfDevelopment(t);
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
	public void queue(ViewHolder row) {
		views.add(row);
		runLoader();
	}

}
