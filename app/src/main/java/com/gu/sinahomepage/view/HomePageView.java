package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.R;

public class HomePageView extends NestedScrollView {

  int IMGHEIGHT;
  ImageView img;
  MyScrollView childScrollView;
  boolean childIsTop = true;

  public HomePageView(@NonNull Context context) {
    super(context);
  }

  public HomePageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    post(
        new Runnable() {
          @Override
          public void run() {
            img = findViewById(R.id.image);
            childScrollView = findViewById(R.id.scrollView);
            childScrollView.setScrollListener(
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
                });
            IMGHEIGHT = img.getMeasuredHeight();
            log("imgHeight=" + IMGHEIGHT);
            log("height= " + getMeasuredHeight());
            int height = getMeasuredHeight();
            LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) childScrollView.getLayoutParams();
            params.height = height;
            childScrollView.setLayoutParams(params);
          }
        });
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_UP:
        if (getStretchSize() > 0) {
          recoverImgSize();
        }
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  int lastY;

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    int y = (int) ev.getY();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        int dy = lastY - y;
        lastY = y;
        if (dy < 0 && childIsTop) {
          img2Stretch(dy, getScrollY());
        } else if (dy > 0 && getStretchSize() > 0) {
          img2Stretch(dy, getScrollY());
        }
        break;
      case MotionEvent.ACTION_UP:
        break;
    }
    return super.onTouchEvent(ev);
  }

  @Override
  public void onNestedPreScroll(
      @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
    super.onNestedPreScroll(target, dx, dy, consumed, type);
    if (dy < 0 && childIsTop) {
      // img need to stretch
      consumed[1] = img2Stretch(dy, getScrollY());
    } else if (dy > 0 && getStretchSize() > 0) {
      consumed[1] = img2Stretch(dy, getScrollY());
    } else if (dy > 0 && getStretchSize() == 0 && topVisible()) {
      int res = Math.min(getScrollY() + dy, IMGHEIGHT) - getScrollY();
      consumed[1] = res;
      scrollBy(0, res);
    } else if (dy > 0 && foldTop()) {
      //
    }
  }

  /**
   * @param dy dy<0
   * @param scrollY homePageView scroll y
   * @return stretch dy
   */
  private int img2Stretch(int dy, int scrollY) {

    if (dy == 0) return 0;
    else if (dy > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchImg(img, -res);
      return res;
    } else {
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res >= 0) {
          scrollBy(0, dy);
        } else {
          scrollTo(0, 0);
          stretchImg(img, -res);
        }
      } else if (scrollY == 0) {
        stretchImg(img, -dy);
      }
    }
    return dy;
  }

  private int getStretchSize() {
    return img.getHeight() - IMGHEIGHT;
  }

  private void stretchImg(ImageView img, int dy) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
    params.height += dy;
    img.setLayoutParams(params);
  }

  private void recoverImgSize() {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
    params.height = IMGHEIGHT;
    img.setLayoutParams(params);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
  }

  public boolean foldTop() {
    return getScrollY() >= IMGHEIGHT;
  }

  private boolean topVisible() {
    return getScrollY() < IMGHEIGHT;
  }

  public void log(String log) {
    Log.e("TAG", "----" + log + "----");
  }
}
