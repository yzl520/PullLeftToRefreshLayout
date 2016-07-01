package com.android.yzl.lib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yzl on 2016/6/6.
 */
public class PullLeftToRefreshLayout extends FrameLayout {

    private static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    private static final long BACK_ANIM_DUR = 500;
    private static final long BEZIER_BACK_DUR = 350;
    private static final int ROTATION_ANIM_DUR = 200;

    /**
     * MoreView移动的最大距离
     */
    private static float MORE_VIEW_MOVE_DIMEN;
    private static final int ROTATION_ANGLE  = 180;

    private static String SCAN_MORE;
    private static String RELEASE_SCAN_MORE;

    private float mTouchStartX;
    private float mTouchCurX;

    private float mPullWidth;
    private float mFooterWidth;

    /**
     * 目的是为了将moreView隐藏以便滑动
     */
    private int moreViewMarginRight;
    private int footerViewBgColor;

    private boolean isRefresh = false;
    private boolean scrollState = false;

    private View mChildView;
    private AnimView footerView;
    private View moreView;
    private TextView moreText;
    private ImageView arrowIv;

    private ValueAnimator mBackAnimator;
    private RotateAnimation mArrowRotateAnim;
    private RotateAnimation mArrowRotateBackAnim;

    OnScrollListener onScrollListener;
    OnRefreshListener onRefreshListener;

    private DecelerateInterpolator interpolator = new DecelerateInterpolator(10);

