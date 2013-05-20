package com.dynamicg.bookmarkTree.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.common.ContextUtil;

public class ColorPickerDialog extends Dialog {

	private static final int TEXTSIZE = 22;
	private static final int BUTTON_WIDTH = 110;

	// alternatively, use max=255 increment=1
	private static final int PROGRESS_MAX = 85;
	private static final int PROGRESS_INCREMENT = 3;

	private final Context context;
	private final ColorSelectedListener colorSelectedListener;

	private final int paddingLayout;
	private final int paddingSeekbarTB;
	private final int paddingSeekbarLR;
	private final boolean withAlpha;

	private TextView titleCell;

	private ColorHolder colorPickerRed = new ColorHolder();
	private ColorHolder colorPickerGreen = new ColorHolder();
	private ColorHolder colorPickerBlue = new ColorHolder();
	private ColorHolder colorPickerAlpha = new ColorHolder();

	private static class ColorHolder {
		public int color=0;
	}

	public static interface ColorSelectedListener {
		public void colorSelected(int selectedColor);
	}

	public ColorPickerDialog(Context context, int color, ColorSelectedListener colorSelectedListener, boolean withAlpha) {
		super(context);
		this.context = context;
		this.colorSelectedListener = colorSelectedListener;
		this.withAlpha = withAlpha;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		maximizeWindowWidth();

		paddingLayout = scale(10);
		paddingSeekbarLR = scale(15);
		paddingSeekbarTB = scale(3);

		initColor(color);

		this.show();
	}

	private int scale(int i) {
		return ContextUtil.getScaledSizeInt(context, i);
	}

	private void initColor(final int color) {
		colorPickerRed.color = Color.red(color);
		colorPickerGreen.color = Color.green(color);
		colorPickerBlue.color = Color.blue(color);
		if (withAlpha) {
			colorPickerAlpha.color = Color.alpha(color);
		}
	}

	private void addMarginCell(LinearLayout parent, int h) {
		TextView cell = new TextView(context);
		cell.setHeight(scale(h));
		parent.addView(cell);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(layoutParams);
		layout.setPadding(paddingLayout,paddingLayout,paddingLayout,paddingLayout);
		layout.setBackgroundColor(Color.BLACK);

		this.titleCell = new TextView(context);
		titleCell.setText(R.string.titleColorPicker);
		titleCell.setTextSize(TEXTSIZE);
		layout.addView(titleCell);

		addMarginCell(layout, 5);
		addProgressBar(layout, colorPickerRed, R.drawable.progress_bg_red);
		addMarginCell(layout, 30);
		addProgressBar(layout, colorPickerGreen, R.drawable.progress_bg_green);
		addMarginCell(layout, 30);
		addProgressBar(layout, colorPickerBlue, R.drawable.progress_bg_blue);
		if (withAlpha) {
			addMarginCell(layout, 30);
			addProgressBar(layout, colorPickerAlpha, R.drawable.progress_bg_alpha);
		}
		addMarginCell(layout, 20);

		updateTitleColor();

		addButtonPanel(layout);

		ScrollView scrollView = new ScrollView(context);
		scrollView.addView(layout);
		this.setContentView(scrollView);

		// maximize window width
		maximizeWindowWidth();
	}

	private void maximizeWindowWidth() {
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}

	private Button createButton(int labelId) {
		Button button = new Button(context);
		button.setText(labelId);
		button.setWidth(scale(BUTTON_WIDTH));
		return button;
	}

	private void addButtonPanel(LinearLayout parent) {
		LinearLayout panel = new LinearLayout(context);
		panel.setOrientation(LinearLayout.HORIZONTAL);
		panel.setGravity(Gravity.CENTER_HORIZONTAL);

		Button btSave = createButton(R.string.commonOK);
		btSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (colorSelectedListener!=null) {
					colorSelectedListener.colorSelected(getSelectedColor());
				}
				dismiss();
			}
		});

		Button btCancel = createButton(R.string.commonCancel);
		btCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		panel.addView(btCancel);
		panel.addView(btSave);

		parent.addView(panel);

	}

	private int getSelectedColor() {
		if (withAlpha) {
			return Color.argb(colorPickerAlpha.color, colorPickerRed.color, colorPickerGreen.color, colorPickerBlue.color);
		}
		else {
			return Color.rgb(colorPickerRed.color, colorPickerGreen.color, colorPickerBlue.color);
		}
	}

	private void updateTitleColor() {
		titleCell.setTextColor(getSelectedColor());
	}

	private Drawable getBackground(int res) {
		return context.getResources().getDrawable(res);
	}

	private void addProgressBar(ViewGroup parent, final ColorHolder target, int background) {

		SeekBar slider = new SeekBar(context);
		slider.setProgressDrawable(getBackground(background));

		slider.setMax(PROGRESS_MAX);
		slider.setProgress(target.color/PROGRESS_INCREMENT);
		slider.setPadding(paddingSeekbarLR, paddingSeekbarTB, paddingSeekbarLR, paddingSeekbarTB);

		slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private void update(SeekBar seekBar) {
				target.color = seekBar.getProgress()*PROGRESS_INCREMENT;
				updateTitleColor();
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				update(seekBar);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				update(seekBar);
			}
		});

		parent.addView(slider);
	}

}
