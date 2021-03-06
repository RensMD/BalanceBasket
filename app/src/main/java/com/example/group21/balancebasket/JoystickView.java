package com.example.group21.balancebasket;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
    private OnJoystickChangeListener listener;

    final int darkTeal = Color.parseColor("#00796B");
    final int buttonTeal = Color.parseColor("#009688");

    private int buttonColor = buttonTeal;

    private float x, y; // These are in the intern coordinates
    private double lastX, lastY; // These are in the external coordinates
    private float buttonRadius;
    private float joystickRadius = 0;
    private float centerX;
    private float centerY;

    Paint p = new Paint();

    public JoystickView(Context context) {
        super(context);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // Make the layout square
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (joystickRadius == 0) { // Check if it has been set yet
            joystickRadius = (float) getWidth() / 3;
            buttonRadius = joystickRadius / 2;
            centerX = (float) getWidth() / 2;
            centerY = (float) getHeight() / 2;
            x = centerX;
            y = centerY;
        }

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(30);
        p.setColor(buttonTeal);
        canvas.drawCircle(centerX, centerY, joystickRadius, p);
        canvas.drawCircle(centerX, centerY, joystickRadius / 2, p);

        p.setColor(buttonColor);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, buttonRadius, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        float abs = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        if (abs > joystickRadius) {
            x = ((x - centerX) * joystickRadius / abs + centerX);
            y = ((y - centerY) * joystickRadius / abs + centerY);
        }

        if (lastX == 0 && lastY == 0 && (getXValue() > 0.50 || getXValue() < -0.50 || getYValue() > 0.50 || getYValue() < -0.50)) {
            x = centerX;
            y = centerY;
            return true;
        }
        lastX = getXValue();
        lastY = getYValue();

        invalidate();

        if (listener != null) {
            int actionMask = event.getActionMasked();
            if (actionMask == MotionEvent.ACTION_DOWN) {
                buttonColor = darkTeal;
                listener.setOnTouchListener(getXValue(), getYValue());
                return true;
            } else if (actionMask == MotionEvent.ACTION_MOVE) {
                buttonColor = darkTeal;
                listener.setOnMovedListener(getXValue(), getYValue());
                return true;
            } else if (actionMask == MotionEvent.ACTION_UP || actionMask == MotionEvent.ACTION_CANCEL) {
                buttonColor = buttonTeal;
                x = centerX;
                y = centerY;
                lastX = 0;
                lastY = 0;
                listener.setOnReleaseListener(0, 0);
                return true;
            }
        }
        return false;
    }

    public double getXValue() {
        return (x - centerX) / joystickRadius; // X-axis is positive at the right side
    }

    public double getYValue() {
        return -((y - centerY) / joystickRadius); // Y-axis should be positive upwards
    }

    public void setOnJoystickChangeListener(OnJoystickChangeListener listener) {
        this.listener = listener;
    }

    public interface OnJoystickChangeListener {
        void setOnTouchListener(double xValue, double yValue);

        void setOnMovedListener(double xValue, double yValue);

        void setOnReleaseListener(double xValue, double yValue);
    }
}
