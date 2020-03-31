package com.gu.sinahomepage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gu.sinahomepage.view.HomePageView;
import com.gu.sinahomepage.view.appbar.AppBar;
import com.gu.sinahomepage.view.appbar.SquareImageBtn;
import com.gu.sinahomepage.view.appbar.TopAppBar;
import com.gu.sinahomepage.view.content.MyRecyclerView;
import com.gu.sinahomepage.view.bottom.horizontalscroll.ViewPager;
import com.gu.sinahomepage.view.tab.TabLayout;

import java.util.ArrayList;
import java.util.List;

/** 使用recyclerView作为HorizontalScrollView的child --Demo-- */
public class RecyclerViewActivity extends AppCompatActivity
    implements AppBar.AppBarListener, HomePageView.HomePageRefreshListener {
  private static final String[] titles = {"第一页", "第二页", "第三页"};

  List<String> list;
  MyRecyclerView rv1, rv2, rv3;
  SquareImageBtn arrow, right_btn;
  TextView nickname;
  Handler handler;
  HomePageView homePageView;
  Animation animation;
  boolean isRefreshing;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recycler_view);
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
    rv1 = findViewById(R.id.rv1);
    rv2 = findViewById(R.id.rv2);
    rv3 = findViewById(R.id.rv3);

    rv1.setLayoutManager(new LinearLayoutManager(this));
    rv2.setLayoutManager(new LinearLayoutManager(this));
    rv3.setLayoutManager(new LinearLayoutManager(this));
    list = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      list.add("text " + i);
    }
    rv1.setAdapter(new DataAdapter(this, list));
    rv2.setAdapter(new DataAdapter(this, list));
    rv3.setAdapter(new DataAdapter(this, list));
  }

  private void initAnim() {
    animation =
        new RotateAnimation(
            0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    animation.setDuration(500);
    animation.setRepeatCount(Animation.INFINITE);
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
    handler.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            homePageView.stopRefresh();
          }
        },
        3000);
    isRefreshing = true;
  }

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
    if (animation != null) animation.cancel();
  }

  static class DataAdapter extends RecyclerView.Adapter {
    LayoutInflater inflater;
    List<String> list;

    DataAdapter(Context context, List<String> list) {
      super();
      inflater = LayoutInflater.from(context);
      this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(inflater.inflate(R.layout.recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      ((TextView) holder.itemView).setText(list.get(position));
    }

    @Override
    public int getItemCount() {
      return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Log.e("TAG", "onClick: " + getAdapterPosition());
              }
            });
      }
    }
  }
}
