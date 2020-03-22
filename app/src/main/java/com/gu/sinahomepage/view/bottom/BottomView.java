package com.gu.sinahomepage.view.bottom;

public interface BottomView {

  /**
   * bottom滚动内部child
   *
   * @param dy deltaY
   */
  void scrollCurrentChildDy(int dy);

  /**
   * 是否正在水平滚动
   *
   * @return res
   */
  boolean isScrolling();

  /**
   * 当前页child是否滚动到顶部
   *
   * @return res
   */
  boolean childScroll2Top();

  /** 停止child fling */
  void stopChildViewFling();

  /**
   * 垂直移动deltaY
   *
   * @param dy deltaY
   */
  void moveBy(int dy);

  /**
   * 垂直移动到y
   *
   * @param y p
   */
  void moveTo(int y);

  /** 获得当前移动位置y */
  int getMoveY();

  void initViewSize(int width, int height);

  void initLocation(int l, int t, int r, int b);
}
