package com.gu.indicatorwidget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/7/9
 */
public class IndicatorView extends View {
  private int num;
  private int margin;
  private int itemWidth, indicatorWidth, itemHeight;
  private int mPos = 0;
  private float percent;
  private Paint p;
  private Scroller mScroller;
  private boolean animMoveFlag, noAnimMoveFlag;
  private static final String TAG = "TAG";
  private int rd;
  private RectF mRect;

  public IndicatorView(Context context) {
    this(context, null);
  }

  public IndicatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    p = new Paint();
    mScroller = new Scroller(context);
    mRect = new RectF();
    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView);
      num = a.getInt(R.styleable.IndicatorView_num, 4);
      rd = a.getDimensionPixelOffset(R.styleable.IndicatorView_rd, 10);
      margin = a.getDimensionPixelOffset(R.styleable.IndicatorView_margin, 80);
      p.setColor(a.getColor(R.styleable.IndicatorView_bg_color, Color.RED));
      a.recycle();
    } else {
      num = 4;
      rd = 10;
      margin = 80;
      p.setColor(Color.RED);
    }
  }

  public void setNum(int num) {
    this.num = num;
  }

  public void setRoundCorner(int rd) {
    this.rd = rd;
  }

  public void setMargin(int margin) {
    this.margin = margin;
  }

  public void setIndicatorColor(int color) {
    p.setColor(color);
    Log.e(TAG, "setIndicatorColor: color=" + color);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    itemWidth = MeasureSpec.getSize(widthMeasureSpec) / num;
    itemHeight = MeasureSpec.getSize(heightMeasureSpec);
    indicatorWidth = itemWidth - 2 * margin;
  }

  private void updateRect(RectF rect, float l, float t, float r, float b) {
    rect.left = l;
    rect.top = t;
    rect.right = r;
    rect.bottom = b;
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (animMoveFlag) {
      if (mScroller.computeScrollOffset()) {
        updateRect(
            mRect, mScroller.getCurrX(), 0, mScroller.getCurrX() + indicatorWidth, getHeight());
        canvas.drawRoundRect(mRect, rd, rd, p);
        postInvalidate();
      } else {
        updateRect(
            mRect,
            mPos * itemWidth + margin,
            0,
            mPos * itemWidth + margin + indicatorWidth,
            getHeight());
        canvas.drawRoundRect(mRect, rd, rd, p);
        animMoveFlag = false;
      }
    } else if (noAnimMoveFlag) {
      noAnimMoveFlag = false;
      updateRect(
          mRect,
          mPos * itemWidth + margin,
          0,
          mPos * itemWidth + margin + indicatorWidth,
          getHeight());
      canvas.drawRoundRect(mRect, rd, rd, p);
    } else {
      drawRect(canvas, percent);
    }
  }

  /** @param percent (+1.0) ~ (-1.0) */
  public void drawRect(Canvas canvas, float percent) {
    if (percent >= -1f && percent <= -0.5f) {
      updateRect(
          mRect,
          getPreLeft(),
          0,
          (int) (getPreLeft() + 2 * itemWidth * (1.5f + percent) - 2 * margin),
          itemHeight);
      canvas.drawRoundRect(mRect, rd, rd, p);
    } else if (percent > -0.5f && percent <= 0f) {
      updateRect(
          mRect,
          (int) (getCurRight() - itemWidth * (1f - 2 * percent) + 2 * margin),
          0,
          getCurRight(),
          itemHeight);
      canvas.drawRoundRect(mRect, rd, rd, p);
    } else if (percent > 0f && percent <= 0.5f) {
      updateRect(
          mRect,
          getCurLeft(),
          0,
          getCurLeft() + (int) (itemWidth * (1f + 2 * percent)) - 2 * margin,
          itemHeight);
      canvas.drawRoundRect(mRect, rd, rd, p);
    } else if (percent > 0.5f && percent <= 1f) {
      updateRect(
          mRect,
          getNextRight() - (int) (2 * itemWidth * (1.5f - percent) - 2 * margin),
          0,
          getNextRight(),
          itemHeight);
      canvas.drawRoundRect(mRect, rd, rd, p);
    }
  }

  private int getCurLeft() {
    return mPos * itemWidth + margin;
  }

  private int getPreLeft() {
    return (mPos - 1) * itemWidth + margin;
  }

  private int getCurRight() {
    return mPos * itemWidth + indicatorWidth + margin;
  }

  private int getNextRight() {
    return (mPos + 1) * itemWidth + indicatorWidth + margin;
  }

  public void setPercent(float percent) {
    this.percent = percent;
    postInvalidate();
  }

  public void setCurrent(int pos) {
    animMoveFlag = true;
    mScroller.startScroll(mPos * itemWidth + margin, 0, (pos - mPos) * itemWidth, 0, 500);
    this.mPos = pos;
    postInvalidate();
  }

  public void setCurrentNoAnim(int pos) {
    this.mPos = pos;
    percent = 0f;
    noAnimMoveFlag = true;
    postInvalidate();
  }

  /**
   * 只更新参数，不进行postInvalidate();
   *
   * @param pos current pos
   */
  public void setParamCurrentPos(int pos) {
    this.mPos = pos;
    percent = 0f;
    noAnimMoveFlag = true;
  }

  public void indicator2RightNoAnim() {
    this.mPos += 1;
    percent = 0f;
    noAnimMoveFlag = true;
    postInvalidate();
  }

  public void indicator2LeftNoAnim() {
    this.mPos -= 1;
    percent = 0f;
    noAnimMoveFlag = true;
    postInvalidate();
  }

  public void indicator2Right() {
    floatAnimator(percent, 1f, 1);
  }

  public void indicator2Left() {
    floatAnimator(percent, -1f, -1);
  }

  public void indicatorComeback() {
    floatAnimator(percent, 0, 0);
  }

  public void floatAnimator(float from, float to, final int add) {
    Log.e(TAG, "floatAnimator: from=" + from + ",to=" + to);
    ValueAnimator mAnimator = ValueAnimator.ofFloat(from, to);
    mAnimator.addUpdateListener(
        new ValueAnimator.AnimatorUpdateListener() {

          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            setPercent((float) animation.getAnimatedValue());
          }
        });
    mAnimator.addListener(
        new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {}

          @Override
          public void onAnimationEnd(Animator animation) {
            mPos += add;
            percent = 0f;
          }

          @Override
          public void onAnimationCancel(Animator animation) {}

          @Override
          public void onAnimationRepeat(Animator animation) {}
        });
    mAnimator.setDuration(300);
    mAnimator.start();
  }

  public int getPos() {
    return mPos;
  }
}
