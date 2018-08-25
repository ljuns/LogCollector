package cn.ljuns.demo;

import android.app.Application;

import cn.ljuns.logcollector.LogCollector;
import cn.ljuns.logcollector.util.TypeUtils;

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
//                .setTag("MainActivity")
//                .setLevel(LevelUtils.W)
//                .setTagWithLevel("EGL_emulation", LevelUtils.D)
                .setFilterStr("onCreate", false)
                .setFilterType(TypeUtils.WARN)
                .start();
    }
}
