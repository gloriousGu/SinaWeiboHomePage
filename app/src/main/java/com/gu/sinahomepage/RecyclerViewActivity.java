package com.gu.sinahomepage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recycler_view);
    initAnim();
    handler = new Handler();
    homePageView = findViewById(R.id.homePageView);
    homePageView.setRefreshListener(this);
    TopAppBar appBar = findViewById(R.id.appBar);
    appBar.setAppBarListener(this);
    arrow = findViewById(R.id.arrow);
    right_btn = findViewById(R.id.right_btn);
    nickname = findViewById(R.id.nickname);
    list = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      list.add("text " + i);
    }
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
    right_btn.setImageLevel(doPull ? 1 : 0);
    if (!doPull) right_btn.setRotation(0);
  }

  @Override
  public void onAppBarChangeRefreshSize(int size) {
    right_btn.setRotation(size * 2);
  }

  @Override
  public void onAppBarStartRefreshing() {
    right_btn.setImageLevel(1);
    right_btn.startAnimation(animation);
  }

  @Override
  public void onAppBarStopRefreshing() {
    animation.cancel();
    right_btn.setImageLevel(0);
  }

  @Override
  public void onFoldStateChanged(boolean isFold) {
    arrow.setImageLevel(isFold ? 1 : 0);
    right_btn.setImageLevel(isFold ? 2 : 0);
    nickname.setVisibility(isFold ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public void onStartRefresh() {
    /* 模拟一个延迟加载效果 3秒后自动结束 */
    handler.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            homePageView.stopRefresh();
          }
        },
        3000);
  }

  @Override
  public void onStopRefresh() {
    /* finish load data! Do nothing */
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
