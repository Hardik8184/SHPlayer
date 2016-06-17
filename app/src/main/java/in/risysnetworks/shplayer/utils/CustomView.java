/*
 * This is the source code of SHPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @ RiSysNetworks 2016.
 */
package in.risysnetworks.shplayer.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomView extends RelativeLayout {

	final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
	final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

	final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
	int beforeBackground;

	// Indicate if user touched this view the last time
	public boolean isLastTouch = false;

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled)
			setBackgroundColor(beforeBackground);
		else
			setBackgroundColor(disabledBackgroundColor);
		invalidate();
	}

	boolean animation = false;

	@Override
	protected void onAnimationStart() {
		super.onAnimationStart();
		animation = true;
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		animation = false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (animation)
			invalidate();
	}
}
