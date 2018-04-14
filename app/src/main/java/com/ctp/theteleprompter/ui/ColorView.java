package com.ctp.theteleprompter.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ctp.theteleprompter.R;


public class ColorView extends View {

    private float luminance = 0;
    private String colorHex = "#000000";
    private final float scale = getResources().getDisplayMetrics().density;
    private boolean showText;
    private Paint paint = new Paint();

    public ColorView(Context context) {
        super(context);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Custom XML attribute to display text in view default TRUE
        // XML attribute {app:show_text="false"}
        TypedArray typed = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ColorView, 0, 0);
        showText = typed.getBoolean(R.styleable.ColorView_show_text, true);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!showText) {
            return;
        }

        float GESTURE_THRESHOLD_DP = 16.0f;

        paint.setColor((luminance >= 0.5) ? Color.BLACK : Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (GESTURE_THRESHOLD_DP * scale + 0.7f));

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int fontHeight = (int) (fontMetrics.descent + fontMetrics.ascent);

        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2 - fontHeight / 2;
        canvas.drawText(colorHex, x, y, paint);
    }

    /**
     * Set color to a view and get its luminance for text
     *
     * @param color represent a color integer
     */

    @Override
    public void setBackgroundColor(@ColorInt int color) {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N)
            luminance = (float) ColorUtils.calculateLuminance(color);
        else
            luminance = Color.luminance(color);

        colorHex = String.format("#%06X", (0xFFFFFF & color));
        super.setBackgroundColor(color);
    }

    public void setColor(String color) {
        setBackgroundColor(Color.parseColor(color));
    }

    public void setColor(@ColorInt int color) {
        setBackgroundColor(color);
    }

    public boolean isShowingText() {
        return showText;
    }
}
