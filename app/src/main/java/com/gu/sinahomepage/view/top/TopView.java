package com.gu.sinahomepage.view.top;

public interface TopView {

  void stretch(int dy);

  void stretchRecover();

  int getInitHeight();

  int getStretchSize();
}
