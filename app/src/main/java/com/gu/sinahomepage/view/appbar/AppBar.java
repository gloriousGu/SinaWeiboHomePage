package com.gu.sinahomepage.view.appbar;

public interface AppBar {
  void translationY(int y);

  int getMeasuredHeight();

  void changeFoldState(boolean isFold);

  void setTransparent(boolean transparent);

  void setAppBarListener(AppBarListener listener);

  void updateByStretch(int stretchSize, int refreshSize);

  void start2Refresh();

  void stop2Refresh();

  /** AppBar 接到拖拽时回调 更新界面 */
  interface AppBarListener {

    void onAppBarChangePullState(boolean doPull);

    /**
     * 下拉时 appBar通知具体view响应下拉
     *
     * @param size 下拉距离
     */
    void onAppBarChangePullSize(int size);

    void onAppBarStartRefreshing();

    void onAppBarStopRefreshing();

    void onFoldStateChanged(boolean isFold);
  }
}
