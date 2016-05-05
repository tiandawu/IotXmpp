package com.cqupt.xmpp.widght.RevealView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by tiandawu on 2015/10/9.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RelativeRevealView extends RelativeLayout {

    private Path mClipPath;
    private float mClipRadius = 0;
    private int mClipCenterX, mClipCenterY = 0;
    private Animator mAnimator;
    private static final int DEFAULT_DURATION = 600;
    private boolean mIsContentShown = true;

    public RelativeRevealView(Context context) {
        this(context, null);
    }

    public RelativeRevealView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeRevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mClipPath = new Path();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mClipCenterX = w / 2;
        mClipCenterY = h / 2;
        if (!mIsContentShown) {
            mClipRadius = 0;
        } else {
            mClipRadius = (float) (Math.sqrt(w * w + h * h) / 2);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public float getClipRadius() {
        return mClipRadius;
    }

    public void setClipRadius(float clipRadius) {
        mClipRadius = clipRadius;
        invalidate();
    }

    public boolean isContentShown() {
        return mIsContentShown;
    }

    public void setContentShown(boolean isContentShown) {
        mIsContentShown = isContentShown;
        if (mIsContentShown) {
            mClipRadius = 0;
        } else {
            mClipRadius = getMaxRadius(mClipCenterX, mClipCenterY);
        }
        invalidate();
    }

    public void show() {
        show(DEFAULT_DURATION, null);
    }

    public void hide() {
        hide(DEFAULT_DURATION, null);
    }

    public void next() {
        next(DEFAULT_DURATION);
    }

    public void show(int duration, AnimaFinshListener animaFinshListener) {
        show(getWidth() / 2, getHeight() / 2, duration, animaFinshListener);
    }

    public void hide(int duration, AnimaFinshListener animaFinshListener) {
        hide(getWidth() / 2, getHeight() / 2, duration, animaFinshListener);
    }

    public void next(int duration) {
        next(getWidth() / 2, getHeight() / 2, duration);
    }

    public void show(int x, int y) {
        show(x, y, DEFAULT_DURATION, null);
    }

    public void hide(int x, int y) {
        hide(x, y, DEFAULT_DURATION, null);
    }

    public void next(int x, int y) {
        next(x, y, DEFAULT_DURATION);
    }

    public void show(int x, int y, int duration, final AnimaFinshListener animaFinshListener) {
        if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
            throw new RuntimeException(
                    "Center point out of range or call method when View is not initialed yet.");
        }

        mClipCenterX = x;
        mClipCenterY = y;
        float maxRadius = getMaxRadius(x, y);

        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(this, "clipRadius", 0f, maxRadius);
        mAnimator.setInterpolator(new BakedBezierInterpolator());
        mAnimator.setDuration(duration);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsContentShown = true;
                if (animaFinshListener != null) animaFinshListener.onAnimFinish(animation);
            }
        });
        mAnimator.start();
    }

    public void hide(int x, int y, int duration, final AnimaFinshListener animaFinshListener) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > getWidth())
            x = getWidth();
        if (y > getHeight())
            y = getHeight();

        if (x != mClipCenterX || y != mClipCenterY) {
            mClipCenterX = x;
            mClipCenterY = y;
            mClipRadius = getMaxRadius(x, y);
        }

        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(this, "clipRadius", 0f);
        mAnimator.setInterpolator(new BakedBezierInterpolator());
        mAnimator.setDuration(duration);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsContentShown = false;
                if (animaFinshListener != null) animaFinshListener.onAnimFinish(animation);
            }
        });
        mAnimator.start();
    }

    public void next(int x, int y, int duration) {
        final int childCount = getChildCount();
        if (childCount > 1) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (i == 0) {
                    bringChildToFront(child);
                }
            }
            show(x, y, duration, null);
        }
    }

    private float getMaxRadius(int x, int y) {
        int h = Math.max(x, getWidth() - x);
        int v = Math.max(y, getHeight() - y);
        return (float) Math.sqrt(h * h + v * v);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (indexOfChild(child) == getChildCount() - 1) {
            boolean result;
            mClipPath.reset();
            mClipPath.addCircle(mClipCenterX, mClipCenterY, mClipRadius,
                    Path.Direction.CW);

            canvas.save();
            canvas.clipPath(mClipPath);
            result = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return result;
        } else {
            return super.drawChild(canvas, child, drawingTime);
        }
    }

    private MotionEvent event;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        event = ev;
        return super.onInterceptTouchEvent(ev);
    }

    public MotionEvent getEvent() {
        return event;
    }

    public interface AnimaFinshListener {
        void onAnimFinish(Animator animation);
    }
}
