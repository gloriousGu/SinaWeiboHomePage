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
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.R;

public class HomePageView extends NestedScrollView {

  int IMAGE_HEIGHT;
  ImageView img;
  MyScrollView childScrollView;
  boolean childIsTop = true;
  boolean cancelScroll;

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
            IMAGE_HEIGHT = img.getMeasuredHeight();
            log("imgHeight=" + IMAGE_HEIGHT);
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
      case MotionEvent.ACTION_DOWN:
        //防抖动
        childScrollView.stopFling();
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
        touchInImage(dy);
        break;
      case MotionEvent.ACTION_UP:
        break;
    }
    return super.onTouchEvent(ev);
  }

  @Override
  public boolean dispatchNestedPreScroll(
      int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
    // 停止image恢复时页面滚动 处理成外层消耗dy
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
    //    if (type == ViewCompat.TYPE_TOUCH) {
    consumed[1] = deltaYConsume(dy, type);
    //    }
  }

  @Override
  public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return super.dispatchNestedPreFling(velocityX, velocityY);
  }
  //
  //  @Override
  //  public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
  //    return super.onNestedPreFling(target, velocityX, velocityY);
  //  }

  private void touchInImage(int dy) {
    if (dy < 0 && childIsTop) {
      final int scrollY = getScrollY();
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res < 0) {
          stretchImg(img, -res);
        }
      } else if (scrollY == 0) {
        stretchImg(img, -dy);
      }
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchImg(img, -res);
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
            stretchImg(img, -res);
          }
          scrollTo(0, 0);
        } else {
          scrollTo(0, 0);
        }
      } else if (scrollY == 0) {
        if (type == ViewCompat.TYPE_TOUCH) stretchImg(img, -dy);
      }
      return dy;
    } else if (dy > 0 && getStretchSize() > 0) {
      int res = Math.min(getStretchSize(), dy);
      stretchImg(img, -res);
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
    return img.getHeight() - IMAGE_HEIGHT;
  }

  private void stretchImg(ImageView img, int dy) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
    params.height += dy;
    img.setLayoutParams(params);
  }

  private void recoverImgSize() {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
    params.height = IMAGE_HEIGHT;
    img.setLayoutParams(params);
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
