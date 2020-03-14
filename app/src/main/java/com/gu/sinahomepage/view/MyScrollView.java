package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.core.widget.NestedScrollView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyScrollView extends NestedScrollView
    implements NestedScrollView.OnScrollChangeListener {

  int contentHeight;
  int scrollHeight;
  Method stopFlingMethod;
  private static final String METHOD_NAME = "abortAnimatedScroll"; // 反射获取子类私有方法

  public void setScrollListener(ScrollListener scrollListener) {
    this.mScrollListener = scrollListener;
  }

  private ScrollListener mScrollListener;

  public MyScrollView(Context context) {
    super(context);
  }

  public MyScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOnScrollChangeListener(this);
    try {
      stopFlingMethod = getClass().getSuperclass().getDeclaredMethod(METHOD_NAME);
      stopFlingMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  /*
  反射机制调用 NestedScrollView 的 abortAnimatedScroll()
   */
  public void stopFling() {
    try {
      stopFlingMethod.invoke(this);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  int lastX, lastY;

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastX = (int) ev.getRawX();
        lastY = (int) ev.getRawY();
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_MOVE:
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        if (Math.abs(lastX - x) > Math.abs(lastY - y) + 4) {
          getParent().getParent().requestDisallowInterceptTouchEvent(false);
        }
        lastX = x;
        lastY = y;
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public void onScrollChange(
      NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    if (mScrollListener == null) return;
    mScrollListener.onScroll(scrollY);

    if (scrollY + scrollHeight >= contentHeight || contentHeight <= scrollHeight) {
      mScrollListener.onScrollToBottom();
    } else {
      mScrollListener.notBottom();
    }

    if (scrollY == 0) {
      mScrollListener.onScrollToTop();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    contentHeight = getChildAt(0).getHeight();
    scrollHeight = getHeight();
  }

  private boolean parentFoldTop() {
    return ((HomePageView) getParent()).foldTop();
  }

  public void log(String log) {
    Log.e("SCROLL-TAG", "----" + log + "----");
  }
}
