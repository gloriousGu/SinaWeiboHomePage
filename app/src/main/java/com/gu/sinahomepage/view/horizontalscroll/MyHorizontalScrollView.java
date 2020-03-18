package com.gu.sinahomepage.view.horizontalscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gu.indicatorwidget.TabLayout;
import com.gu.sinahomepage.view.HomePageView;

import java.lang.reflect.Field;

public class MyHorizontalScrollView extends HorizontalScrollView {
  private static final String TAG = MyHorizontalScrollView.class.getSimpleName();

  TabLayout mTabLayout;
  private boolean cancelSuperFling;
  private static final int START_FLING_SPEED = 500;
  int pageCurIndex;
  int pageSize;

  public MyHorizontalScrollView(@NonNull Context context) {
    super(context);
  }

  public MyHorizontalScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public void bindTabLayout(TabLayout tabLayout) {
    this.mTabLayout = tabLayout;
  }

  private int getFlingSpeed() {
    try {
      Field velocityTrackerField = getClass().getSuperclass().getDeclaredField("mVelocityTracker");
      velocityTrackerField.setAccessible(true);
      VelocityTracker mVelocityTracker = (VelocityTracker) velocityTrackerField.get(this);
      mVelocityTracker.computeCurrentVelocity(1000, 8000);
      return (int) mVelocityTracker.getXVelocity();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return super.dispatchTouchEvent(ev);
  }

  /** 备注 */
  boolean res;

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    super.onInterceptTouchEvent(ev);
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        res = true;
        return false;
      case MotionEvent.ACTION_MOVE:
        int stretchSize = ((HomePageView) getParent().getParent()).getStretchSize();
        if (stretchSize > 0) {
          res = false;
        }
    }
    return res; // cation!
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        break;
      case MotionEvent.ACTION_UP:
        //取消掉horizontal本身的fling,让fling结束时的位置在整数页（简单viewpager实现）
        cancelSuperFling = true;
        int speedY = getFlingSpeed();
        super.onTouchEvent(ev);
        cancelSuperFling = false;
        if (Math.abs(speedY) > START_FLING_SPEED) {
          // do self fling!
          smoothScrollTo(calcPageIndexBySpeed(speedY), getScrollY());
        } else {
          // 拖动抬手时 spring back
          scrollByActionUP();
        }
        return true;
    }
    return super.onTouchEvent(ev);
  }

  private int calcPageIndexBySpeed(int speedY) {
    return speedY < 0 ? (pageCurIndex + 1) * getWidth() : (pageCurIndex - 1) * getWidth();
  }

  private void scrollByActionUP() {
    final int scrollX = getScrollX();
    final int width = getWidth();
    int deltaX = scrollX - pageCurIndex * width;
    if (deltaX > 0 && deltaX > width / 3) {
      smoothScrollTo((pageCurIndex + 1) * width, getScrollY());
    } else if (deltaX > 0 && deltaX <= width / 3) {
      smoothScrollTo(pageCurIndex * width, getScrollY());
    } else if (deltaX < 0 && deltaX < -width / 3) {
      smoothScrollTo((pageCurIndex - 1) * width, getScrollY());
    } else if (deltaX < 0 && deltaX >= -width / 3) {
      smoothScrollTo(pageCurIndex * width, getScrollY());
    }
  }

  @Override
  public void fling(int velocityX) {
    if (cancelSuperFling) return;
    super.fling(velocityX);
    log("start fling!");
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    pageSize = getChildAt(0).getMeasuredWidth() / getMeasuredWidth();
  }

  private void log(String log) {
    Log.e(TAG, "----" + log + "----");
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    int width = getWidth();
    if (l % width == 0) {
      pageCurIndex = l / width;
      mTabLayout.selectPos(pageCurIndex);
    } else {
      int deltaX = l - pageCurIndex * width;
      if (deltaX > 0) {
        mTabLayout.onPageScrolled(pageCurIndex, pageCurIndex + 1, 1.0f * (l % width) / width);
      } else {
        mTabLayout.onPageScrolled(pageCurIndex, pageCurIndex - 1, 1.0f * (l % width) / width);
      }
    }
  }
}
