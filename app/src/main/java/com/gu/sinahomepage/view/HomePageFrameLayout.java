package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gu.sinahomepage.R;
import com.gu.sinahomepage.view.appbar.AppBar;
import com.gu.sinahomepage.view.bottom.BottomView;
import com.gu.sinahomepage.view.tab.Tab;
import com.gu.sinahomepage.view.top.TopView;

public class HomePageFrameLayout extends FrameLayout {
  private static final String TAG = HomePageFrameLayout.class.getSimpleName();
  Tab tab;
  TopView imgLayout;
  BottomView bottomView; // HomePageHorScrollView
  AppBar appBar;
  int FOLD_SIZE;

  public HomePageFrameLayout(@NonNull Context context) {
    super(context);
  }

  public HomePageFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    tab = findViewById(R.id.tab);
    appBar = findViewById(R.id.appBar);
    imgLayout = findViewById(R.id.imglayout);
    bottomView = findViewById(R.id.horizontalScrollView);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    FOLD_SIZE =
        imgLayout.getMeasuredHeight() - tab.getMeasuredHeight() - appBar.getMeasuredHeight();
    ((HomePageView) getParent()).setFoldSize(FOLD_SIZE);
    int height = parentHeight + FOLD_SIZE;
    bottomView.initViewSize(
        MeasureSpec.getSize(widthMeasureSpec),
        parentHeight - tab.getMeasuredHeight() - appBar.getMeasuredHeight());
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    bottomView.initLocation(0, imgLayout.getMeasuredHeight(), right, getMeasuredHeight());
  }

  private void log(String log) {
    Log.e(TAG, "----" + log + "----");
  }
}
