package com.library.downloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
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
    private Drawable cancelBack;
    private Drawable continueBack;
    private Drawable stopBack;
    //状态以及显示百分比的字体大小
    private int statusSize;
    //状态的颜色
    private int statusColor;

    private int loadPointColor;
    //整个背景的颜色
    private int bgColor;
    //进度的颜色
    private int progressColor;
    //背景收缩的时间
    private int collectSpeed;
    //背景收缩后中间的load转一圈需要的时间
    private int collectRotateSpeed;
    //收缩后背景展开的时间
    private int expandSpeed;
    //loading状态下右边的loading每一次转动时增加的角度
    private int rightLoadingSpeed;
    //左边运动的几个点走一次需要的时间
    private int leftLoadingSpeed;

    public Down360ViewGroup(Context context) {
        this(context, null);
    }

    public Down360ViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Down360ViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArgus(context, attrs);
        View.inflate(context, R.layout.down_viewgroup_layout, this);
        down360LoadingView = (Down360LoadingView) findViewById(R.id.down_load);
        Down360LoadingView.ArguParams arguParams = new Down360LoadingView.ArguParams();
        arguParams.statusSize = statusSize;
        arguParams.statusColor = statusColor;
        arguParams.loadPointColor = loadPointColor;
        arguParams.bgColor = bgColor;
        arguParams.progressColor = progressColor;
        arguParams.collectSpeed = collectSpeed;
        arguParams.collectRotateSpeed = collectRotateSpeed;
        arguParams.expandSpeed = expandSpeed;
        arguParams.rightLoadingSpeed = rightLoadingSpeed;
        arguParams.leftLoadingSpeed = leftLoadingSpeed;
        down360LoadingView.setArgus(arguParams);
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

    private void initArgus(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Down360LoadingView);
        try {
            cancelBack = array.getDrawable(R.styleable.Down360LoadingView_cancel_back_icon);
            continueBack = array.getDrawable(R.styleable.Down360LoadingView_continue_back_icon);
            stopBack = array.getDrawable(R.styleable.Down360LoadingView_stop_back_icon);
            if (cancelBack == null || continueBack == null || stopBack == null) {
                throw new IllegalArgumentException("your cancel_back_icon is null or stop_back_icon is null or continue_back_icon");
            }
            statusSize = sp2px(15);
            statusSize = (int) array.getDimension(R.styleable.Down360LoadingView_status_text_size, statusSize);
            statusColor = Color.WHITE;
            statusColor = array.getColor(R.styleable.Down360LoadingView_status_text_color, statusColor);
            loadPointColor = Color.WHITE;
            loadPointColor = array.getColor(R.styleable.Down360LoadingView_load_point_color, loadPointColor);
            bgColor = Color.parseColor("#00CC99");
            bgColor = array.getColor(R.styleable.Down360LoadingView_bg_color, bgColor);
            progressColor = Color.parseColor("#4400CC99");
            progressColor = array.getColor(R.styleable.Down360LoadingView_progress_color, progressColor);
            collectSpeed = 800;
            collectSpeed = array.getInt(R.styleable.Down360LoadingView_collect_speed, collectSpeed);
            collectRotateSpeed = 1500;
            collectRotateSpeed = array.getInt(R.styleable.Down360LoadingView_collect_rotate_speed, collectRotateSpeed);
            expandSpeed = 1000;
            expandSpeed = array.getInt(R.styleable.Down360LoadingView_expand_speed, expandSpeed);
            rightLoadingSpeed = 7;
            rightLoadingSpeed = array.getInt(R.styleable.Down360LoadingView_right_loading_speed, rightLoadingSpeed);
            leftLoadingSpeed = 2000;
            leftLoadingSpeed = array.getInt(R.styleable.Down360LoadingView_left_loading_speed, leftLoadingSpeed);
        } finally {
            array.recycle();
        }
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

    public void setOnProgressStateChangeListener(Down360LoadingView.OnProgressStateChangeListener onProgressStateChangeListener) {
        down360LoadingView.setOnProgressStateChangeListener(onProgressStateChangeListener);
    }

    public Down360LoadingView.Status getStatus() {
        return down360LoadingView.getStatus();
    }

    private int sp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }
}
