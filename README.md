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
供调用的方法有如下:

取消下载:
setCancel();

设置进度:
setProgress(int progress);

设置状态(status:Down360Loading.Status.Normal直接可以取消的操作):
setStatus(Status status);

设置暂停或继续:
setStop(stop:true表示暂停;stop:false表示继续);

接口监听:
public interface OnProgressStateChangeListener {
    void onSuccess();

    //暂停
    void onStop();

    //取消
    void onCancel();

    //继续
    void onContinue();
}

```

**好了介绍就到这里了，如果觉得行的话，点个star吧，谢谢!!!**


### 关于我:
**email:** a1002326270@163.com

**csdn:**[enter](http://blog.csdn.net/u010429219/article/details/64922781)

**简书:**[enter](http://www.jianshu.com/p/52bf13d4ca76)
