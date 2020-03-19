package com.gu.sinahomepage.view.horizontalscroll.content.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gu.sinahomepage.view.HomePageView;
import com.gu.sinahomepage.view.horizontalscroll.content.ScrollItem;

import java.lang.reflect.Field;

public class MyRecyclerView extends RecyclerView implements ScrollItem {
  public MyRecyclerView(@NonNull Context context) {
    super(context);
  }

  public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  int lastX, lastY;

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastX = (int) ev.getRawX();
        lastY = (int) ev.getRawY();
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_MOVE:
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        if (Math.abs(lastX - x) > Math.abs(lastY - y) + 4) {
          getParent().getParent().requestDisallowInterceptTouchEvent(false);
        }
        lastX = x;
        lastY = y;
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    return super.onTouchEvent(e);
  }

  private void log(String log) {
    Log.e("RecyclerView", "----" + log + "----");
  }

  @Override
  public void setField(Boolean res) {
    //    try {
    //      Field mScrollState = getClass().getSuperclass().getDeclaredField("mScrollState");
    //      mScrollState.setAccessible(true);
    //      if (!res) {
    //        mScrollState.set(this, SCROLL_STATE_IDLE);
    //      }
    //    } catch (NoSuchFieldException | IllegalAccessException e) {
    //      Log.e("TAG", "-----反射异常 1!-----");
    //      e.printStackTrace();
    //    }
  }

  @Override
  public void stopFling() {}

  @Override
  public boolean scroll2Top() {
    return getScrollYDistance() == 0;
  }

  private int getScrollYDistance() {
    LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
    int position = layoutManager.findFirstVisibleItemPosition();
    View firstVisibleChildView = layoutManager.findViewByPosition(position);
    int itemHeight = firstVisibleChildView.getHeight();
    return (position) * itemHeight - firstVisibleChildView.getTop();
  }
}
