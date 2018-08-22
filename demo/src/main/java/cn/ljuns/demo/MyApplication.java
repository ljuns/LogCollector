package cn.ljuns.demo;

import android.app.Application;

import cn.ljuns.logcollector.LogCollector;

/**
 * Created by ljuns on 2018/8/18
 * I am just a developer.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        Map<String, String> map = new HashMap<>();
//        map.put("MainActivity", )

        LogCollector.getInstance(this)
                .setCleanCache(true)
                .setTag("MainActivity")
//                .setLevel(LevelUtils.D)
//                .setLogcatColors(R.color.colorAccent, R.color.colorPrimaryDark, Color.RED, Color.GREEN)
                .start();
    }
}
