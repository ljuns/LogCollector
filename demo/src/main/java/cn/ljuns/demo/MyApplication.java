package cn.ljuns.demo;

import android.app.Application;

import cn.ljuns.logcollector.LogCollector;
import cn.ljuns.logcollector.util.LevelUtils;

/**
 * Created by ljuns on 2018/8/18
 * I am just a developer.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogCollector.getInstance(this)
                .setCleanCache(true)
//                .setTag("EGL_emulation")
                .setLevel(LevelUtils.D)
//                .setTagWithLevel("EGL_emulation", LevelUtils.D)
//                .setString("onCreate", false)
//                .setType(TypeUtils.WARN)
                .start();
    }
}
