package com.gu.sinahomepage.view.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gu.sinahomepage.R;

public class TopAppBar extends FrameLayout implements AppBar {
  int foldSize;
  SquareImageBtn arrow;
  TextView nickname;

  public TopAppBar(Context context) {
    super(context);
  }

  public TopAppBar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    arrow = findViewById(R.id.arrow);
    nickname = findViewById(R.id.nickname);
  }

  @Override
  public void changeByScroll(int y) {
    setTranslationY(y);
    setTransparent(y != foldSize);
    arrow.setImageLevel(y == foldSize ? 1 : 0);
    nickname.setVisibility(y == foldSize ? VISIBLE : INVISIBLE);
  }

  @Override
  public void setFoldSize(int foldSize) {
    this.foldSize = foldSize;
  }

  @Override
  public void setTransparent(boolean transparent) {
    getBackground().setAlpha(transparent ? 0 : 255);
  }
}
