package com.gu.sinahomepage.view.horizontalscroll.content.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.widget.NestedScrollView;

import com.gu.sinahomepage.view.horizontalscroll.content.ScrollItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2020/3/19
 */
public class MyScrollView extends NestedScrollView implements ScrollItem {

  private Method stopFlingMethod;
  private static final String METHOD_NAME = "abortAnimatedScroll"; // 反射获取子类私有方法

  public MyScrollView(Context context) {
    super(context);
  }

  public MyScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    reflectionFlingMethod();
  }

  private void reflectionFlingMethod() {
    try {
      stopFlingMethod = getClass().getSuperclass().getDeclaredMethod(METHOD_NAME);
      stopFlingMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  /** 反射机制调用 NestedScrollView 的 abortAnimatedScroll() */
  @Override
  public void stopFling() {
    try {
      stopFlingMethod.invoke(this);
    } catch (IllegalAccessException | InvocationTargetException e) {
      Log.e("TAG", "-----反射异常 2!-----");
      e.printStackTrace();
    }
  }

  @Override
  public boolean isTop() {
    return getScrollY() == 0;
  }

  @Override
  public void scrollDy(int dy) {
    scrollBy(0, dy);
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
  public boolean onTouchEvent(MotionEvent ev) {
    return super.onTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return super.onInterceptTouchEvent(ev);
  }
}
