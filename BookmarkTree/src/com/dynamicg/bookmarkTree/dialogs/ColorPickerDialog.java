package com.dynamicg.bookmarkTree.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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
	
	private final Context context;
	private final ColorSelectedListener colorSelectedListener;
	
	private final int paddingLayout;
	private final int paddingSeekbarTop;
	private final int paddingSeekbarBottom;
	private final int paddingSeekbarLR;
	
	private TextView titleCell;
	
	private IntHolder colorPickerRed = new IntHolder();
	private IntHolder colorPickerGreen = new IntHolder();
	private IntHolder colorPickerBlue = new IntHolder();

	private static class IntHolder {
		public int value=0;
	}

	public static interface ColorSelectedListener {
		public void colorSelected(int selectedColor);
	}
	
	public ColorPickerDialog(Context context, int color, ColorSelectedListener colorSelectedListener) {
		super(context);
		this.context = context;
		this.colorSelectedListener = colorSelectedListener;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		maximizeWindowWidth();
		
		paddingLayout = ContextUtil.getScaledSizeInt(context, 10);
		
		paddingSeekbarTop = ContextUtil.getScaledSizeInt(context, 10);
		paddingSeekbarBottom = ContextUtil.getScaledSizeInt(context, 30);
		paddingSeekbarLR = ContextUtil.getScaledSizeInt(context, 15);
		
		initColor(color);
		
		this.show();
	}

	private void initColor(final int color) {
		colorPickerRed.value = Color.red(color);
		colorPickerGreen.value = Color.green(color);
		colorPickerBlue.value = Color.blue(color);
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

		layout.addView ( createBar(colorPickerRed, R.drawable.progress_bg_red) );
		layout.addView ( createBar(colorPickerGreen, R.drawable.progress_bg_green) );
		layout.addView ( createBar(colorPickerBlue, R.drawable.progress_bg_blue) );
		
		Button btSave = new Button(context);
		btSave.setText(R.string.commonOK);
		btSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (colorSelectedListener!=null) {
					colorSelectedListener.colorSelected(getSelectedColor());
				}
				else {
					System.err.println("COLOR SELECTED:"+getSelectedColor());
				}
				dismiss();
			}
		});
		layout.addView(btSave);
		
		updateTitleColor();
		
		ScrollView scrollView = new ScrollView(context);
		scrollView.addView(layout);
		this.setContentView(scrollView);
		
		// maximize window width
		//LayoutUtil.maximizeDialog(this);
		maximizeWindowWidth();
	}
	
	private void maximizeWindowWidth() {
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}
	
	private int getSelectedColor() {
		return Color.rgb(colorPickerRed.value, colorPickerGreen.value, colorPickerBlue.value);
	}
	
	private void updateTitleColor() {
		titleCell.setTextColor(getSelectedColor());
	}
	
	private Drawable getBackground(int res) {
		return context.getResources().getDrawable(res);
	}
	
	private SeekBar createBar(final IntHolder target, int background) {
		
		SeekBar slider = new SeekBar(context);
		slider.setProgressDrawable(getBackground(background));
		
		slider.setMax(255);
		slider.setProgress(target.value);
		slider.setPadding(paddingSeekbarLR, paddingSeekbarTop, paddingSeekbarLR, paddingSeekbarBottom);
		
		slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private void update(SeekBar seekBar) {
				target.value = seekBar.getProgress();
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
		
		return slider;
	}
	
}
