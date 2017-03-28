最近在学习android的高级view的绘制，再结合值动画的数据上的改变，自己撸了个360手机助手的下载按钮。先看下原版的360手机助手的下载按钮是长啥样子吧:

![360下载按钮效果图.gif](https://github.com/1002326270xc/360Downloading-master/blob/master/photos/360下载按钮效果图.gif)

再来看看自己demo吧，你们尽情的吐槽吧，哈哈：


![修改loading状态下的运动点最高点和最低点.gif](https://github.com/1002326270xc/360Downloading-master/blob/master/photos/修改loading状态下的运动点最高点和最低点.gif)

属性也没怎么整理，就抽取出了一些比较常用的几个了:

| 属性名        | 类型           | 描述  |
| :------------- |:-------------| :-----|
| status_text_size      | dimension | 状态以及显示百分比的字体大小|
| status_text_color      | color | 状态的颜色 |
| bg_color      | color      |   整个背景的颜色 |
| progress_color | color      |    进度的颜色 |
| collect_speed | integer      |    背景收缩的时间 |
| collect_rotate_speed | integer      |    背景收缩后中间的load转一圈需要的时间 |
| expand_speed | integer      |   收缩后背景展开的时间|
| right_loading_speed | integer      |   loading状态下右边的loading每一次转动时增加的角度|
| left_loading_speed | integer      |   左边运动的几个点走一次需要的时间|

**代码使用:**
```java
 /**
 * 进度改变的方法
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
    if (onProgressUpdateListener != null) {
        onProgressUpdateListener.onChange(this.progress);
    }
    invalidate();
    if (progress == 100) {
        status = Status.Complete;
        this.stop = false;
        clearAnimation();
        loadRotateAnimation.cancel();
        movePointAnimation.cancel();
    }
}

/**
 * 暂停或继续的方法
 *
 * @param stop(true:表示暂停，false:继续)
 */
public void setStop(boolean stop) {
    if (this.stop == stop) {
        return;
    }
    this.stop = stop;
    if (stop) {
        loadRotateAnimation.cancel();
        movePointAnimation.cancel();
    } else {
        loadRotateAnimation.start();
        movePointAnimation.start();
    }
}

/**
 *设置状态的方法
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
    }
    invalidate();
}
```

**好了介绍就到这里了，如果觉得行的话，
进入github的传送门点个star吧，谢谢!!!**


### 关于我:
**email:** a1002326270@163.com

**csdn:**[enter](http://blog.csdn.net/u010429219/article/details/64922781)
