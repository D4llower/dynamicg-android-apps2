package com.dynamicg.homebuttonlauncher.tools;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TabHost;

import com.dynamicg.common.Logger;
import com.dynamicg.common.SystemUtil;
import com.dynamicg.homebuttonlauncher.MainActivityHome;
import com.dynamicg.homebuttonlauncher.R;
import com.dynamicg.homebuttonlauncher.preferences.PreferencesManager;

// see http://stackoverflow.com/questions/937313/android-basic-gesture-detection
// TODO ## fling animation ??
public class SwipeHelper {

	private static final Logger log = new Logger(SwipeHelper.class);

	public static void attach(
			final MainActivityHome activity
			, final PreferencesManager preferences
			, final TabHost tabhost
			, final View anchor
			)
	{

		final Context context = activity;
		final ViewConfiguration vc = ViewConfiguration.get(context);
		final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();

		final int swipeMaxOffPath = DialogHelper.getDimension(R.dimen.swipeMaxOff);
		final int swipeMinDistance = DialogHelper.getDimension(R.dimen.swipeMinDist);

		final SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {

			private boolean flip(int direction) {
				final int current = preferences.getTabIndex();
				final int count = preferences.prefSettings.getNumTabs();
				final int newIndex = current+direction;
				if (newIndex>=0 && newIndex<count) {
					tabhost.setCurrentTab(newIndex);
				}
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				log.debug("### SWIPE", Math.abs(e1.getX() - e2.getX())/1.5f, Math.abs(e1.getY() - e2.getY())/1.5f);

				try {
					if (Math.abs(e1.getY() - e2.getY()) > swipeMaxOffPath) {
						return false;
					}

					if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
						return flip(+1);
					} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
						return flip(-1);
					}
				} catch (Exception e) {
					SystemUtil.dumpIfDevelopment(e);
				}
				return false;
			}
		};

		final GestureDetector gestureDetector = new GestureDetector(context, onGestureListener);
		final View.OnTouchListener gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		anchor.setOnTouchListener(gestureListener);
	}

}