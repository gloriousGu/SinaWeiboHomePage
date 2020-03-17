package com.gu.indicatorwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabLayout extends FrameLayout {

  private ColorStateList csl;

  private int mIndicatorColor;
  private List<String> mTitles;

  private int margin;
  private int rd;
  // textSize unit is sp
  private int textSize;
  private LinearLayout mContentLayout;
  private IndicatorView mIndicatorView;
  //    private ViewPager viewPager;

  private int startPos;
  private boolean clicked;

  public void onPageScrolled(int from, int to, float positionOffset) {
    // 如果是点击事件，则不使用滚动的百分比，使用默认动画处理
    if (clicked) return;
    // 如果是左右滑动，使用百分比处理mIndicatorView效果
    if (from < to) {
      // 向右
      mIndicatorView.setPercent(positionOffset);
    } else if (from > to) {
      // 向左
      mIndicatorView.setPercent(positionOffset - 1);
    }
  }

  private void log(String log) {
    Log.e("log", "----" + log + "----");
  }

  public int getPos() {
    return mIndicatorView.getPos();
  }

  public void selectPos(int pos) {
    mIndicatorView.setCurrentNoAnim(pos);
    updateSelected(pos);
  }

  //  @Override
  //  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  //    // 如果是点击事件，则不使用滚动的百分比，使用默认动画处理
  //    if (clicked) return;
  //    // 如果是左右滑动，使用百分比处理mIndicatorView效果
  //    if (position == startPos) {
  //      // 向右
  //      mIndicatorView.setPercent(positionOffset);
  //    } else if (position < startPos) {
  //      // 向左
  //      mIndicatorView.setPercent(positionOffset - 1);
  //    }
  //  }
  //
  //  @Override
  //  public void onPageSelected(int position) {
  //    updateSelected(position);
  //  }

  //  @Override
  //  public void onPageScrollStateChanged(int state) {
  //    if (state == 0) {
  //      startPos = viewPager.getCurrentItem();
  //      if (!clicked) {
  //        mIndicatorView.setCurrentNoAnim(startPos);
  //      }
  //      clicked = false;
  //    }
  //  }

  //  @Override
  //  public void onClick(View v) {
  //    if (viewPager == null || mIndicatorView == null) return;
  //    int to = (Integer) v.getTag();
  //    int from = viewPager.getCurrentItem();
  //    viewPager.setCurrentItem(to, true);
  //    clicked = true;
  //    if (isOffsetTwo(from, to)) {
  //      mIndicatorView.setCurrent(to);
  //    } else if (toRight(from, to)) {
  //      mIndicatorView.indicator2Right();
  //    } else if (toLeft(from, to)) {
  //      mIndicatorView.indicator2Left();
  //    }
  //  }

  // 滚动跨度超过两个
  private boolean isOffsetTwo(int from, int to) {
    return Math.abs(to - from) > 1;
  }

  // 向右滚动
  private boolean toRight(int from, int to) {
    return to - from == 1;
  }

  // 向左滚动
  private boolean toLeft(int from, int to) {
    return to - from == -1;
  }

  private void updateSelected(int pos) {
    int size = mContentLayout.getChildCount();
    for (int i = 0; i < size; i++) {
      mContentLayout.getChildAt(i).setSelected(pos == i);
    }
  }

  // ------------------------------------------------------//
  public TabLayout(@NonNull Context context) {
    this(context, null);
  }

  public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    textSize = 14;
    csl = getResources().getColorStateList(R.color.text_state_color);
    margin = 80;
    mIndicatorColor = Color.RED;
    rd = 10;
    int minHeight = context.getResources().getDimensionPixelOffset(R.dimen.min_tab_layout_height);
    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);
      mIndicatorColor = a.getColor(R.styleable.TabLayout_indicatorColor, Color.RED);
      csl = a.getColorStateList(R.styleable.TabLayout_textColor);
      if (csl == null) {
        csl = getResources().getColorStateList(R.color.text_state_color);
      }
      textSize = px2sp(context, a.getDimension(R.styleable.TabLayout_textSize, 14));
      margin = a.getDimensionPixelOffset(R.styleable.TabLayout_margin, 80);
      rd = a.getDimensionPixelOffset(R.styleable.TabLayout_rd, 2);
      a.recycle();
    }
    mTitles = new ArrayList<>();
    LayoutParams params = new LayoutParams(context, attrs);
    if (params.height < minHeight) params.height = minHeight;
    setLayoutParams(params);
    create(true);
  }

  //  public TabLayout setViewPager(ViewPager vp) {
  //
  //    if (viewPager != null) {
  //      viewPager.removeOnPageChangeListener(this);
  //    }
  //    viewPager = vp;
  //    return this;
  //  }

  /**
   * 用titles数组创建TabLayout（ 更新标题时调用）
   *
   * @param titles 标题
   */
  public TabLayout createContentByTitles(String[] titles) {
    if (!mTitles.isEmpty()) {
      mTitles.clear();
    }
    mTitles.addAll(Arrays.asList(titles));
    create(false);
    return this;
  }

  /** 让viewpager的滚动与tabLayout建立联系 */
  public void combine() {
    if (mTitles == null || mTitles.isEmpty()) {
      try {
        throw new Exception("异常：combineViewPager()之前，请先设置titles！");
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }
    //    if (viewPager == null) {
    //      try {
    //        throw new Exception("异常：combineViewPager()之前，请先设置viewPager！");
    //      } catch (Exception e) {
    //        e.printStackTrace();
    //      }
    //      return;
    //    }
    //    viewPager.addOnPageChangeListener(this);
  }

  public TabLayout setTextColors(ColorStateList csl) {
    this.csl = csl;
    return this;
  }

  public TabLayout setMargin(int margin) {
    this.margin = margin;
    return this;
  }

  public TabLayout setRd(int rd) {
    this.rd = rd;
    return this;
  }

  /**
   * 设置tab文字尺寸,单位sp
   *
   * @param textSize size of sp
   * @return TabLayout
   */
  public TabLayout setTextSize(int textSize) {
    this.textSize = textSize;
    return this;
  }

  /**
   * 设置indicatorView颜色
   *
   * @param indicatorColor 颜色rgb
   * @return TabLayout
   */
  public TabLayout setIndicatorColor(int indicatorColor) {
    mIndicatorColor = indicatorColor;
    return this;
  }

  /** activity或fragment销毁时调用 */
  public void destroyView() {
    releaseListener();
    removeAllViews();
    if (mTitles != null) mTitles.clear();
    mTitles = null;
    mContentLayout = null;
    mIndicatorView = null;
  }

  /**
   * 创建一个新的
   *
   * @param init 是否是第一次创建
   */
  private void create(boolean init) {
    if (init) {
      releaseListener();
      removeAllViews();
    }
    addTabContent(getContext());
    addIndicatorView(getContext(), 0);
  }

  /** 更新属性 */
  public void update() {
    int pos = 0;
    if (mContentLayout != null) {
      for (int i = 0; i < mContentLayout.getChildCount(); i++) {
        TextView textView = (TextView) mContentLayout.getChildAt(i);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(csl);
        if (textView.isSelected()) pos = i;
      }
      mContentLayout.invalidate();
    }
    addIndicatorView(getContext(), pos);
    invalidate();
  }

  /**
   * 添加tab item
   *
   * @param context 上下文
   */
  private void addTabContent(Context context) {
    if (mTitles == null || mTitles.isEmpty()) return;
    if (mContentLayout != null) {
      releaseListener();
      removeView(mContentLayout);
    }
    mContentLayout = new LinearLayout(context);
    LayoutParams params =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mContentLayout.setOrientation(LinearLayout.HORIZONTAL);
    mContentLayout.setLayoutParams(params);
    for (int i = 0; i < mTitles.size(); i++) {
      TextView textView = new TextView(context);
      textView.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
      textView.setGravity(Gravity.CENTER);
      textView.setText(mTitles.get(i));
      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
      textView.setTag(i);
      textView.setTextColor(csl);

      /** 此处先取消点击事件 */
      //      textView.setOnClickListener(this);
      if (i == 0) textView.setSelected(true);
      mContentLayout.addView(textView);
    }
    addView(mContentLayout);
  }

  /**
   * 添加indicatorView
   *
   * @param context 上下文
   * @param pos 出现的位置
   */
  private void addIndicatorView(Context context, int pos) {
    if (mTitles == null || mTitles.isEmpty()) return;
    if (mIndicatorView != null) removeView(mIndicatorView);
    mIndicatorView = new IndicatorView(context);
    mIndicatorView.setNum(mTitles.size());
    mIndicatorView.setMargin(margin);
    mIndicatorView.setRoundCorner(rd);
    mIndicatorView.setIndicatorColor(mIndicatorColor);
    mIndicatorView.setParamCurrentPos(pos);
    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10);
    params.gravity = Gravity.BOTTOM;
    mIndicatorView.setLayoutParams(params);
    addView(mIndicatorView);
  }

  /** 释放listeners */
  private void releaseListener() {
    int size = mTitles == null ? 0 : mTitles.size();
    for (int i = 0; i < size; i++) {
      View view = findViewWithTag(i);
      if (view != null) view.setOnClickListener(null);
    }
    //    if (viewPager != null) {
    //      viewPager.removeOnPageChangeListener(this);
    //    }
  }

  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }
}
