package ru.devdem.reminder.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import ru.devdem.reminder.R;

public class HoldButton extends AppCompatButton {

    public int Strength;
    public int Force;

    private boolean touch;
    private boolean holded = false;
    private final int colorFill;
    private final int colorHoldText;
    private final String textHolded;
    private final String textHint;

    private Thread holdThread;
    private OnClickListener listener;
    private final Context mContext;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN: {
                touch = true;
                if (Strength >= 100 && !holded) {
                    if (listener != null) listener.onClick(this);
                    if (textHolded != null) {
                        setText(textHolded);
                    }
                    holded = true;
                }
                break;
            }
            default: {
                touch = false;
                if (!holded && textHint != null && Strength <= 30)
                    Toast.makeText(mContext, textHint, Toast.LENGTH_SHORT)
                            .show();
            }
        }
        return super.onTouchEvent(event);
    }

    Paint paint = new Paint();
    Paint paintText = new Paint();
    RectF rect = new RectF();
    RectF rectClip = new RectF();

    private float lastFill;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = (float) getWidth();
        float height = (float) getHeight();
        paintText.setColor(colorHoldText);
        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(getTextSize());
        paintText.setTextScaleX(getTextScaleX());
        paintText.setTextAlign(Paint.Align.CENTER);
        paint.setColor(colorFill);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        float fill = (Strength / 100f);
        rect.set(0, 0, width,
                height);
        rectClip.set(width * lastFill + ((fill - lastFill) * 16f), 0, width,
                height);
        float radius = Math.min(width, height) * 0.4f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(rectClip);
            canvas.drawRoundRect(rect, radius, radius, paint);
            if (textHolded != null) {
                canvas.drawText(textHolded.toUpperCase(getTextLocale()), width / 2f, height / 2f, paintText);
            }
        }
        lastFill = fill;
    }

    private void init() {
        if (holdThread == null && Strength < 100) {
            holdThread = new Thread(null, () -> {
                while (Strength < 100 && !holded) {
                    invalidate();
                    if (!touch && Strength > 0) Strength -= Force / 2;
                    if (touch || Strength < 0)
                        Strength += Force;
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Strength = 100;
            });
            holdThread.start();
        }
    }

    public void setHoldDownListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public HoldButton(@NonNull Context context) {
        this(context, null);
    }

    public HoldButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public HoldButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HoldButton);
        textHint = typedArray.getString(R.styleable.HoldButton_hint);
        textHolded = typedArray.getString(R.styleable.HoldButton_textHolded);
        Strength = typedArray.getInt(R.styleable.HoldButton_strength, 0);
        Force = typedArray.getInt(R.styleable.HoldButton_force, 2);
        colorHoldText = typedArray.getColor(R.styleable.HoldButton_textHoldedColor, Color.parseColor("#ffffff"));
        String colorFillString = typedArray.getString(R.styleable.HoldButton_fillColor);
        if (colorFillString != null) colorFill = Color.parseColor(colorFillString);
        else colorFill = Color.parseColor("#ff0000");
        typedArray.recycle();
        init();
    }


}
