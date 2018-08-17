package cn.ljuns.logcollector;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LogCollector {

    private File mCacheFile;    // 缓存文件
    private String[] mLogType;    //过滤类型
    private String mBgColor = ColorUtils.BLACK_COLOR;    // 背景颜色
//    private String[] mLogcatColors; // 默认的颜色集合
    private boolean mCleanCache = false;    // 是否清除缓存日志文件
    private boolean mShowLogColors = true;  // 是否设置颜色

    private LogCollector() {}

    private static class SingletonHolder {
        private static final LogCollector INSTANCE = new LogCollector();
    }

    public static LogCollector getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 设置缓存文件
     *
     * @param file file
     * @return LogCollector
     */
    private LogCollector setCacheFile(@NonNull File file) {
        this.mCacheFile = file;
        return this;
    }

    private LogCollector setCacheFile(@NonNull String path) {
        this.mCacheFile = new File(path);
        return this;
    }

    /**
     * 设置缓存类型
     *
     * @param types LogType
     * @return LogCollector
     */
    public LogCollector setLogType(@TagUtils.LogType String... types) {
        this.mLogType = types;
        return this;
    }

    /**
     * 是否清除之前的缓存
     *
     * @param cleanCache cleanCache
     * @return LogCollector
     */
    public LogCollector setCleanCache(boolean cleanCache) {
        this.mCleanCache = cleanCache;
        return this;
    }

    /**
     * 设置背景颜色
     * @param bgColor bgColor
     * @return LogCollector
     */
    public LogCollector setBgColor(@ColorUtils.BgColor String bgColor) {
        this.mBgColor = bgColor;
        if (bgColor.equals(ColorUtils.WHITE_COLOR)) {
            mShowLogColors = false;
        }
        return this;
    }

    /**
     * 设置各种 logcat 颜色
     * @param verboseColor verboseColor
     * @return LogCollector
     */
    private LogCollector setColors(String verboseColor) {
        // TODO: 2018/8/10  
        return this;
    }

    /**
     * 启动
     * @param context Context
     */
    public synchronized void start(Context context) {
        mCacheFile = CacheFile.createLogCacheFile(context, mCleanCache);
        CrashHandler.getInstance().crash(context, mCleanCache);
        new Thread(new LogRunnable()).start();
    }

    private class LogRunnable implements Runnable {
        @Override
        public void run() {
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                // 获取 logcat
                Process process = Runtime.getRuntime().exec(new String[]{"logcat", "-v", "time"});

                reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8"));
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(mCacheFile), "UTF-8"));

                String str = null;
                writer.write(" <body bgcolor=\" " + mBgColor + " \">");
                while (((str = reader.readLine()) != null)) {
                    Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
                    outputLogcat(writer, str);
                }
                writer.write("</body>");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                CloseUtils.close(reader);
                CloseUtils.close(writer);
            }
        }
    }

    /**
     * 输出 logcat
     * @param writer BufferedWriter
     * @param str str
     * @throws IOException
     */
    private void outputLogcat(BufferedWriter writer, String str) throws IOException {
        if (mLogType != null && mLogType.length > 0) {
            for (String type : mLogType) {
                if (str.contains(type)) {
                    write(writer, ColorUtils.COLORS[TagUtils.getIndex(mLogType[0])], str);
                }
            }
        } else {
            for (int i = 0; i < TagUtils.TAGS.length; i++) {
                if (str.contains(TagUtils.TAGS[i])) {
                    write(writer, mShowLogColors ? ColorUtils.COLORS[i] : "", str);
                }
            }
        }
    }

    /**
     * 写数据
     * @param writer BufferedWriter
     * @param color color
     * @param str str
     * @throws IOException
     */
    private void write(BufferedWriter writer, String color, String str) throws IOException {
        writer.write("<font size=\"3\" color=\"" + color + "\">" + str + "</font></br>");
        writer.newLine();
        writer.flush();
    }
}