package br.cin.ufpe.soundtracker.views;

import in.ubee.resources.ui.interfaces.InteractiveChildInterface;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

public class MeasurePointView extends View implements InteractiveChildInterface {

    private Set<Measure> mMeasures;

    private float mMeasureRadius = 20;
    private float mStrokeWidth = 5f;
    private float mTextSize = 10;
    private Rect mTextMeasureRect;

    private Paint mDefaultPaint;
    private Paint mSelectedPaint;
    private Paint mTextPaint;
    
    private Measure mSelectedMeasure;

    public MeasurePointView(Context context) {
        super(context);
        this.initialization(context);
    }

    private void initialization(final Context context) {

        mTextMeasureRect = new Rect();
        mMeasureRadius *= this.getResources().getDisplayMetrics().density;
        mStrokeWidth *= this.getResources().getDisplayMetrics().density;
        mTextSize *= this.getResources().getDisplayMetrics().density;

        mMeasures = new HashSet<Measure>();
        mDefaultPaint = new Paint();

        mDefaultPaint = new Paint();
        mDefaultPaint.setColor(Color.argb(123, 0, 0, 255));
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setShadowLayer(mMeasureRadius, 0, 0, Color.BLACK);
        
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        mSelectedPaint = new Paint();
        mSelectedPaint.setColor(Color.RED);
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Style.STROKE);
        mSelectedPaint.setStrokeCap(Paint.Cap.ROUND);
        mSelectedPaint.setStrokeJoin(Paint.Join.ROUND);
        mSelectedPaint.setStrokeWidth(mStrokeWidth);

        this.populate();
    }

    public void addMeasure(final Measure measure) {
        mMeasures.add(measure);
        mSelectedMeasure = measure;
    }

    private void populate() {
        for (int i = 0; i < 300; i += 50) {
            Measure measure = new Measure();
            measure.set(i, i);
            measure.value = i;
            this.addMeasure(measure);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (Measure measure : mMeasures) {

            canvas.drawCircle(measure.x, measure.y, mMeasureRadius, mDefaultPaint);
            
            if (mSelectedMeasure.equals(measure)) {
                canvas.drawCircle(measure.x, measure.y, mMeasureRadius, mSelectedPaint);
            }

            String value = measure.value + "";
            mTextPaint.getTextBounds(value, 0, value.length(), mTextMeasureRect);
            canvas.drawText(value, measure.x - mTextMeasureRect.centerX(), measure.y - mTextMeasureRect.centerY(), mTextPaint);
        }

    }
    
    @Override
    public float getXOffset() {
        return 0;
    }

    @Override
    public float getXOnMap() {
        return 0;
    }

    @Override
    public float getYOffset() {
        return 0;
    }

    @Override
    public float getYOnMap() {
        return 0;
    }

    @Override
    public boolean isScalable() {
        return true;
    }

}

class Measure extends PointF {
    float radius;
    float value;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Measure other = (Measure) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }

}
