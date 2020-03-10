package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.widget.NestedScrollView;

public class MyScrollView extends NestedScrollView
    implements NestedScrollView.OnScrollChangeListener {

  int contentHeight;
  int scrollHeight;

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
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_MOVE:
        if (!parentFoldTop()) {
          getParent().requestDisallowInterceptTouchEvent(false);
        } else {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return super.onTouchEvent(ev);
  }

  @Override
  public void onScrollChange(
      NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
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
}
