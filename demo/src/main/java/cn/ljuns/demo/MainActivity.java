package cn.ljuns.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.ljuns.logcollector.LogCollector;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogCollector.getInstance()
                .setLogType(LogCollector.ERROR)
                .setCleanCache(true)
                .start(this);

        Log.d(TAG, "onCreate: ");
    }
}
