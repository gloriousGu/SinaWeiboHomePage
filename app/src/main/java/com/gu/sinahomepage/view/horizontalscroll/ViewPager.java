package com.gu.sinahomepage.view.horizontalscroll;

import com.gu.sinahomepage.view.bottom.BottomView;
import com.gu.sinahomepage.view.tab.Tab;

public interface ViewPager extends BottomView {

  /** 获取当前viewPage pos */
  int getCurrentItemPos();

  /** 执行滚动到位置to */
  void setCurrentItem(int to);

  /** 绑定tab */
  void bindTab(Tab tab);
}
