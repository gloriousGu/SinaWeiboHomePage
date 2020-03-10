package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.R;

public class HomePageView extends NestedScrollView {

  int imgHeight;
  ImageView img;
  int stretchSize;
  MyScrollView childScrollView;
  boolean childIsTop = true;
  boolean stretchState;

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
                  public void onScroll(int scrollY) {}

                  @Override
                  public void notBottom() {}
                });
            imgHeight = img.getMeasuredHeight();
            log("imgHeight=" + imgHeight);
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
  public void onNestedPreScroll(
      @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
    super.onNestedPreScroll(target, dx, dy, consumed, type);
    if (dy < 0 && childIsTop) {
      // img need to stretch
      consumed[1] = img2Stretch(dy, getScrollY());
      stretchSize -= consumed[1];
      Log.e("TAG", "stretchSize= " + stretchSize);
    } else if (dy > 0 && stretchState) {
      consumed[1] = img2Stretch(dy, getScrollY());
      stretchSize -= consumed[1];
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
      int res = Math.min(stretchSize, dy);
      stretchImg(img, -res);
//      scrollBy(0, res);
      if (stretchSize == res) stretchState = false;
      return res;
    } else {
      if (scrollY > 0) {
        int res = scrollY + dy;
        if (res >= 0) {
          scrollBy(0, dy);
        } else {
          scrollTo(0, 0);
          stretchState = true;
          stretchImg(img, -res);
        }
      } else if (scrollY == 0) {
        stretchState = true;
        stretchImg(img, -dy);
      }
    }
    return dy;
  }

  private void stretchImg(ImageView img, int dy) {
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
    params.height += dy;
    img.setLayoutParams(params);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (foldTop()) log("TOP IS FOLD!");
    log("scrolly=" + oldt);
  }

  public boolean foldTop() {
    return getScrollY() >= imgHeight;
  }

  public void log(String log) {
    Log.e("TAG", "----" + log + "----");
  }
}
