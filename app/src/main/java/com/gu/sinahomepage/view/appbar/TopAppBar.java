package com.gu.sinahomepage.view.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class TopAppBar extends FrameLayout implements AppBar {

  private AppBarListener listener;
  private boolean isFold;

  public TopAppBar(Context context) {
    super(context);
  }

  public TopAppBar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void setAppBarListener(AppBarListener listener) {
    this.listener = listener;
  }

  @Override
  public void translationY(int y) {
    setTranslationY(y);
  }

  @Override
  public void updateByStretch(int stretchSize, int refreshSize) {
    if (stretchSize == 0) {
      changePullState(false);
    } else if (stretchSize < refreshSize) {
      changePullState(true);
    } else {
      changeRefreshSize(refreshSize - stretchSize);
    }
  }

  @Override
  public void start2Refresh() {
    if (listener != null) listener.onAppBarStartRefreshing();
  }

  @Override
  public void stop2Refresh() {
    if (listener != null) listener.onAppBarStopRefreshing();
  }

  @Override
  public void changeFoldState(boolean isFold) {
    if (this.isFold == isFold) return;
    setTransparent(!isFold);
    this.isFold = isFold;
    if (listener != null) listener.onFoldStateChanged(isFold);
  }

  @Override
  public void setTransparent(boolean transparent) {
    getBackground().setAlpha(transparent ? 0 : 255);
  }

  private void changeRefreshSize(int refreshSize) {
    if (listener != null) listener.onAppBarChangePullSize(refreshSize);
  }

  private void changePullState(boolean start2Pull) {
    if (listener != null) listener.onAppBarChangePullState(start2Pull);
  }
}
