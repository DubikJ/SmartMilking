package com.avatlantik.smartmilking.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.avatlantik.smartmilking.R;

public class LoadMilkView extends android.support.v7.widget.AppCompatImageView {

    private Animation animationScan;

    public LoadMilkView(Context context) {
        super(context);
        init(context);
    }

    public LoadMilkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMilkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        animationScan = AnimationUtils.loadAnimation(context, R.anim.scan);
        animationScan.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == GONE || visibility == INVISIBLE) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    private void startAnimation() {
        startAnimation(animationScan);
    }

    private void stopAnimation() {
        this.clearAnimation();
    }
}