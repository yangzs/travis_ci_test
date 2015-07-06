package com.yac.yacapp.utils;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;

public class FontManager {

	public static HashMap<String, Typeface> TypefaceMap = new HashMap<String, Typeface>();

	public static Typeface getTypefaceByFontName(Context context, String name) {
		if (TypefaceMap.containsKey(name)) {
			return TypefaceMap.get(name);
		} else {
			Typeface tf = Typeface.createFromAsset(context.getResources().getAssets(), name);
			TypefaceMap.put(name, tf);
			return tf;
		}
	}
}
