package com.gu.sinahomepage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gu.sinahomepage.view.tab.TabLayout;
import com.gu.sinahomepage.view.horizontalscroll.HomePageHorScrollView;

public class ScrollViewActivity extends AppCompatActivity {
  private static final String[] titles = {"第一页", "第二页", "第三页"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_horizontal_main);
    TabLayout tabLayout = findViewById(R.id.tab);
    tabLayout.createContentByTitles(titles).combine();
    HomePageHorScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);
    horizontalScrollView.bindTabLayout(tabLayout);
  }

  public void textClick(View view) {
    Log.e("TAG", "textClick: ! ---- " + ((TextView) view).getText());
  }
}
