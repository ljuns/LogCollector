package cn.ljuns.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.ljuns.logcollector.LogCollector;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogCollector.getInstance()
//                .setLogType(TagUtils.DEBUG)
                .setCleanCache(true)
                .start(this);

        Log.d(TAG, "onCreate: ");

        try {
            Thread.sleep(5000);
            throw new RuntimeException("自定义的异常");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
