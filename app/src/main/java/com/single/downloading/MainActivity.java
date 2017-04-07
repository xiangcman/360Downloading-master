package com.single.downloading;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.library.downloading.Down360Loading;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Down360Loading.OnProgressUpdateListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //模拟进度的计时器
    private Timer timer;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down_layout);
        final Down360Loading loading = (Down360Loading) findViewById(R.id.loading);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!loading.isStop()) {
                            if (loading.getStatus() == Down360Loading.Status.Load) {
                                progress++;
                                Log.d(TAG, "DownActivity:" + progress);
                                loading.setProgress(progress);
                            }
                        }
                    }
                });
            }
        }, 0, 500);
        loading.setOnProgressUpdateListener(this);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loading.getStatus() == Down360Loading.Status.Load) {
                    progress = 0;
                    loading.setStatus(Down360Loading.Status.Normal);
                    ((Button) findViewById(R.id.stop)).setText("暂停");
                }
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loading.getStatus() == Down360Loading.Status.Load) {
                    boolean stop = loading.isStop();
                    loading.setStop(!stop);
                    ((Button) findViewById(R.id.stop)).setText(stop ? "继续" : "暂停");
                }
            }
        });

    }

    @Override
    public void onChange(int progress) {
        if (progress >= 100) {
            //do something
            progress = 100;
            timer.cancel();
            Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
        }
    }
}
