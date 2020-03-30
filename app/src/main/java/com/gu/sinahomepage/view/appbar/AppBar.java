package com.gu.sinahomepage.view.appbar;

public interface AppBar {
  void translationY(int y);

  int getMeasuredHeight();

  void changeFoldState(boolean isFold);

  void setTransparent(boolean transparent);

  void setAppBarListener(AppBarListener listener);

  void changeRefreshSize(int refreshSize);

  void changePullState(boolean start2Pull);

  void start2Refresh();

  void stop2Refresh();

  interface AppBarListener {

    void onAppBarChangePullState(boolean doPull);

    void onAppBarChangeRefreshSize(int size);

    void onAppBarStartRefreshing();

    void onAppBarStopRefreshing();

    void onFoldStateChanged(boolean isFold);
  }
}
