package com.library.downloading;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by xiangcheng on 17/2/22.
 */

public class Down360Loading extends View {

    private static final String TAG = Down360Loading.class.getSimpleName();

    private boolean stop;
    private Status status = Status.Normal;

    private int width, height;
    //背景的画笔
    private Paint bgPaint;
    //白色的画笔
    private Paint textPaint;
    //即将进入到loading收缩的动画
    private Animation collectAnimator;
    //右边loading转圈的动画
    private ValueAnimator angleAnimator;
    //平移的动画
    private Animation tranlateAnimation;
    //通过当前的长度来绘制背景
    private int currentLength;
    //pre状态下几个组合点旋转角度
    private float angle;
    //loading状态下右边几个点旋转的角度
    private float loadAngle;
    //即将进入到loading状态下的平移动画
    private float translateX;
    //loading下右边几个点的loading动画
    private ValueAnimator loadRotateAnimation;
    //loading下几个运动的点的动画
    private ValueAnimator movePointAnimation;
    //定义4个运动的点
    private MovePoint[] fourMovePoint = new MovePoint[4];

    private int progress;
    //状态以及显示百分比的字体大小
    private int statusSize;
    //状态的颜色
    private int statusColor;
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

    public Down360Loading(Context context) {
        super(context);
        init();
    }

