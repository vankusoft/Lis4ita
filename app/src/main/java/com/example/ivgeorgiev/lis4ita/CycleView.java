package com.example.ivgeorgiev.lis4ita;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

public class CycleView extends HorizontalInfiniteCycleViewPager {

    public CycleView(Context context) {
        super(context);
    }

    public CycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
