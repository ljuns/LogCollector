package cn.ljuns.logcollector;

import android.app.Application;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.ljuns.logcollector.util.CloseUtils;
import cn.ljuns.logcollector.util.FileUtils;

/**
 * Created by ljuns on 2018/8/15
 * I am just a developer.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler sCrashHandler = new CrashHandler();
    private Application mContext;
    private boolean mCleanCache;
    private CrashHandlerListener mCrashHandlerListener;
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private CrashHandler() {}

    public static CrashHandler getInstance() {
        return sCrashHandler;
    }

    public CrashHandler init(Application context, boolean cleanCache) {
        this.mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.mContext = context;
        this.mCleanCache = cleanCache;

        return this;
    }

    public void crash(CrashHandlerListener listener) {
        mCrashHandlerListener = listener;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        mCrashHandlerListener.crashHandler();
        dumpExceptionToCacheFile(e);

        e.printStackTrace();

        // 系统是否提供了默认的异常处理器
        if (mUncaughtExceptionHandler != null) {
            mUncaughtExceptionHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 缓存文件
     *
     * @param e
     */
    private void dumpExceptionToCacheFile(Throwable e) {
        File file = FileUtils.createCrashCacheFile(mContext, mCleanCache);

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            printWriter.println();
            e.printStackTrace(printWriter);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            CloseUtils.close(printWriter);
        }
    }
}
