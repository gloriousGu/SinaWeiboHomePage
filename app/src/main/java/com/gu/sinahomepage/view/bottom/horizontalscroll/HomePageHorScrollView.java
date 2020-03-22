package com.gu.sinahomepage.view.bottom.horizontalscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gu.sinahomepage.view.HomePageView;
import com.gu.sinahomepage.view.content.ScrollItem;
import com.gu.sinahomepage.view.tab.Tab;

import java.lang.reflect.Field;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2020/3/19
 */
public class HomePageHorScrollView extends HorizontalScrollView implements ViewPager {
  private static final String TAG = HomePageHorScrollView.class.getSimpleName();

  Tab tab;
  private boolean cancelSuperFling;
  private static final int START_FLING_SPEED = 500;
  int pageCurIndex, destIndex;

  ScrollItem currentItem;

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
            updateCurrentChild(0);
          }
        });
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    View view = getChildAt(0);
    if (view != null) {
      // 先传给content measureSpec参数为父parent具体尺寸，然后在content的measure再根据该尺寸重新修改宽度
      view.getLayoutParams().width = width;
      view.getLayoutParams().height = height;
      view.measure(
          MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
  }

  @Override
  public boolean childScroll2Top() {
    return currentItem.isTop();
  }

  @Override
  public void scrollCurrentChildDy(int dy) {
    currentItem.scrollDy(dy);
  }

  @Override
  public boolean isScrolling() {
    return scrolling;
  }

  @Override
  public void bindTab(Tab tab) {
    this.tab = tab;
    tab.setViewPager(this);
  }

  @Override
  public void setCurrentItem(int to) {
    if (to == pageCurIndex) return;
    destIndex = to;
    smoothScrollTo(to * getWidth(), getScrollY());
  }

  @Override
  public int getCurrentItemPos() {
    return pageCurIndex;
  }

  @Override
  public void stopChildViewFling() {
    ViewGroup viewGroup = (ViewGroup) getChildAt(0);
    for (int i = 0; i < viewGroup.getChildCount(); i++) {
      ((ScrollItem) viewGroup.getChildAt(i)).stopFling();
    }
  }

  @Override
  public void moveBy(int dy) {
    setTranslationY(getMoveY() + dy);
  }

  @Override
  public void moveTo(int y) {
    setTranslationY(y);
  }

  @Override
  public int getMoveY() {
    return (int) getTranslationY();
  }

  @Override
  public void initViewSize(int width, int height) {
    getLayoutParams().height = height;
    measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
  }

  @Override
  public void initLocation(int l, int t, int r, int b) {
    layout(l, t, r, b);
  }

  private void updateCurrentChild(int pos) {
    currentItem = (ScrollItem) ((ViewGroup) getChildAt(0)).getChildAt(pos);
  }

  private void onScrollFinish() {
    tab.onPageScrolledFinish();
    updateCurrentChild(pageCurIndex);
    scrolling = false;
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

  boolean res;

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    super.onInterceptTouchEvent(ev);
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        res = true; // 重置res
        return false;
      case MotionEvent.ACTION_MOVE:
        int stretchSize = ((HomePageView) getParent().getParent()).getStretchSize();
        // 拉伸状态禁止 水平滚动
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
          scroll2Page(calcPageIndexBySpeed(speedY));
        } else {
          // 拖动抬手时 spring back
          scrollByActionUP();
        }
        return true;
    }
    return super.onTouchEvent(ev);
  }

  private int calcPageIndexBySpeed(int speedY) {
    return speedY < 0 ? (pageCurIndex + 1) : (pageCurIndex - 1);
  }

  private void scrollByActionUP() {
    final int scrollX = getScrollX();
    final int width = getWidth();
    int deltaX = scrollX - pageCurIndex * width;
    if (deltaX > 0 && deltaX > width / 3) {
      //      smoothScrollTo((pageCurIndex + 1) * width, getScrollY());
      scroll2Page(pageCurIndex + 1);
    } else if (deltaX > 0 && deltaX <= width / 3) {
      //      smoothScrollTo(pageCurIndex * width, getScrollY());
      scroll2Page(pageCurIndex);
    } else if (deltaX < 0 && deltaX < -width / 3) {
      //      smoothScrollTo((pageCurIndex - 1) * width, getScrollY());
      scroll2Page(pageCurIndex - 1);
    } else if (deltaX < 0 && deltaX >= -width / 3) {
      //      smoothScrollTo(pageCurIndex * width, getScrollY());
      scroll2Page(pageCurIndex);
    }
  }

  private void scroll2Page(int dest) {
    destIndex = dest;
    smoothScrollTo(dest * getWidth(), getScrollY());
  }

  @Override
  public void fling(int velocityX) {
    if (cancelSuperFling) return;
    super.fling(velocityX);
    log("start fling!");
  }

  private void log(String log) {
    Log.e(TAG, "----" + log + "----");
  }

  boolean scrolling;

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    int width = getWidth();
    if (l % width == 0) {
      pageCurIndex = l / width;
      if (pageCurIndex == destIndex) {
        onScrollFinish();
      }
    } else {
      scrolling = true;
      // 如果超过1页的滚动 不执行中间动画效果
      notifyTabScroll(l, width);
    }
  }

  /**
   * 通知tablayout执行动画
   *
   * @param scrollX scroll x
   * @param pageWidth page width
   */
  private void notifyTabScroll(int scrollX, int pageWidth) {
    if (tab.noAnim()) return;
    int deltaX = scrollX - pageCurIndex * pageWidth;
    if (deltaX > 0) {
      tab.onPageScrolled(pageCurIndex, pageCurIndex + 1, 1.0f * (scrollX % pageWidth) / pageWidth);
    } else {
      tab.onPageScrolled(pageCurIndex, pageCurIndex - 1, 1.0f * (scrollX % pageWidth) / pageWidth);
    }
  }
}
