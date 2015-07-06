package com.yac.yacapp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.yac.yacapp.R;
import com.yac.yacapp.utils.FontManager;

/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 */
public class RoundProgressBar4Arc extends View {
	private static float MaxAngle = 240;
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 圆环的颜色
	 */
	private int roundColor;

	/**
	 * 圆环进度的颜色
	 */
	private int roundProgressColor;

	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;

	/**
	 * 圆环的宽度
	 */
	private float roundWidth;

	/**
	 * 最大进度
	 */
	private int max;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textIsDisplayable;

	/**
	 * 进度的风格，实心或者空心
	 */
	private int style;

	/**
	 * 当前进度
	 */
	private int progress;
	private float sweepAngle;
	public static final int STROKE = 0;
	public static final int FILL = 1;

	public RoundProgressBar4Arc(Context context) {
		this(context, null);
	}

	public RoundProgressBar4Arc(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressBar4Arc(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
		textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
		textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		
		mTypedArray.recycle();
		initPaint(context);
	}

	private void initPaint(Context context) {
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		paint.setTextSize(textSize);
		paint.setTypeface(FontManager.getTypefaceByFontName(context, "FuturaStd-Condensed.ttf"));// 设置字体
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/**
		 * 画最外层的大圆环
		 */
		int centre = getWidth() / 2; // 获取圆心的x坐标
		int radius = (int) (centre - roundWidth / 2); // 圆环的半径
		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setStrokeCap(Paint.Cap.ROUND);
		RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
		canvas.drawArc(oval, 150, MaxAngle, false, paint); // 根据进度画圆弧
		
		/**
		 * 画进度百分比
		 */
		paint.setStrokeWidth(0);
		paint.setColor(roundProgressColor);
		float textWidth = paint.measureText(String.valueOf(progress)); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		if (textIsDisplayable && progress != 0 && style == STROKE) {
			canvas.drawText(String.valueOf(progress), centre - textWidth / 2, centre + textSize / 4, paint); // 画出进度百分比
		}
		
		/**
		 * 画圆弧 ，画圆环的进度
		 */
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setColor(roundProgressColor); // 设置进度的颜色
		canvas.drawArc(oval, 150, sweepAngle, false, paint); // 根据进度画圆弧
	}
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(float progress) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			sweepAngle =  240 * progress / max;
			this.progress = (int)progress;
			postInvalidate();
		}

	}
	
	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param sweepAngle
	 */
	public synchronized boolean setSweepAngle(float sweepAngle) {
		if (sweepAngle < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}else if (sweepAngle > MaxAngle) {
			this.sweepAngle = MaxAngle;
			return false;
		}else if (sweepAngle <= MaxAngle) {
			this.sweepAngle = sweepAngle;
			postInvalidate();
			return true;
		}
		return false;
	}

	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

}
