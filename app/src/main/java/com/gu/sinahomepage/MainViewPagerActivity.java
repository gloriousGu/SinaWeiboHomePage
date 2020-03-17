package com.gu.sinahomepage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gu.indicatorwidget.TabLayout;
import com.gu.sinahomepage.view.horizontalscroll.MyHorizontalScrollView;

public class MainViewPagerActivity extends AppCompatActivity {
  private static final String[] titles = {"第一页", "第二页", "第三页"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_horizontal_main);
    TabLayout tabLayout = findViewById(R.id.tab);
    tabLayout.createContentByTitles(titles).combine();
    MyHorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);
    horizontalScrollView.bindTabLayout(tabLayout);
  }
}
