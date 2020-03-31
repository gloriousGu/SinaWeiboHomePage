package com.gu.sinahomepage;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gu.sinahomepage.view.HomePageView;
import com.gu.sinahomepage.view.appbar.AppBar;
import com.gu.sinahomepage.view.appbar.SquareImageBtn;
import com.gu.sinahomepage.view.appbar.TopAppBar;
import com.gu.sinahomepage.view.bottom.horizontalscroll.ViewPager;
import com.gu.sinahomepage.view.tab.TabLayout;

/** 使用scrollView作为HorizontalScrollView的child --Demo-- */
public class ScrollViewActivity extends AppCompatActivity
    implements HomePageView.HomePageRefreshListener, AppBar.AppBarListener {
  private static final String[] titles = {"第一页", "第二页", "第三页"};
  SquareImageBtn arrow, right_btn;
  TextView nickname;
  Handler handler;
  HomePageView homePageView;
  Animation animation;
  boolean isRefreshing;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_horizontal_main);

    initAnim();
    handler = new Handler();
    homePageView = (HomePageView) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    homePageView.setRefreshListener(this);
    TopAppBar appBar = findViewById(R.id.appBar);
    appBar.setAppBarListener(this);
    arrow = findViewById(R.id.arrow);
    right_btn = findViewById(R.id.right_btn);
    nickname = findViewById(R.id.nickname);

    TabLayout tabLayout = findViewById(R.id.tab);
    tabLayout.createContentByTitles(titles).combine();
    ViewPager horizontalScrollView = findViewById(R.id.horizontalScrollView);
    horizontalScrollView.bindTab(tabLayout);
  }

  private void initAnim() {
    animation =
        new RotateAnimation(
            0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    animation.setDuration(500);
    animation.setRepeatCount(Animation.INFINITE);
  }

  public void textClick(View view) {
    Log.e("TAG", "textClick: ! ---- " + ((TextView) view).getText());
  }

  @Override
  public void onAppBarChangePullState(boolean doPull) {
    right_btn.setImageResource(
        doPull ? R.drawable.common_loading_white_36px : R.drawable.ic_more_horiz_white_36px);
    if (!doPull) right_btn.setRotation(0);
  }

  @Override
  public void onAppBarChangePullSize(int size) {
    right_btn.setRotation(size * 2);
  }

  @Override
  public void onAppBarStartRefreshing() {
    right_btn.setImageResource(R.drawable.common_loading_white_36px);
    right_btn.startAnimation(animation);
  }

  @Override
  public void onAppBarStopRefreshing() {
    animation.cancel();
    right_btn.setImageResource(R.drawable.ic_more_horiz_white_36px);
  }

  @Override
  public void onFoldStateChanged(boolean isFold) {
    arrow.setImageLevel(isFold ? 1 : 0);
    right_btn.setImageResource(
        isFold ? R.drawable.ic_more_horiz_grey_700_36px : R.drawable.ic_more_horiz_white_36px);
    nickname.setVisibility(isFold ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public void onStartRefresh() {
    Toast.makeText(getApplicationContext(), "请稍后,加载中...", Toast.LENGTH_SHORT).show();
    /* 模拟一个延迟加载效果 3秒后自动结束 */
    handler.postDelayed(refreshRunnable, 10000);
    isRefreshing = true;
  }

  Runnable refreshRunnable =
      new Runnable() {
        @Override
        public void run() {
          homePageView.stopRefresh();
        }
      };

  @Override
  public void onStopRefresh() {
    /* finish load data! Do nothing */
    isRefreshing = false;
    Toast.makeText(getApplicationContext(), "加载结束", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    // 刷新中 禁止再次拖拽
    return !isRefreshing && super.dispatchTouchEvent(ev);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    release();
  }

  private void release() {
    if (handler != null) {
      Log.e("TAG", "handler removeCallbacks! ");
      handler.removeCallbacks(refreshRunnable);
    }
    if (animation != null) animation.cancel();
  }
}
