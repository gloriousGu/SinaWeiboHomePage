package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.R;
import com.gu.sinahomepage.view.horizontalscroll.MyHorizontalScrollView;

public class HomePageView extends NestedScrollView {

  int IMAGE_HEIGHT;
  FrameLayout imgLayout;
  MyScrollView childScrollView1, childScrollView2, childScrollView3;
  MyHorizontalScrollView horizontalScrollView;

  public void setChildIsTop(boolean childIsTop) {
    this.childIsTop = childIsTop;
  }

  boolean childIsTop = true;

  public HomePageView(@NonNull Context context) {
    super(context);
  }

  public HomePageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  ScrollListener scrollListener =
      new ScrollListener() {
        @Override
        public void onScrollToBottom() {}

        @Override
        public void onScrollToTop() {
          childIsTop = true;
        }

        @Override
        public void onScroll(int scrollY) {
          if (scrollY != 0) childIsTop = false;
        }

        @Override
        public void notBottom() {}
      };

  private void init() {
    post(
        new Runnable() {
          @Override
          public void run() {
            horizontalScrollView = findViewById(R.id.horizontalScrollView);
            imgLayout = findViewById(R.id.imglayout);
            childScrollView1 = findViewById(R.id.scrollView1);
            childScrollView1.setScrollListener(scrollListener);
            childScrollView2 = findViewById(R.id.scrollView2);
            childScrollView2.setScrollListener(scrollListener);
            childScrollView3 = findViewById(R.id.scrollView3);
            childScrollView3.setScrollListener(scrollListener);
            IMAGE_HEIGHT = imgLayout.getMeasuredHeight();
            log("imgHeight=" + IMAGE_HEIGHT);
            log("height= " + getMeasuredHeight());
            int height = getMeasuredHeight();
            int width = getMeasuredWidth();

            initSize(horizontalScrollView, width, height);
            initSize(childScrollView1, width, height);
            initSize(childScrollView2, width, height);
            initSize(childScrollView3, width, height);
          }
        });
  }

  private void initSize(View view, int width, int height) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
    params.height = height;
    params.width = width;
    view.setLayoutParams(params);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = ev.getPointerId(0);
        lastY = (int) ev.getY();
        // 防抖动
        childScrollView1.stopFling();
        childScrollView2.stopFling();
        childScrollView3.stopFling();

        break;
      case MotionEvent.ACTION_UP:
        if (getStretchSize() > 0) {
          recoverImgSize();
        }
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  int lastY;
  int mActivePointerId;

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        break;
      case MotionEvent.ACTION_MOVE:
        final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
        if (activePointerIndex == -1) {
          log("HomePageView >>> Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
          break;
        }
        int dy = lastY - y;
        lastY = y;
        touchInImage(dy);
        break;
    }
    return super.onTouchEvent(ev);
  }

  @Override
  public boolean dispatchNestedPreScroll(
      int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
    // image缩小时 停止页面的滚动 处理成外层消耗dy
    if (dy > 0 && getStretchSize() > 0) {
      consumed[1] = Math.min(getStretchSize(), dy);
      return true;
    }
    return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
  }

  @Override
  public void onNestedPreScroll(
      @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
    super.onNestedPreScroll(target, dx, dy, consumed, type);
    consumed[1] = deltaYConsume(dy, type);
  }

  private void touchInImage(int dy) {
    if (dy < 0 && childIsTop) {
      final int scrollY = getScrollY();
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res < 0) {
          stretchImg(imgLayout, -res);
        }
      } else if (scrollY == 0) {
        stretchImg(imgLayout, -dy);
      }
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchImg(imgLayout, -res);
    }
  }

  private int deltaYConsume(int dy, int type) {
    if (dy < 0 && childIsTop) {
      // img need to stretch
      final int scrollY = getScrollY();
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res > 0) {
          scrollBy(0, dy);
        } else if (res < 0) {
          if (type == ViewCompat.TYPE_TOUCH) {
            stretchImg(imgLayout, -res);
          }
          scrollTo(0, 0);
        } else {
          scrollTo(0, 0);
        }
      } else if (scrollY == 0) {
        if (type == ViewCompat.TYPE_TOUCH) stretchImg(imgLayout, -dy);
      }
      return dy;
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchImg(imgLayout, -res);
      return res;
    } else if (dy > 0 && getStretchSize() == 0 && topVisible()) {
      int res = Math.min(getScrollY() + dy, IMAGE_HEIGHT) - getScrollY();
      scrollBy(0, res);
      return res;
    } else if (dy > 0 && foldTop()) {
      //
    }
    return 0;
  }

  private int getStretchSize() {
    return imgLayout.getHeight() - IMAGE_HEIGHT;
  }

  private void stretchImg(View img, int dy) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
    params.height += dy;
    img.setLayoutParams(params);
  }

  private void recoverImgSize() {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgLayout.getLayoutParams();
    params.height = IMAGE_HEIGHT;
    imgLayout.setLayoutParams(params);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
  }

  public boolean foldTop() {
    return getScrollY() >= IMAGE_HEIGHT;
  }

  private boolean topVisible() {
    return getScrollY() < IMAGE_HEIGHT;
  }

  public void log(String log) {
    Log.e("TAG", "----" + log + "----");
  }
}
