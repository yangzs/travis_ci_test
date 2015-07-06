package com.yac.yacapp.views;

import com.yac.yacapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RoundProgressImageView extends View {

	private Paint mPaint;
	private Context context;
	private Bitmap bitmap;
	
	public RoundProgressImageView(Context context) {
		this(context, null);
	}

	public RoundProgressImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_big_gradient_ring);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();  
		mPaint.setColor(Color.BLUE);  
		canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), mPaint);  
		canvas.restore();  
        
		canvas.save();  
		canvas.drawBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), mPaint);  
		canvas.restore(); 
	}
	

}
