package com.gu.sinahomepage.view.top;

public interface TopView {

  void stretchBy(int dy);

  void stretchRecoverBy(int y);

  void stretchRecover();

  int getStretchSize();

  int getMeasuredHeight();
}