    public Down360Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArgus(context, attrs);
        init();
    }

    private void initArgus(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Down360Loading);
        try {
            statusSize = sp2px(15);
            statusSize = (int) array.getDimension(R.styleable.Down360Loading_status_text_size, statusSize);
            statusColor = Color.WHITE;
            statusColor = array.getColor(R.styleable.Down360Loading_status_text_color, statusColor);
            bgColor = Color.parseColor("#00CC99");
            bgColor = array.getColor(R.styleable.Down360Loading_bg_color, bgColor);
            progressColor = Color.parseColor("#4400CC99");
            progressColor = array.getColor(R.styleable.Down360Loading_progress_color, progressColor);
            collectSpeed = 800;
            collectSpeed = array.getInt(R.styleable.Down360Loading_collect_speed, collectSpeed);
            collectRotateSpeed = 1500;
            collectRotateSpeed = array.getInt(R.styleable.Down360Loading_collect_rotate_speed, collectRotateSpeed);
            expandSpeed = 1000;
            expandSpeed = array.getInt(R.styleable.Down360Loading_expand_speed, expandSpeed);
            rightLoadingSpeed = 5;
            rightLoadingSpeed = array.getInt(R.styleable.Down360Loading_right_loading_speed, rightLoadingSpeed);
            leftLoadingSpeed = 3000;
            leftLoadingSpeed = array.getInt(R.styleable.Down360Loading_left_loading_speed, leftLoadingSpeed);
        } finally {
            array.recycle();
        }
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(statusColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(statusSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        initAnimation();
    }

    private void initAnimation() {
        collectAnimator = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                currentLength = (int) (width - width * interpolatedTime);
                if (currentLength <= height) {
                    currentLength = height;
                    clearAnimation();
                    status = Status.Pre;
                    angleAnimator.start();
                }
                invalidate();
            }
        };
        collectAnimator.setInterpolator(new LinearInterpolator());
        collectAnimator.setDuration(collectSpeed);

        angleAnimator = ValueAnimator.ofFloat(0, 1);
        angleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle += 10;
                invalidate();

            }
        });
        angleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                status = Status.Expand;
                angleAnimator.cancel();
                startAnimation(tranlateAnimation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        angleAnimator.setDuration(collectRotateSpeed);

        tranlateAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                currentLength = (int) (height + (width - height) * interpolatedTime);
                translateX = (float) ((width * 1.0 / 2 - height * 1.0 / 2) * interpolatedTime);
                invalidate();
            }
        };
        tranlateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                status = Status.Load;
                clearAnimation();
                loadRotateAnimation.start();
                movePointAnimation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tranlateAnimation.setDuration(expandSpeed);

        loadRotateAnimation = ValueAnimator.ofFloat(0, 1);
        loadRotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                loadAngle += rightLoadingSpeed;
                if (loadAngle > 360) {
                    loadAngle = loadAngle - 360;
                }
                invalidate();
            }
        });
        loadRotateAnimation.setDuration(Integer.MAX_VALUE);

    }

    class MovePoint {
        float radius;
        float moveX;
        float moveY;
        float startX;
        boolean isDraw;

        public MovePoint(float radius, float moveX, float moveY) {
            this.radius = radius;
            this.moveX = moveX;
            this.moveY = moveY;
            this.startX = moveX;
        }
    }

    public enum Status {
        Normal, Start, Pre, Expand, Load, Complete;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (status == Status.Normal || status == Status.Start) {
            float start = (float) (width * 1.0 / 2 - currentLength * 1.0 / 2);
            canvas.drawRoundRect(start, 0, (float) (width * 1.0 / 2 + currentLength * 1.0 / 2), height, 90, 90, bgPaint);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float allHeight = fontMetrics.descent - fontMetrics.ascent;
            if (status == Status.Normal) {
                canvas.drawText("下载", (float) (width * 1.0 / 2), (float) (height * 1.0 / 2 - allHeight / 2 - fontMetrics.ascent), textPaint);
            }
        } else if (status == Status.Pre) {
            canvas.drawCircle((float) (width * 1.0 / 2), (float) (height * 1.0 / 2), (float) (height * 1.0 / 2), bgPaint);
            canvas.save();
            canvas.rotate(angle, (float) (width * 1.0 / 2), (float) (height * 1.0 / 2));
            canvas.drawCircle((float) (width * 1.0 / 2), (float) (height * 1.0 / 2), 25, textPaint);
            canvas.drawCircle((float) (width * 1.0 / 2), (float) (height * 1.0 / 2) - 24, 15, textPaint);
            canvas.drawCircle((float) (width * 1.0 / 2 - 22), (float) ((height * 1.0 / 2) + 18 * 0.866), 15, textPaint);
            canvas.drawCircle((float) (width * 1.0 / 2 + 22), (float) ((height * 1.0 / 2) + 18 * 0.866), 15, textPaint);
            canvas.restore();
        } else if (status == Status.Expand) {
            float start = (float) (width * 1.0 / 2 - currentLength * 1.0 / 2);
            canvas.drawRoundRect(start, 0, (float) (width * 1.0 / 2 + currentLength * 1.0 / 2), height, 90, 90, bgPaint);

            canvas.save();
            canvas.translate(translateX, 0);
            canvas.drawCircle((float) (width * 1.0 / 2), (float) (height * 1.0 / 2), 25, textPaint);
            canvas.drawCircle((float) (width * 1.0 / 2), (float) (height * 1.0 / 2) - 24, 15, textPaint);
            canvas.drawCircle((float) (width * 1.0 / 2 - 22), (float) ((height * 1.0 / 2) + 18 * 0.866), 15, textPaint);
            canvas.drawCircle((float) (width * 1.0 / 2 + 22), (float) ((height * 1.0 / 2) + 18 * 0.866), 15, textPaint);
            canvas.restore();
        } else if (status == Status.Load || status == Status.Complete) {

            float start = (float) (width * 1.0 / 2 - currentLength * 1.0 / 2);
            bgPaint.setColor(progressColor);
            canvas.drawRoundRect(start, 0, (float) (width * 1.0 / 2 + currentLength * 1.0 / 2), height, 90, 90, bgPaint);
            if (progress != 100) {
                //画中间的几个loading的点的情况哈
                for (int i = 0; i < fourMovePoint.length; i++) {
                    if (fourMovePoint[i].isDraw)
                        canvas.drawCircle(fourMovePoint[i].moveX, fourMovePoint[i].moveY, fourMovePoint[i].radius, textPaint);
                }
            }

            float progressRight = (float) (progress * width * 1.0 / 100);
            //在最上面画进度
            bgPaint.setColor(bgColor);

            canvas.save();
            canvas.clipRect(0, 0, progressRight, height);
            canvas.drawRoundRect(start, 0, (float) (width * 1.0 / 2 + currentLength * 1.0 / 2), height, 90, 90, bgPaint);
            canvas.restore();

            if (progress != 100) {
                bgPaint.setColor(bgColor);
                canvas.drawCircle((float) (width - height * 1.0 / 2), (float) (height * 1.0 / 2), (float) (height * 1.0 / 2), bgPaint);
                canvas.save();
                //// TODO: 17/2/22
                canvas.rotate(loadAngle, (float) (width - height * 1.0 / 2), (float) (height * 1.0 / 2));
                canvas.drawCircle(width - height + 25, getCircleY(width - height + 25), 5, textPaint);
                canvas.drawCircle(width - height + 40, getCircleY(width - height + 40), 7, textPaint);
                canvas.drawCircle(width - height + 60, getCircleY(width - height + 60), 9, textPaint);
                canvas.drawCircle(width - height + 90, getCircleY(width - height + 90), 11, textPaint);
                canvas.restore();
            }
            //中间的进度文字
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float allHeight = fontMetrics.descent - fontMetrics.ascent;
            canvas.drawText(progress + "%", (float) (width * 1.0 / 2), (float) (height * 1.0 / 2 - allHeight / 2 - fontMetrics.ascent), textPaint);
        }
    }

    /**
     * 根据x坐标算出圆的y坐标
     *
     * @param cx:点的圆心
     * @return
     */
    private float getCircleY(float cx) {
        float cy = (float) (height * 1.0 / 2 - Math.sqrt((height * 1.0 / 2 - dp2px(7)) * (height * 1.0 / 2 - dp2px(7)) - ((width - height * 1.0 / 2) - cx) * ((width - height * 1.0 / 2) - cx)));
        return cy;
    }

    /**
     * 这里是在loading情况下获取几个点运动的轨迹数学函数
     *
     * @param moveX
     * @return
     */
    private float drawMovePoints(float moveX) {
        float moveY = (float) (height / 2 + (height / 2 - fourMovePoint[3].radius) * Math.sin(4 * Math.PI * moveX / (width - height) + height / 2));
        return moveY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            if (widthMode == MeasureSpec.AT_MOST) {
                if (widthSize < 3 * getScreenWidth() / 5 * 1.0) {
                    widthSize = (int) (3 * getScreenWidth() / 5 * 1.0);
                }
            } else {
                widthSize = (int) (3 * getScreenWidth() / 5 * 1.0);
            }
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            if (heightMode == MeasureSpec.AT_MOST) {
                if (heightSize < dp2px(30)) {
                    heightSize = dp2px(30);
                }
            } else {
                heightSize = (int) (3 * getMeasuredWidth() / 5 * 1.0);
            }

        }
        width = widthSize;
        currentLength = width;
        height = heightSize;

        fourMovePoint[0] = new MovePoint(dp2px(4), (float) ((width - height / 2) * 0.88), 0);
        fourMovePoint[1] = new MovePoint(dp2px(3), (float) ((width - height / 2) * 0.85), 0);
        fourMovePoint[2] = new MovePoint(dp2px(2), (float) ((width - height / 2) * 0.80), 0);
        fourMovePoint[3] = new MovePoint(dp2px(5), (float) ((width - height / 2) * 0.75), 0);

        movePointAnimation = ValueAnimator.ofFloat(0, 1);
        movePointAnimation.setRepeatCount(ValueAnimator.INFINITE);
        movePointAnimation.setInterpolator(new LinearInterpolator());
        movePointAnimation.setDuration(leftLoadingSpeed);
        movePointAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                for (int i = 0; i < fourMovePoint.length; i++) {
                    fourMovePoint[i].moveX = fourMovePoint[i].startX - fourMovePoint[0].startX * value;
                    if (fourMovePoint[i].moveX <= height / 2) {
                        fourMovePoint[i].isDraw = false;
                    }
                    fourMovePoint[i].moveY = drawMovePoints(fourMovePoint[i].moveX);
                }
                Log.d("TAG", "fourMovePoint[0].moveX:" + fourMovePoint[0].moveX + ",fourMovePoint[0].moveY:" + fourMovePoint[0].moveY);
            }
        });

        movePointAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (int i = 0; i < fourMovePoint.length; i++) {
                    fourMovePoint[i].isDraw = true;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                for (int i = 0; i < fourMovePoint.length; i++) {
                    fourMovePoint[i].isDraw = true;
                }
            }
        });

        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    private float moveX;

    /**
     * 暂停或继续的方法
     *
     * @param stop(true:表示暂停，false:继续)
     */
    public void setStop(boolean stop) {
        if (status != Down360Loading.Status.Load) {
            return;
        }
        if (this.stop == stop) {
            return;
        }
        this.stop = stop;
        if (stop) {
            loadRotateAnimation.cancel();
            moveX = movePointAnimation.getAnimatedFraction();
            movePointAnimation.cancel();
            if (onProgressStateChangeListener != null) {
                onProgressStateChangeListener.onStop();
            }
        } else {
            loadRotateAnimation.start();
            movePointAnimation.setCurrentFraction(moveX);
            movePointAnimation.start();
            if (onProgressStateChangeListener != null) {
                onProgressStateChangeListener.onContinue();
            }
        }
    }

    public boolean isStop() {
        return stop;
    }

    public Status getStatus() {
        return status;
    }

    public void setOnProgressStateChangeListener(OnProgressStateChangeListener onProgressStateChangeListener) {
        this.onProgressStateChangeListener = onProgressStateChangeListener;
    }

    private OnProgressStateChangeListener onProgressStateChangeListener;

    public interface OnProgressStateChangeListener {
        void onSuccess();

        //暂停
        void onStop();

        //取消
        void onCancel();

        //继续
        void onContinue();
    }

    //设置取消的方法
    public void setCancel() {
        if (status == Down360Loading.Status.Load) {
            setStatus(Down360Loading.Status.Normal);
        }
    }

    /**
     * 进度改变的方法
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (status != Status.Load) {
            throw new RuntimeException("your status is not loading");
        }

        if (this.progress == progress) {
            return;
        }
        this.progress = progress;
        invalidate();
        if (progress == 100) {
            status = Status.Complete;
            this.stop = false;
            clearAnimation();
            loadRotateAnimation.cancel();
            movePointAnimation.cancel();
            if (onProgressStateChangeListener != null) {
                onProgressStateChangeListener.onSuccess();
            }
        }
    }

    /**
     * 设置状态的方法
     *
     * @param status(Down360Loading.Status.Normal:直接取消的操作)
     */
    public void setStatus(Status status) {
        if (this.status == status) {
            return;
        }
        this.status = status;
        if (this.status == Status.Normal) {
            progress = 0;
            this.stop = false;
            clearAnimation();
            loadRotateAnimation.cancel();
            movePointAnimation.cancel();
            if (onProgressStateChangeListener != null) {
                onProgressStateChangeListener.onCancel();
            }
        }
        invalidate();
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private int sp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (status != Status.Normal) {
            //如果不是normal状态下直接不让有ontouch事件
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        //抬起的时候去改变status
        if (action == MotionEvent.ACTION_UP) {
            status = Status.Start;
            startAnimation(collectAnimator);
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (loadRotateAnimation != null && loadRotateAnimation.isRunning()) {
            loadRotateAnimation.end();
        }
        if (movePointAnimation != null && movePointAnimation.isRunning()) {
            movePointAnimation.end();
        }
        if (angleAnimator != null && angleAnimator.isRunning()) {
            angleAnimator.end();
        }

        if (collectAnimator != null && collectAnimator.hasStarted()) {
            collectAnimator.cancel();
        }

        if (tranlateAnimation != null && tranlateAnimation.hasStarted()) {
            tranlateAnimation.cancel();
        }

    }
}
