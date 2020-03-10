package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomePageView extends FrameLayout {

  int lastY;
  public boolean childTop;
  boolean foldTop;
  boolean isIntercept;
  private static final String TAG = "HomePageView";
  int imgHeight;
  ImageView img;
  MyScrollView scrollView;

  public HomePageView(@NonNull Context context) {
    super(context);
  }

  public HomePageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  @Override
  public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
    return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
  }

  private void init() {
    post(
        new Runnable() {
          @Override
          public void run() {
            img = (ImageView) getChildAt(0);
            scrollView = (MyScrollView) getChildAt(1);
            scrollView.setScrollListener(
                new ScrollListener() {
                  @Override
                  public void onScrollToBottom() {}

                  @Override
                  public void onScrollToTop() {
                    childTop = true;
                    log("TOP");
                  }

                  @Override
                  public void onScroll(int scrollY) {
                    if (scrollY != 0) {
                      childTop = false;
                    }
                    log("scrollY=" + scrollY);
                  }

                  @Override
                  public void notBottom() {}
                });
            imgHeight = img.getMeasuredHeight();
            Log.e(TAG, "imgHeight=" + imgHeight);
          }
        });
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    log("onMeasure");
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int childCount = getChildCount();
    int childTop = 0;
    for (int i = 0; i < childCount; i++) {
      View child = getChildAt(i);
      child.layout(l, childTop, r, childTop + child.getMeasuredHeight());
      childTop += child.getMeasuredHeight();
      log("child height=" + child.getMeasuredHeight());
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {

    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        isIntercept = false;
        lastY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        int dy = lastY - y;
        lastY = y;
        isIntercept = true;
        break;
    }
    return isIntercept;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int y = (int) event.getY();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        int dy = lastY - y;
        if (!foldTop) {
          scrollBy(0, dy);
        } else {
          //          MotionEvent e = MotionEvent.obtain(event);
          //          scrollView.dispatchTouchEvent(e);
        }
        lastY = y;
        break;
      case MotionEvent.ACTION_UP:
        break;
    }
    return super.onTouchEvent(event);
  }

  private void log(String log) {
    Log.e(TAG, "----" + log + "----");
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    log("t=" + t);
    foldTop = t >= imgHeight;
  }

  public boolean foldTop() {
    return getScrollY() >= imgHeight;
  }
}
