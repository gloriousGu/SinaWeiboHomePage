package com.gu.sinahomepage.view.content;

import android.content.Context;

public class DimensUtils {
  /** dp转px */
  public static int dp2px(Context context, int dp) {
    float density = context.getResources().getDisplayMetrics().density;
    return (int) (dp * density + 0.5f);
  }
  /** 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /** 根据距离和速度计算时间 */
  public static long calcDuration(int from, int to, int vel) {
    return (long) (1000f * Math.abs(from - to) / vel);
  }
}
