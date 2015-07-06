package com.yac.yacapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class ToggleRadioButton extends RadioButton {

	public ToggleRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ToggleRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ToggleRadioButton(Context context) {
		super(context);
	}

	@Override
	public void toggle() {
//		if (isChecked()) {
//			setChecked(false);
//		} else {
//			setChecked(true);
//		}
//		 super.toggle();
		setChecked(!isChecked());
	}
	
    @Override
    public boolean performClick() {
        /*
         * XXX: These are tiny, need some surrounding 'expanded touch area',
         * which will need to be implemented in Button if we only override
         * performClick()
         */

        /* When clicked, toggle the state */
        toggle();
        return true;
//        return super.performClick();
    }

}
