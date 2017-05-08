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

import static com.single.downloading.R.id.stop;

public class MainActivity extends AppCompatActivity implements Down360Loading.OnProgressStateChangeListener {
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
        loading.setOnProgressStateChangeListener(this);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setCancel();
            }
        });

        findViewById(stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean stop = loading.isStop();
                loading.setStop(!stop);
            }
        });

    }

    @Override
    public void onSuccess() {
        timer.cancel();
        Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        progress = 0;
        ((Button) findViewById(stop)).setText("暂停");
    }

    @Override
    public void onContinue() {
        ((Button) findViewById(stop)).setText("暂停");
    }

    @Override
    public void onStop() {
        ((Button) findViewById(stop)).setText("继续");
    }
    
      @Override
    protected void onDestroy() {
        timer.cancel();
    }
}
