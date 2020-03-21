package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.R;
import com.gu.sinahomepage.view.bottom.BottomView;
import com.gu.sinahomepage.view.top.TopView;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2020/3/19
 */
public class HomePageView extends NestedScrollView {

  TopView topView;
  BottomView bottomView;
  HomePageFrameLayout contentLayout;
  LinearLayout appBar;

  public HomePageView(@NonNull Context context) {
    super(context);
  }

  public HomePageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    topView = findViewById(R.id.imglayout);
    bottomView = findViewById(R.id.horizontalScrollView);
    contentLayout = findViewById(R.id.home_page_view_content);
    appBar = findViewById(R.id.appBar);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    log("HomePageView:height= " + height);
    View v = getChildAt(0);
    v.getLayoutParams().height = height;
    v.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = ev.getPointerId(0);
        lastY = (int) ev.getY();
        // 防抖动：如果down事件发生时child的scroller未结束，会产生抖动现象，反射方式scroller强制finish
        bottomView.stopChildViewFling();
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
    if (dy < 0 && canStretch()) {
      final int scrollY = getScrollY();
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res < 0) {
          stretch(-res);
        }
      } else if (scrollY == 0) {
        stretch(-dy);
      }
    } else if (dy < 0 && !bottomView.childScroll2Top()) {
      final int scrollY = getScrollY();
      if (scrollY == 0) {
        bottomView.scrollCurrentChildDy(dy);
      }
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretch(-res);
    }
  }

  private int deltaYConsume(int dy, int type) {
    if (dy < 0 && canStretch()) {
      // img need to stretch
      final int scrollY = getScrollY();
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res > 0) {
          scrollBy(0, dy);
        } else if (res < 0) {
          if (type == ViewCompat.TYPE_TOUCH) {
            stretch(-res);
          }
          scrollTo(0, 0);
        } else {
          scrollTo(0, 0);
        }
      } else if (scrollY == 0) {
        if (type == ViewCompat.TYPE_TOUCH) {
          // 拉伸stretch
          stretch(-dy);
        }
      }
      return dy;
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretch(-res);
      return res;
    } else if (dy > 0 && getStretchSize() == 0 && topVisible()) {
      int res = Math.min(getScrollY() + dy, getFoldHeight()) - getScrollY();
      scrollBy(0, res);
      return res;
    }
    return 0;
  }

  /**
   * 是否可以拉伸
   *
   * @return res
   */
  private boolean canStretch() {
    return bottomView.childScroll2Top() && !bottomView.isScrolling();
  }

  private int getFoldHeight() {
    return contentLayout.getFoldHeight();
  }

  private boolean topVisible() {
    return getScrollY() < getFoldHeight();
  }

  public boolean topInvisible() {
    return getScrollY() >= getFoldHeight();
  }

  public int getStretchSize() {
    return topView.getStretchSize();
  }

  /**
   * 拉伸并移动horizontalScrollView
   *
   * @param dy deltaY
   */
  private void stretch(int dy) {
    topView.stretch(dy);
    bottomView.moveBy(dy);
  }

  /** 还原拉伸 */
  private void recoverImgSize() {
    topView.stretchRecover();
    bottomView.moveTo(0);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    appBar.setTranslationY(t);
  }

  public void log(String log) {
    Log.e("TAG", "----" + log + "----");
  }
}
