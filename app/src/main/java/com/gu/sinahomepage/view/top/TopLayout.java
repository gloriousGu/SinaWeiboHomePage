package com.gu.sinahomepage.view.top;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TopLayout extends FrameLayout implements TopView {
  int IMAGE_HEIGHT;

  public TopLayout(@NonNull Context context) {
    super(context);
  }

  public TopLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    post(
        new Runnable() {
          @Override
          public void run() {
            IMAGE_HEIGHT = getHeight();
            Log.e("TAG", "imgLayout height= " + IMAGE_HEIGHT);
          }
        });
  }

  @Override
  public void stretch(int dy) {
    layout(0, 0, getWidth(), getBottom() + dy);
  }

  @Override
  public void stretchRecover() {
    layout(0, 0, getWidth(), IMAGE_HEIGHT);
  }

  @Override
  public int getStretchSize() {
    return getHeight() - IMAGE_HEIGHT;
  }

  @Override
  public int getInitHeight() {
    return IMAGE_HEIGHT;
  }
}
