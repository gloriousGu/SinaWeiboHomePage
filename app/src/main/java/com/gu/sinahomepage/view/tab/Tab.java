package com.gu.sinahomepage.view.tab;

import com.gu.sinahomepage.view.horizontalscroll.ViewPager;

public interface Tab {
  void setViewPager(ViewPager viewPager);

  void onPageScrolledFinish();

  boolean noAnim();

  void onPageScrolled(int from, int to, float positionOffset);
}
