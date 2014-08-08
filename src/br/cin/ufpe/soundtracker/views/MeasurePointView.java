package br.cin.ufpe.soundtracker.views;

import in.ubee.resources.ui.interfaces.InteractiveChildInterface;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;
import br.cin.ufpe.soundtracker.microphone.SoundMeasure;

public class MeasurePointView extends View implements InteractiveChildInterface {

    private Set<SoundMeasure> mMeasures;

    private float mMeasureRadius = SoundMeasure.RADIUS;
    private float mStrokeWidth = 5f;
    private float mTextSize = 10;
    private Rect mTextMeasureRect;

    private Paint mDefaultPaint;
    private Paint mSelectedPaint;
    private Paint mTextPaint;
    
    private SoundMeasure mSelectedMeasure;

    public MeasurePointView(Context context) {
        super(context);
        this.initialization(context);
    }

    private void initialization(final Context context) {

        mTextMeasureRect = new Rect();
        mMeasureRadius *= this.getResources().getDisplayMetrics().density;
        mStrokeWidth *= this.getResources().getDisplayMetrics().density;
        mTextSize *= this.getResources().getDisplayMetrics().density;

        mMeasures = new HashSet<SoundMeasure>();
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

    }

    public void addMeasure(final SoundMeasure measure) {
        
        boolean found = false;
        
        for (SoundMeasure oldMeasure : mMeasures) {
            if (oldMeasure.equals(measure)) {
                oldMeasure.value = measure.value;
                found = true;
            }
        }
        
        if (!found) {
            mMeasures.add(measure);
        }
        
        mSelectedMeasure = measure;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (SoundMeasure measure : mMeasures) {

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


