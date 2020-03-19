package com.gu.sinahomepage.view.horizontalscroll.content.impl;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class MyLinearLayoutManager extends LinearLayoutManager {
  public MyLinearLayoutManager(Context context) {
    super(context);
  }

  public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  @Override
  public boolean canScrollVertically() {
    return canScroll && super.canScrollVertically();
  }

  boolean canScroll = true;

  public void setCanScroll(boolean canScroll) {
    this.canScroll = canScroll;
  }
}
