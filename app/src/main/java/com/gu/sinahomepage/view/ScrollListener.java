package com.gu.sinahomepage.view;

public interface ScrollListener {
  void onScrollToBottom();

  void onScrollToTop();

  void onScroll(int scrollY);

  void notBottom();
}
