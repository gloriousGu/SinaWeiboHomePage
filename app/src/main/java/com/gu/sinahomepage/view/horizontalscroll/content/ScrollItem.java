package com.gu.sinahomepage.view.horizontalscroll.content;

/** 内容view必须实现的方法 说明： 1.内容view放在HomePageHorScrollView中. 2.内容view可以是recyclerView、scrollView等 */
public interface ScrollItem {

  void stopFling();

  boolean isTop();

  void scrollDy(int dy);
}
