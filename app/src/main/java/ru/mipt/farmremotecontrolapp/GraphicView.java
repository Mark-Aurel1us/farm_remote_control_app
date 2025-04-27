package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

import ru.mipt.ru.mipt.farmremotecontrolapp.R;

/**
 * TODO: document your custom view class.
 */
public class GraphicView extends View {
    private String TAG = "GraphicView";
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public GraphicView(Context context) {
        super(context);
        init(null, 0);
    }

    public GraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GraphicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GraphicView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.GraphicView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.GraphicView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.GraphicView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.GraphicView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.GraphicView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        //invalidateTextPaintAndMeasurements();
    }
/*
    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        
        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            width = Math.min(widthSize,heightSize*2);
            height = width/2;
        }
        else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST) {
            //Must be this size
            width = widthSize;
            height = Math.min(width/2, heightSize);
        } else if (heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.AT_MOST) {
            //Must be this size
            height = heightSize;
            width = Math.min(height*2, widthSize);
        } else if (heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY) {
            //Can't be bigger than...
            height = heightSize;
            width = widthSize;
        } else {
            Log.d(TAG, "Invalid MeasureSpec mode");
            height = heightSize;
            width = widthSize;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        drawGraphic(canvas);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    private long[] timestamps = null;
    private double[] values = null;
    private String xLabel = null;
    private String yLabel = null;

    private void drawGraphic(Canvas canvas){
        int height = getHeight();
        int width = getWidth();
        if(isNull(xLabel) || isNull(yLabel) || isNull(timestamps) || isNull(values)){return;}
        //Log.d(TAG, "Drawing custom view:" + this.timestamps.toString() + ";" + this.values.toString() + ";" + this.xLabel + ";" + this.yLabel);

        canvas.drawColor(Color.WHITE);

        Paint paintAxis = new Paint();
        paintAxis.setColor(Color.BLACK);
        paintAxis.setStrokeWidth(5);

        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(30);

        Paint paintLine = new Paint();
        paintLine.setColor(Color.BLUE);
        paintLine.setStrokeWidth(3);

        int padding = 80;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;

        // Определение минимального и максимального значений
        double minValue = Float.MAX_VALUE, maxValue = Float.MIN_VALUE;
        for (double v : values){
            if (v < minValue) minValue = v;
            if (v > maxValue) maxValue = v;
        }

        // Рисуем оси
        int xAxisY = height - padding;
        int yAxisX = padding;
        canvas.drawLine(yAxisX, padding, yAxisX, xAxisY, paintAxis);
        canvas.drawLine(yAxisX, xAxisY, width - padding, xAxisY, paintAxis);

        // Подписи осей
        canvas.drawText(xLabel, width / 2f, height - 20, paintText);
        canvas.drawText(yLabel, 20, height / 2f, paintText);

        // Отображение значений по X (время)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        for (int i = 0; i < timestamps.length; i++) {
            float x = yAxisX + (timestamps[i] - timestamps[0]) * graphWidth / (timestamps[timestamps.length - 1] - timestamps[0]);
            String timeLabel = sdf.format(new Date(timestamps[i] * 1000));
            canvas.drawText(timeLabel, x, xAxisY + 40, paintText);
        }

        // Отображение значений по Y
        for (int i = 0; i < 5; i++) {
            double value = minValue + i * (maxValue - minValue) / 4;
            double y = xAxisY - (value - minValue) * graphHeight / (maxValue - minValue);
            canvas.drawText(String.format(Locale.US, "%.1f", value), 5, (float) y, paintText);
        }

        // Рисуем линии графика
        for (int i = 0; i < timestamps.length - 1; i++) {
            double x1 = yAxisX + (timestamps[i] - timestamps[0]) * graphWidth / (timestamps[timestamps.length - 1] - timestamps[0]);
            double y1 = xAxisY - (values[i] - minValue) * graphHeight / (maxValue - minValue);
            double x2 = yAxisX + (timestamps[i + 1] - timestamps[0]) * graphWidth / (timestamps[timestamps.length - 1] - timestamps[0]);
            double y2 = xAxisY - (values[i + 1] - minValue) * graphHeight / (maxValue - minValue);
            canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paintLine);
        }
    }

    public void setGraphicParams(long[] _timestamps, double[] _values, String _xLabel, String _yLabel){
        this.timestamps = _timestamps;
        this.values = _values;
        this.xLabel = _xLabel;
        this.yLabel = _yLabel;
        Log.d(TAG,"GraphicParams changed");
        this.invalidate();
    }
}