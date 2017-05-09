package com.library.downloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by xiangcheng on 17/5/9.
 */

public class Down360ViewGroup extends RelativeLayout {
    private Down360LoadingView down360LoadingView;
    private ImageView cancelImg;
    private ImageView stopContinueImg;

    public Down360ViewGroup(Context context) {
        this(context, null);
    }

    public Down360ViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Down360ViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Down360LoadingView);
        Drawable cancelBack = array.getDrawable(R.styleable.Down360LoadingView_cancel_back_icon);
        final Drawable continueBack = array.getDrawable(R.styleable.Down360LoadingView_continue_back_icon);
        final Drawable stopBack = array.getDrawable(R.styleable.Down360LoadingView_stop_back_icon);
        View.inflate(context, R.layout.down_viewgroup_layout, this);
        down360LoadingView = (Down360LoadingView) findViewById(R.id.down_load);
        cancelImg = (ImageView) findViewById(R.id.cancel_img);
        stopContinueImg = (ImageView) findViewById(R.id.stop_img);
        cancelImg.setImageDrawable(cancelBack);
        stopContinueImg.setImageDrawable(continueBack);
        cancelImg.setVisibility(View.GONE);
        stopContinueImg.setVisibility(View.GONE);
        cancelImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                down360LoadingView.setCancel();
                cancelImg.setVisibility(View.GONE);
                stopContinueImg.setVisibility(View.GONE);
            }
        });
        stopContinueImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStop()) {
                    down360LoadingView.setStop(false);
                    stopContinueImg.setImageDrawable(continueBack);
                } else {
                    down360LoadingView.setStop(true);
                    stopContinueImg.setImageDrawable(stopBack);
                }
            }
        });

    }

    public boolean isStop() {
        return down360LoadingView.isStop();
    }

    public void setProgress(int progress) {
        down360LoadingView.setProgress(progress);
        if (progress > 0) {
            if (stopContinueImg.getVisibility() == View.GONE && cancelImg.getVisibility() == View.GONE) {
                stopContinueImg.setVisibility(View.VISIBLE);
                cancelImg.setVisibility(View.VISIBLE);
            }
        }
    }

    public Down360LoadingView.Status getStatus() {
        return down360LoadingView.getStatus();
    }
}
