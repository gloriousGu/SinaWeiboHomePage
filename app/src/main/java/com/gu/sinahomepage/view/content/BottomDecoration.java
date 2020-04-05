package com.gu.sinahomepage.view.content;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gu.sinahomepage.R;

/** Created by developergu on 2020/4/3 */
public class BottomDecoration extends RecyclerView.ItemDecoration {
  private static final int DIVIDER = 14; // unit of dp
  private static final int FOOTER_DIVER = 60;
  private static final float FOOTER_TEXT_SIZE = 14f; // unit of sp
  private static final String FOOTER_TEXT = "没有更多了";
  private static final int PADDING_LEFT = 12;
  private static final int PADDING_TEXT = 6;
  private Paint mDividerPaint, mFooterTextPaint, mLinePaint;
  private int mPaddingLeft, mPaddingText;
  private int mDivider;
  private int mFooterDivider;
  private int mNoDividerPos; // 0...noDividerPos no drawing divider. noDividerPos+1... drawing divider
  private int mTextWidth;

  public BottomDecoration(Context context) {
    this(context, -1);
  }

  public BottomDecoration(Context context, int noDividerPos) {
    super();
    mNoDividerPos = noDividerPos;
    mDivider = DimensUtils.dp2px(context, DIVIDER);
    mFooterDivider = DimensUtils.dp2px(context, FOOTER_DIVER);
    mDividerPaint = new Paint();
    mDividerPaint.setColor(ContextCompat.getColor(context, R.color.dividerColor));
    mFooterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mFooterTextPaint.setColor(ContextCompat.getColor(context, R.color.footerTextColor));
    mFooterTextPaint.setStrokeWidth(1f);
    mFooterTextPaint.setTextSize(DimensUtils.sp2px(context, FOOTER_TEXT_SIZE));
    mFooterTextPaint.setTextAlign(Paint.Align.CENTER);
    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setStrokeWidth(1f);
    mLinePaint.setColor(ContextCompat.getColor(context, R.color.dividerLineColor));
    mPaddingLeft = DimensUtils.dp2px(context, PADDING_LEFT);
    mPaddingText = DimensUtils.dp2px(context, PADDING_TEXT);
    mTextWidth = getTextWidth();
  }

  @Override
  public void getItemOffsets(
      Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    int pos = parent.getChildAdapterPosition(view);
    if (pos > mNoDividerPos) outRect.top = mDivider;
    if (pos == parent.getAdapter().getItemCount() - 1) outRect.bottom = mFooterDivider;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);
    int childCount = parent.getChildCount();
    int adapterCount = parent.getAdapter().getItemCount();
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();
    for (int i = 0; i < childCount; i++) {
      View view = parent.getChildAt(i);
      int adapterPos = parent.getChildAdapterPosition(view);
      // draw divider top
      if (adapterPos > mNoDividerPos) {
        c.drawRect(left, view.getTop() - mDivider, right, view.getTop(), mDividerPaint);
      }
      // draw last one footer divider
      if (i == childCount - 1 && adapterPos == adapterCount - 1) {
        //        int footerTop = view.getBottom();
        //        int footerBottom = view.getBottom() + mFooterDivider;
        c.drawRect(left, view.getBottom(), right, view.getBottom() + mFooterDivider, mDividerPaint);
        Paint.FontMetricsInt fontMetrics = mFooterTextPaint.getFontMetricsInt();
        int baseline =
            (view.getBottom()
                    + mFooterDivider
                    + view.getBottom()
                    - fontMetrics.bottom
                    - fontMetrics.top)
                / 2;
        c.drawText(FOOTER_TEXT, (left + right) / 2, baseline, mFooterTextPaint);
        c.drawLine(
            left + mPaddingLeft,
            (view.getBottom() + view.getBottom() + mFooterDivider) / 2,
            (right - left - mTextWidth) / 2 - mPaddingText,
            (view.getBottom() + view.getBottom() + mFooterDivider) / 2,
            mLinePaint);
        c.drawLine(
            (left + right + mTextWidth) / 2 + mPaddingText,
            (view.getBottom() + view.getBottom() + mFooterDivider) / 2,
            right - mPaddingLeft,
            (view.getBottom() + view.getBottom() + mFooterDivider) / 2,
            mLinePaint);
      }
    }
  }

  private int getTextWidth() {
    Rect rect = new Rect();
    mFooterTextPaint.getTextBounds(FOOTER_TEXT, 0, FOOTER_TEXT.length(), rect);
    return rect.width();
  }
}