    public PullLeftToRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public PullLeftToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLeftToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPullWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
        MORE_VIEW_MOVE_DIMEN = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        mFooterWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        moreViewMarginRight = -getResources().getDimensionPixelSize(R.dimen.dp_26);
        SCAN_MORE = getResources().getString(R.string.scan_more);
        RELEASE_SCAN_MORE = getResources().getString(R.string.release_scan_more);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PullLeftToRefreshLayout);
        footerViewBgColor = ta.getColor(R.styleable.PullLeftToRefreshLayout_footerBgColor, Color.rgb(243, 242, 242));
        ta.recycle();

        this.post(new Runnable() {
            @Override
            public void run() {
                mChildView = getChildAt(0);
                addFooterView();
                addMoreView();
                initBackAnim();
                initRotateAnim();
            }
        });
    }

    private void addFooterView() {
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;

        footerView = new AnimView(getContext());
        footerView.setLayoutParams(params);
        footerView.setBgColor(footerViewBgColor);
        footerView.setBezierBackDur(BEZIER_BACK_DUR);
        addViewInternal(footerView);
    }

    private void addMoreView() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.setMargins(0, 0, moreViewMarginRight, 0);

        moreView = LayoutInflater.from(getContext()).inflate(R.layout.item_load_more, this, false);
        moreView.setLayoutParams(params);
        moreText = (TextView) moreView.findViewById(R.id.tvMoreText);
        arrowIv = (ImageView) moreView.findViewById(R.id.ivRefreshArrow);

        addViewInternal(moreView);
    }

    private void initRotateAnim() {
        int pivotType = Animation.RELATIVE_TO_SELF;
        float pivotValue = 0.5f;
        mArrowRotateAnim = new RotateAnimation(0, ROTATION_ANGLE, pivotType, pivotValue, pivotType, pivotValue);
        mArrowRotateAnim.setInterpolator(ANIMATION_INTERPOLATOR);
        mArrowRotateAnim.setDuration(ROTATION_ANIM_DUR);
        mArrowRotateAnim.setFillAfter(true);

        mArrowRotateBackAnim = new RotateAnimation(ROTATION_ANGLE, 0, pivotType, pivotValue, pivotType, pivotValue);
        mArrowRotateBackAnim.setInterpolator(ANIMATION_INTERPOLATOR);
        mArrowRotateBackAnim.setDuration(ROTATION_ANIM_DUR);
        mArrowRotateBackAnim.setFillAfter(true);
    }

    private void initBackAnim() {
        if (mChildView == null) {
            return;
        }
        mBackAnimator = ValueAnimator.ofFloat(mPullWidth, 0);
        mBackAnimator.addListener(new AnimListener());
        mBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                if (val <= mFooterWidth) {
                    val = interpolator.getInterpolation(val / mFooterWidth) * val;
                    footerView.getLayoutParams().width = (int) val;
                    footerView.requestLayout();
                }

                if (mChildView != null) {
                    mChildView.setTranslationX(-val);
                }

                moveMoreView(val, true);
            }
        });
        mBackAnimator.setDuration(BACK_ANIM_DUR);
    }

    private void addViewInternal(@NonNull View child) {
        super.addView(child);
    }

    @Override
    public void addView(View child) {
        if (getChildCount() >= 1) {
            throw new RuntimeException("you can only attach one child");
        }

        mChildView = child;
        super.addView(child);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefresh) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = ev.getX();
                mTouchCurX = mTouchStartX;
                setScrollState(false);
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = ev.getX();
                float dx = curX - mTouchStartX;

                if (dx < -10 && !canScrollRight()) {//点击事件要传回子类
                    setScrollState(true);
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefresh) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mTouchCurX = event.getX();
                float dx = mTouchStartX - mTouchCurX;
                dx = Math.min(mPullWidth * 2, dx);
                dx = Math.max(0, dx);

                if (mChildView == null || dx <= 0) {
                    return true;
                }

                float unit = dx / 2;
                float offsetx = interpolator.getInterpolation(unit / mPullWidth) * unit;
                mChildView.setTranslationX(-offsetx);
                footerView.getLayoutParams().width = (int) offsetx;
                footerView.requestLayout();

                moveMoreView(offsetx, false);

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mChildView == null) {
                    return true;
                }

                float childDx = Math.abs(mChildView.getTranslationX());
                if (childDx >= mFooterWidth) {
                    mBackAnimator.setFloatValues(childDx, 0);
                    mBackAnimator.start();
                    footerView.releaseDrag();

                    if (reachReleasePoint()) {
                        isRefresh = true;
                    }
                } else {
                    mBackAnimator.setFloatValues(childDx, 0);
                    mBackAnimator.start();
                }

                setScrollState(false);

                return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * direction Negative to check scrolling left, positive to check scrolling right
     *
     * @return
     */
    private boolean canScrollRight() {
        if (mChildView == null) {
            return false;
        }

        return ViewCompat.canScrollHorizontally(mChildView, 1);
    }

    private void moveMoreView(float offsetx, boolean release) {
        float dx = offsetx / 2;
        if (dx <= MORE_VIEW_MOVE_DIMEN) {
            moreView.setTranslationX(-dx);
            if (!release && switchMoreText(SCAN_MORE)) {
                arrowIv.clearAnimation();
                arrowIv.startAnimation(mArrowRotateBackAnim);
            }
        } else {
            if (switchMoreText(RELEASE_SCAN_MORE)) {
                arrowIv.clearAnimation();
                arrowIv.startAnimation(mArrowRotateAnim);
            }
        }
    }

    private boolean switchMoreText(String text) {
        if (text.equals(moreText.getText().toString())) {
            return false;
        }
        moreText.setText(text);
        return true;
    }

    private boolean reachReleasePoint() {
        return RELEASE_SCAN_MORE.equals(moreText.getText().toString());
    }

    private class AnimListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (onRefreshListener != null && isRefresh) {
                onRefreshListener.onRefresh();
            }
            moreText.setText(SCAN_MORE);
            arrowIv.clearAnimation();
            isRefresh = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    private void setScrollState(boolean scrollState) {
        if (this.scrollState == scrollState) {
            return;
        }
        this.scrollState = scrollState;
        if (onScrollListener != null) {
            onScrollListener.onScrollChange(scrollState);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}
