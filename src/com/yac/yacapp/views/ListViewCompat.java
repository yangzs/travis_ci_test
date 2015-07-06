package com.yac.yacapp.views;

import com.yac.yacapp.activities.MyCarActivity.MessageItem;
import com.yac.yacapp.icounts.ICounts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewCompat extends ListView implements ICounts{

    private static final String TAG = "ListViewCompat";

    private SlideView mFocusedItemView;

    public ListViewCompat(Context context) {
        super(context);
    }

    public ListViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shrinkListItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SlideView) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int position = pointToPosition(x, y);
            if(debug)Log.i(TAG, "position=" + position);
            if (position != INVALID_POSITION) {
                MessageItem data = (MessageItem) getItemAtPosition(position);
                mFocusedItemView = data.slideView;
                if(debug)Log.i(TAG, "FocusedItemView=" + mFocusedItemView);
            }
        }
        	break;
        case MotionEvent.ACTION_MOVE:
        	
        	break;
        case MotionEvent.ACTION_UP:
        	break;
        default:
            break;
        }

        if (mFocusedItemView != null) {
            if(mFocusedItemView.onRequireTouchEvent(event)){
            	return true;
            }
        }

        return super.onTouchEvent(event);
    }

}
