package com.gu.sinahomepage.view.bottom.horizontalscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContentFrameLayout extends FrameLayout {
  public ContentFrameLayout(@NonNull Context context) {
    super(context);
  }

  public ContentFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int childCount = getChildCount();
    // 父层处理过 传下来的是border width height
    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    for (int i = 0; i < childCount; i++) {
      View view = getChildAt(i);
      view.getLayoutParams().width = parentWidth;
      view.getLayoutParams().height = height;
      view.measure(
          MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
    // 扩大width=childCount * parentWidth
    setMeasuredDimension(childCount * parentWidth, height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    int count = getChildCount();
    for (int i = 0; i < count; i++) {
      View view = getChildAt(i);
      view.layout(i * view.getWidth(), 0, (i + 1) * view.getWidth(), view.getHeight());
    }
  }
}
