package br.cin.ufpe.soundtracker.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class SoundMapView extends View {

	private float mPointRadius = 5;
	private float mStrokeWidth = 1f;

	private Paint mPointPaint;
	private Paint mSelectedPointPaint;

	private List<SoundMeasure> mMeasures;

	public SoundMapView(Context context) {
		super(context);
		this.initialization();
	}

	private void initialization() {

		mMeasures = new ArrayList<SoundMeasure>();
		
		for (int i = 0; i < 30; i++) {
			SoundMeasure measure = new SoundMeasure();
			measure.x = 10 * i;
			measure.y = 10 * i;
		 	mMeasures.add(measure);
		}

		mPointRadius *= this.getResources().getDisplayMetrics().density;
		mStrokeWidth *= this.getResources().getDisplayMetrics().density;

		mPointPaint = new Paint();
		mPointPaint.setColor(Color.BLUE);
		mPointPaint.setAntiAlias(true);
		mPointPaint.setShadowLayer(mPointRadius, 0, 0, Color.BLACK);

		mSelectedPointPaint = new Paint();
		mSelectedPointPaint.setColor(Color.rgb(102, 102, 102));
		mSelectedPointPaint.setAntiAlias(true);
		mSelectedPointPaint.setStyle(Style.STROKE);
		mSelectedPointPaint.setStrokeCap(Paint.Cap.ROUND);
		mSelectedPointPaint.setStrokeJoin(Paint.Join.ROUND);
		mSelectedPointPaint.setStrokeWidth(mStrokeWidth);

	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);

		// float radius = mMatrix.mapRadius(RADIUS_POINT);

		float radius = 5;

		float one = getMatrix().mapRadius(1f);
		float maxZoom = 1.5f;
		float strokeSize = 1;

		if (one > maxZoom) {
			radius = radius * maxZoom / one;
			strokeSize = strokeSize * maxZoom / one;
		}

		for (SoundMeasure measure : mMeasures) {
			canvas.drawCircle(measure.x, measure.y, radius, mPointPaint);
		}
	}

	// @Override
	// public float getScale() {
	// return 1;
	// }

	// @Override
	// public boolean setScale(final float scale) {
	// // this.matrix.setScale(scale, scale);
	// // this.scale = scale;
	// // this.invalidate();
	// return false;
	// }

}

class SoundMeasure {
	float x;
	float y;
	float z;
	float measure;
}