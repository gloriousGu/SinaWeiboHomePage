package com.gu.sinahomepage.view.horizontalscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gu.indicatorwidget.TabLayout;
import com.gu.sinahomepage.view.HomePageView;
import com.gu.sinahomepage.view.MyScrollView;

import java.lang.reflect.Field;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2020/3/19
 */
public class HomePageHorScrollView extends HorizontalScrollView {
  private static final String TAG = HomePageHorScrollView.class.getSimpleName();

  TabLayout mTabLayout;
  private boolean cancelSuperFling;
  private static final int START_FLING_SPEED = 500;
  int pageCurIndex;
  int pageSize;

  MyScrollView currentScrollView;

  public HomePageHorScrollView(@NonNull Context context) {
    super(context);
  }

  public HomePageHorScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    post(
        new Runnable() {
          @Override
          public void run() {
            HomePageView homePageView = (HomePageView) getParent().getParent();
            log("height= " + homePageView.getMeasuredHeight());
            int height = homePageView.getMeasuredHeight();
            int width = homePageView.getMeasuredWidth();

            initSize(HomePageHorScrollView.this, width, height);
            initScrollItemSize(width, height);
            updateCurrentScrollView(0);
          }
        });
  }

  private void initSize(View view, int width, int height) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
    params.height = height;
    params.width = width;
    view.setLayoutParams(params);
  }

  private void initScrollItemSize(int width, int height) {
    ViewGroup viewGroup = (ViewGroup) getChildAt(0);
    final int count = viewGroup.getChildCount();
    for (int i = 0; i < count; i++) {
      initSize(viewGroup.getChildAt(i), width, height);
    }
  }

  public boolean childScroll2Top() {
    return currentScrollView.getScrollY() == 0;
  }

  public void changeDraggingField(Boolean value) {
    currentScrollView.setField(value);
  }

  private void updateCurrentScrollView(int pos) {
    currentScrollView = (MyScrollView) ((ViewGroup) getChildAt(0)).getChildAt(pos);
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

  public void stopChildViewFling() {
    ViewGroup viewGroup = (ViewGroup) getChildAt(0);
    for (int i = 0; i < viewGroup.getChildCount(); i++) {
      ((MyScrollView) viewGroup.getChildAt(i)).stopFling();
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return super.dispatchTouchEvent(ev);
  }

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
        // 取消掉horizontal本身的fling,让fling结束时的停留位置在整数页（简单viewpager实现）
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
      updateCurrentScrollView(pageCurIndex);
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