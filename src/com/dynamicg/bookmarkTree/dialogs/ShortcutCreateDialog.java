package com.dynamicg.bookmarkTree.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.prefs.SpinnerUtil;
import com.dynamicg.bookmarkTree.util.DialogButtonPanelWrapper;
import com.dynamicg.bookmarkTree.util.DialogHelper;
import com.dynamicg.common.Logger;

public class ShortcutCreateDialog extends Dialog {

	private static final Logger log = new Logger(ShortcutCreateDialog.class);

	private static final int DEFAULT_DENSITY = 160;

	private final Context context;
	private final Bookmark bookmark;
	private final SpinnerUtil spinnerUtil;
	private final ShortcutCreateWorker shortcutCreateWorker;
	private final Dialog opener;

	private TextView titleCell;
	private ImageView previewCell;
	private Spinner densitySpinner;
	private int backgroundColor = Color.WHITE;

	public ShortcutCreateDialog(Dialog opener, BookmarkTreeContext ctx, Bookmark bookmark) {
		super(ctx.activity);
		this.opener = opener;
		this.context = getContext();
		this.bookmark = bookmark;
		this.spinnerUtil = new SpinnerUtil(this);
		this.shortcutCreateWorker = new ShortcutCreateWorker(context);

		this.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		DialogHelper.expandContent(this, R.layout.create_shortcut_body);
		setTitle(R.string.commonCreateShortcut);

		new DialogButtonPanelWrapper(this, DialogButtonPanelWrapper.TYPE_CREATE_CANCEL) {
			@Override
			public void onPositiveButton() {
				String title = titleCell.getText().toString();
				String url = bookmark.getUrl();
				shortcutCreateWorker.create(getIcon(), title, url);
				// close window and opener (which is "edit dialog")
				dismiss();
				opener.dismiss();
			}
		};

		/*
		 * TITLE
		 */
		titleCell = (TextView)findViewById(R.id.shortcutTitle);
		titleCell.setText(bookmark.getDisplayTitle());

		/*
		 * COLOR PICKER
		 */
		Button colorPicker = (Button)findViewById(R.id.shortcutBgColor);
		colorPicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPickerDialog.ColorSelectedListener colorChangedListener = new ColorPickerDialog.ColorSelectedListener() {
					@Override
					public void colorSelected(int selectedColor) {
						backgroundColor = selectedColor;
						if (log.isDebugEnabled) {
							log.debug("ColorPickerDialog.ColorSelectedListener");
						}
						drawPreviewIcon();
					}
				};
				new ColorPickerDialog(context, backgroundColor, colorChangedListener, true);
			}
		});

		/*
		 * DENSITY
		 */
		setLabel(R.id.shortcutIconDensityLabel, R.string.shortcutIconScale);
		int defaultDensity = DEFAULT_DENSITY;
		densitySpinner = (Spinner)findViewById(R.id.shortcutIconDensity);
		spinnerUtil.bind ( R.id.shortcutIconDensity, defaultDensity, SpinnerUtil.getShortcutBitmapDensity(), R.string.shortcutIconScale);
		
		// override the default "on change" listener:
		final long initGracetime = System.currentTimeMillis() + 300l; // screen setup triggers "onItemSeleced" so we catch the initial call
		densitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (System.currentTimeMillis()<initGracetime) {
					return;
				}
				if (log.isDebugEnabled) {
					log.debug("densitySpinner.setOnItemSelected");
				}
				drawPreviewIcon();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		/*
		 * DONE
		 */
		previewCell = (ImageView)findViewById(R.id.shortcutIconPreview);
		drawPreviewIcon();
	}

	private void setLabel(int res, int title) {
		((TextView)findViewById(res)).setText(context.getString(title)+":");
	}

	private Bitmap getIcon() {
		int color = this.backgroundColor;
		int targetDensity = spinnerUtil.getCurrentValue(R.id.shortcutIconDensity);
		if (log.isDebugEnabled) {
			log.debug("getIcon() - density/color", targetDensity, color);
		}
		return shortcutCreateWorker.getIcon(bookmark.getFavicon(), color, targetDensity);
	}

	private void drawPreviewIcon() {
		if (log.isDebugEnabled) {
			log.debug("=== preview()");
		}
		previewCell.setImageBitmap(getIcon());
	}

}
