package com.gu.sinahomepage.view.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageBtn extends AppCompatImageView {
  public SquareImageBtn(Context context) {
    super(context);
  }

  public SquareImageBtn(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int size = View.MeasureSpec.getSize(heightMeasureSpec);
    setMeasuredDimension(size, size);
  }
}
