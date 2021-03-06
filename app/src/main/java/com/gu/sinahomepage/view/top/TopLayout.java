package com.gu.sinahomepage.view.top;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TopLayout extends FrameLayout implements TopView {
  int height;

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
            height = getHeight();
          }
        });
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
  }

  @Override
  public void stretchBy(int dy) {
    layout(0, 0, getWidth(), getBottom() + dy);
  }

  @Override
  public void stretchRecoverBy(int dy) {
    layout(0, 0, getWidth(), height + dy);
  }

  @Override
  public int getStretchSize() {
    return getHeight() - height;
  }
}
