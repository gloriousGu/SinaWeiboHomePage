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

import java.lang.reflect.Field;

public class MyHorizontalScrollView extends HorizontalScrollView {
  private static final String TAG = MyHorizontalScrollView.class.getSimpleName();

  TabLayout mTabLayout;
  private boolean cancelSuperFling;
  private static final int START_FLING_SPEED = 500;
  int pageCurIndex, pageLastIndex;
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

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        break;
      case MotionEvent.ACTION_UP:
        cancelSuperFling = true;
        int speedY = getFlingSpeed();
        super.onTouchEvent(ev);
        cancelSuperFling = false;
        if (Math.abs(speedY) > START_FLING_SPEED) {
          log("start FLING!");
          smoothScrollTo(calcPageIndexBySpeed(), getScrollY());
        } else {
          scrollByActionUP();
        }
        // do self fling!
        return true;
    }
    return super.onTouchEvent(ev);
  }

  private int calcPageIndexBySpeed() {
    return pageCurIndex * getWidth();
  }

  // 滚动dis没有达到尺寸，回弹
  boolean springBack;

  private void scrollByActionUP() {
    final int scrollX = getScrollX();
    final int width = getWidth();
    int dis;
    if (pageCurIndex > pageLastIndex) {
      dis = scrollX % width;
      if (dis > width / 3 || scrollX == (pageSize - 1) * width)
        smoothScrollTo(pageCurIndex * width, getScrollY());
      else {
        springBack = true;
        smoothScrollTo(pageLastIndex * width, getScrollY());
      }
    } else if (pageCurIndex < pageLastIndex) {
      dis = width - scrollX % width;
      if (dis > width / 3) smoothScrollTo(pageCurIndex * width, getScrollY());
      else {
        springBack = true;
        smoothScrollTo(pageLastIndex * width, getScrollY());
      }
    }
    //
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
    Log.e("TAG", "----" + log + "----");
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    int deltaX = l - oldl;
    int width = getWidth();
    if (deltaX > 0) {
      if (l % width == 0) {
        springBack = false;
        changePageIndex(l / width);
        mTabLayout.selectPos(pageCurIndex);
      } else {
        if (!springBack) {
          changePageIndex(l / width + 1);
          mTabLayout.onPageScrolled(pageLastIndex, pageCurIndex, 1.0f * (l % width) / width);
        } else {
          changePageIndex(l / width + 1);
          float rate = 1.0f * (l % width) / width - 1f;
          //          log(
          //              "///////////////////tab pos = "
          //                  + mTabLayout.getPos()
          //                  + ",pageLastIndex= "
          //                  + pageLastIndex
          //                  + ",pageCurIndex= "
          //                  + pageCurIndex
          //                  + ",rate= "
          //                  + rate);
          mTabLayout.onPageScrolled(pageLastIndex, pageCurIndex, rate);
        }
      }
    } else if (deltaX < 0) {
      changePageIndex(l / width);
      if (l % width == 0) {
        springBack = false;
        mTabLayout.selectPos(pageCurIndex);
      } else {
        mTabLayout.onPageScrolled(
            pageLastIndex, pageCurIndex, (springBack ? 1 : 0) + 1.0f * (l % width) / width);
      }
    }
    log(
        "///////deltaX= "
            + deltaX
            + "////////////pageLastIndex= "
            + pageLastIndex
            + ",pageCurIndex= "
            + pageCurIndex);
  }

  private void changePageIndex(int cur) {
    if (cur != pageCurIndex) {
      pageLastIndex = pageCurIndex;
      pageCurIndex = cur;
    }
  }
}
