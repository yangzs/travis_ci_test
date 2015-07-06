package com.yac.yacapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yac.yacapp.R;

public class PopupTextView extends RelativeLayout {
	
	private TextView mPopup_tv, mPopup_num_tv;
	
	public PopupTextView(Context context) {
		super(context);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_textview, this);
        mPopup_tv = (TextView) findViewById(R.id.popup_tv);
        mPopup_num_tv = (TextView) findViewById(R.id.popup_num_tv);
	}

	public PopupTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_textview, this);
        mPopup_tv = (TextView) findViewById(R.id.popup_tv);
        mPopup_num_tv = (TextView) findViewById(R.id.popup_num_tv);
	}

	public PopupTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_textview, this);
        mPopup_tv = (TextView) findViewById(R.id.popup_tv);
        mPopup_num_tv = (TextView) findViewById(R.id.popup_num_tv);
	}
	
	public void setTextColor(int color){
		mPopup_tv.setTextColor(color);
	}
	
	public void setTextSize(int size){
		mPopup_tv.setTextSize(size);
	}
	
	public void setText(CharSequence text){
		mPopup_tv.setText(text);
	}
	
	public void setTextNum(CharSequence text){
		mPopup_num_tv.setText(text);
	}
	
	public CharSequence getText(){
		return mPopup_tv.getText();
	}
	
	public CharSequence getTextNum(){
		return mPopup_num_tv.getText();
	}
	
	public void setTextNumVisibility(int code){
		mPopup_num_tv.setVisibility(code);
	}

	public TextView getTextView(){
		return mPopup_tv;
	}
	
	public TextView getTextNumView(){
		return mPopup_num_tv;
	}
	
}
