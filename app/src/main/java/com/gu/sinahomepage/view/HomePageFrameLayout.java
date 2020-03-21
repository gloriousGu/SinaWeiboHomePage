package com.gu.sinahomepage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gu.sinahomepage.R;
import com.gu.sinahomepage.view.horizontalscroll.HomePageHorScrollView;
import com.gu.sinahomepage.view.tab.TabLayout;
import com.gu.sinahomepage.view.top.TopLayout;

public class HomePageFrameLayout extends FrameLayout {
  private static final String TAG = HomePageFrameLayout.class.getSimpleName();
  TabLayout tab;
  TopLayout imgLayout;
  HomePageHorScrollView homePageHorScrollView;
  LinearLayout appBar;
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
    homePageHorScrollView = findViewById(R.id.horizontalScrollView);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
    log(
        "parentHeight= "
            + parentHeight
            + ",imgLayout height= "
            + imgLayout.getMeasuredHeight()
            + ",tab height= "
            + tab.getMeasuredHeight()
            + ",app height= "
            + appBar.getMeasuredHeight());
    FOLD_SIZE =
        imgLayout.getMeasuredHeight() - tab.getMeasuredHeight() - appBar.getMeasuredHeight();
    int height = parentHeight + FOLD_SIZE;
    log("onMeasure: 计算height= " + height);
    int horHeight =
        homePageHorScrollView.getLayoutParams().height =
            parentHeight - tab.getMeasuredHeight() - appBar.getMeasuredHeight();
    homePageHorScrollView.measure(
        widthMeasureSpec, MeasureSpec.makeMeasureSpec(horHeight, MeasureSpec.EXACTLY));
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    log("in layout! getMeasuredHeight()=" + getMeasuredHeight());
    homePageHorScrollView.layout(0, imgLayout.getMeasuredHeight(), right, getMeasuredHeight());
  }

  public int getFoldHeight() {
    return FOLD_SIZE;
  }

  private void log(String log) {
    Log.e(TAG, "----" + log + "----");
  }
}
