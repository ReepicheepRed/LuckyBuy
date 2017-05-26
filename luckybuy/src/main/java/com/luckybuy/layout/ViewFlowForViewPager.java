package com.luckybuy.layout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhiPeng.S on 2016/7/4.
 */
public class ViewFlowForViewPager extends ViewFlow {
        private ViewPager mPager;

        public ViewFlowForViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        public void setViewPager(ViewPager viewPager) {
            mPager = viewPager;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (mPager != null)
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPager.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        mPager.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mPager.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPager.requestDisallowInterceptTouchEvent(true);
                        break;
                }
            return super.onInterceptTouchEvent(ev);
        }
}
