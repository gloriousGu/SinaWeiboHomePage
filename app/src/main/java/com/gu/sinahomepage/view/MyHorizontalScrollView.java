package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyHorizontalScrollView extends HorizontalScrollView {
  private static final String TAG = MyHorizontalScrollView.class.getSimpleName();

  public MyHorizontalScrollView(@NonNull Context context) {
    super(context);
  }

  public MyHorizontalScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  private void log(String log) {
    Log.e(TAG, "----" + log + "----");
  }
}
