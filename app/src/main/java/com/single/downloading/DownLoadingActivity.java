package com.single.downloading;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.library.downloading.Down360LoadingView;
import com.library.downloading.Down360ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiangcheng on 17/5/9.
 */

public class DownLoadingActivity extends AppCompatActivity implements Down360LoadingView.OnProgressStateChangeListener {
    private static final String TAG = DownLoadingActivity.class.getSimpleName();
    //模拟进度的计时器
    private Timer timer;
    private int progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_loading_layout);
        final Down360ViewGroup loading = (Down360ViewGroup) findViewById(R.id.down_loading_viewgroup);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!loading.isStop()) {
                            if (loading.getStatus() == Down360LoadingView.Status.Load) {
                                progress++;
                                Log.d(TAG, "DownActivity:" + progress);
                                loading.setProgress(progress);
                            }
                        }
                    }
                });
            }
        }, 0, 500);
        loading.setOnProgressStateChangeListener(this);
    }

    @Override
    public void onSuccess() {
        timer.cancel();
        Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        progress = 0;
    }

    @Override
    public void onContinue() {
    }

    @Override
    public void onStopDown() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
