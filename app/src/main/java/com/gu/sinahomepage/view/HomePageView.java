package com.gu.sinahomepage.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.R;
import com.gu.sinahomepage.view.appbar.AppBar;
import com.gu.sinahomepage.view.bottom.BottomView;
import com.gu.sinahomepage.view.bottom.horizontalscroll.ViewPager;
import com.gu.sinahomepage.view.top.TopView;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2020/3/19
 */
public class HomePageView extends NestedScrollView {

  private TopView topView;
  private BottomView bottomView;
  private AppBar appBar;
  private int FOLD_SIZE;
  private ValueAnimator animator;
  private static final float STRETCH_RATE = 0.5f; // 下拉阻尼系数
  private static final int PULL_TO_REFRESH_SIZE = 130;
  private boolean need2Refresh;
  private HomePageRefreshListener refreshListener;

  public HomePageView(@NonNull Context context) {
    super(context);
  }

  public HomePageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    post(
        new Runnable() {
          @Override
          public void run() {
            appBar.setTransparent(true);
          }
        });
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    topView = findViewById(R.id.imglayout);
    bottomView = findViewById(R.id.horizontalScrollView);
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
        if (animator != null && animator.isStarted()) {
          return false;
        }
        mActivePointerId = ev.getPointerId(0);
        lastX = (int) ev.getX();
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

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean res = super.onInterceptTouchEvent(ev);
    if (ev.getAction() == MotionEvent.ACTION_MOVE) {
      int x = (int) ev.getX();
      int y = (int) ev.getY();
      int stretchSize = getStretchSize();
      if (Math.abs(lastX - x) > Math.abs(lastY - y) && stretchSize == 0) {
        // 优先水平滚动
        res = false;
      }
      lastX = x;
      lastY = y;
    }
    return res;
  }

  int lastX, lastY;
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
          stretchBy(-res);
        }
      } else if (scrollY == 0) {
        stretchBy(-dy);
      }
    } else if (dy < 0 && !bottomView.childScroll2Top()) {
      final int scrollY = getScrollY();
      if (scrollY == 0) {
        bottomView.scrollCurrentChildDy(dy);
      }
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchBy(-res);
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
            stretchBy(-res);
          }
          scrollTo(0, 0);
        } else {
          scrollTo(0, 0);
        }
      } else if (scrollY == 0) {
        if (type == ViewCompat.TYPE_TOUCH) {
          // 拉伸stretch
          ((ViewPager) bottomView).getCurrentItem().setField(true);
          stretchBy(-dy);
        }
      }
      return dy;
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchBy(-res);
      return res;
    } else if (dy > 0 && getStretchSize() == 0 && topVisible()) {
      int res = Math.min(getScrollY() + dy, FOLD_SIZE) - getScrollY();
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

  private boolean topVisible() {
    return getScrollY() < FOLD_SIZE;
  }

  public int getStretchSize() {
    return topView.getStretchSize();
  }

  /**
   * 拉伸并移动horizontalScrollView
   *
   * @param dy deltaY
   */
  private void stretchBy(int dy) {
    if (dy > 0) {
      dy = (int) (dy * STRETCH_RATE);
      if (dy == 0) return;
    }
    topView.stretchBy(dy);
    bottomView.moveBy(dy);
    appBar.updateByStretch(getStretchSize(), PULL_TO_REFRESH_SIZE);
    need2Refresh = getStretchSize() >= PULL_TO_REFRESH_SIZE;
  }

  private void stretchRecoverBy(int y) {
    topView.stretchRecoverBy(y);
    bottomView.moveTo(y);
    appBar.updateByStretch(y, PULL_TO_REFRESH_SIZE);
  }

  /** 还原拉伸 */
  private void recoverImgSize() {
    if (animator == null) {
      animator = ValueAnimator.ofInt(getStretchSize(), 0).setDuration(200);
      animator.setInterpolator(new DecelerateInterpolator());
      animator.addUpdateListener(listener);
      animator.addListener(al);
    } else {
      animator.setIntValues(getStretchSize(), 0);
    }
    animator.start();
  }

  ValueAnimator.AnimatorUpdateListener listener =
      new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          Integer value = (Integer) animation.getAnimatedValue();
          stretchRecoverBy(value);
        }
      };

  ValueAnimator.AnimatorListener al =
      new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {}

        @Override
        public void onAnimationEnd(Animator animation) {
          /*回弹动画结束后 进行刷新操作！*/
          if (need2Refresh && refreshListener != null) {
            refreshListener.onStartRefresh();
            appBar.start2Refresh();
          }
        }

        @Override
        public void onAnimationCancel(Animator animation) {}

        @Override
        public void onAnimationRepeat(Animator animation) {}
      };

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    release();
  }

  private void release() {
    if (animator != null) {
      animator.cancel();
    }
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    /*
     *appBar随着滚动 始终悬浮在顶部
     */
    appBar.translationY(t);
    appBar.changeFoldState(t == FOLD_SIZE);
  }

  public void log(String log) {
    Log.e("TAG", "----" + log + "----");
  }

  public void setFoldSize(int size) {
    FOLD_SIZE = size;
  }

  /** 外部调用停止刷新 */
  public void stopRefresh() {
    need2Refresh = false;
    appBar.stop2Refresh();
    if (refreshListener != null) {
      refreshListener.onStopRefresh();
    }
  }

  public void setRefreshListener(HomePageRefreshListener listener) {
    this.refreshListener = listener;
  }

  public interface HomePageRefreshListener {
    void onStartRefresh();

    void onStopRefresh();
  }
}
