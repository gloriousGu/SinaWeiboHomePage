package com.gu.sinahomepage.view.top;

public interface TopView {

  void stretchBy(int dy);

  void stretchRecoverBy(int y);

  int getStretchSize();

  int getMeasuredHeight();
}
