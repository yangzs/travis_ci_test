package com.yac.yacapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yac.yacapp.utils.FontManager;

public class TypefaceTextView extends TextView {

	public TypefaceTextView(Context context) {
		this(context, null);
	}
	
	public TypefaceTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(FontManager.getTypefaceByFontName(context, "FuturaStd-Condensed.ttf"));
	}


}
