package com.gu.sinahomepage.view.appbar;

public interface AppBar {
  void changeByScroll(int scrollY);

  void setFoldSize(int foldSize);

  int getMeasuredHeight();

  void setTransparent(boolean transparent);
}
