package cn.ljuns.logcollector;

import android.app.Application;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LogCollector implements CrashHandlerListener {

    private static final int LOGCAT_TYPE_COUNT = 6;

    private Application mContext;
    private File mCacheFile;    // 缓存文件

    private String[] mLogcatColors;
    private String[] mTags; // 需要过滤的 TAG
    private String[] mLevels;   // 需要过滤的列表
    private String mFilterStr;  // 需要过滤的字符串
    private String mBgColor = "#FFFFFFFF";    // 背景颜色

    private Map<String, String> mTagWithLevel;

    private boolean mIgnoreCase = false;    // 是否过滤大小写
    private boolean mCleanCache = false;    // 是否清除缓存日志文件
    private boolean mShowLogColors = false;  // 是否设置颜色

    private LogRunnable mLogRunnable;

    private static volatile LogCollector sLogCollector;

    private LogCollector(Application context) {
        this.mContext = context;
        mLogcatColors = new String[LOGCAT_TYPE_COUNT];
    }

    public static LogCollector getInstance(Application context) {
        if (sLogCollector == null) {
            synchronized (LogCollector.class) {
                if (sLogCollector == null) {
                    sLogCollector = new LogCollector(context);
                }
            }
        }
        return sLogCollector;
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
    private LogCollector setBgColor(int bgColor) {
//        this.mBgColor = ColorUtils.parseColor(bgColor);
        return this;
    }

    /**
     * 设置各种 logcat 颜色
     * @param logcatColors logColors
     * @return LogCollector
     */
    public LogCollector setLogcatColors(@NonNull int... logcatColors) {
        for (int i = 0; i < logcatColors.length; i++) {
            mLogcatColors[i] = ColorUtils.parseColor(mContext, logcatColors[i]);
        }
        mShowLogColors = true;

        for (String color : mLogcatColors) {
            System.out.println(color);
        }

        if (mLogcatColors.length < LOGCAT_TYPE_COUNT) {
            for (int i = mLogcatColors.length; i < LOGCAT_TYPE_COUNT; i++) {
                mLogcatColors[i] = "#FF000000";
            }
        }

        return this;
    }

    /**
     * 设置需要过滤的 TAG
     * @param tags tags
     * @return LogCollector
     */
    public LogCollector setTag(@NonNull String... tags) {
        mTags = tags;
        return this;
    }

    /**
     * 设置需要过滤的类型
     * @param levels levels
     * @return LogCollector
     */
    public LogCollector setLevel(@LevelUtils.Level String... levels) {
        mLevels = levels;
        return this;
    }

    /**
     * 设置需要过滤的 tag:level
     * @param tagWithLevel tagWithLevel
     * @return LogCollector
     */
    public LogCollector setTagWithLevel(@NonNull Map<String, String> tagWithLevel) {
        mTagWithLevel = tagWithLevel;
        return this;
    }

    /**
     * 设置需要过滤的字符串，区分大小写
     * @param str str
     * @param ignoreCase ignoreCase
     * @return LogCollector
     */
    public LogCollector setFilterStr(@NonNull String str, boolean ignoreCase) {
        mFilterStr = str;
        mIgnoreCase = ignoreCase;
        return this;
    }

    /**
     * 启动
     */
    public synchronized void start() {
        mCacheFile = CacheFile.createLogCacheFile(mContext, mCleanCache, mShowLogColors);
        CrashHandler.getInstance().init(mContext, mCleanCache).crash(this);
        mLogRunnable = new LogRunnable();
        new Thread(mLogRunnable).start();
    }

    @Override
    public void crashHandler() {
        mLogRunnable.isCrash = true;
    }

    private class LogRunnable implements Runnable {
        volatile boolean isCrash = false;

        @Override
        public void run() {
            List<String> commandLine = new ArrayList<>();

            commandLine.add("logcat");
            commandLine.add("-v");
            commandLine.add("time");

            if (mTags != null && mTags.length > 0) {
                commandLine.add("-s");
                commandLine.addAll(Arrays.asList(mTags));
            }

            if (mFilterStr != null) {
                if (mIgnoreCase) {
                    commandLine.add("-i");
                }
                commandLine.add(mFilterStr);
            }

            if (mLevels != null && mLevels.length > 0) {
                for (String level : mLevels) {
                    commandLine.add("*:" + level);
                }
            }

            if (mTagWithLevel != null && !mTagWithLevel.isEmpty()) {
                for (Map.Entry<String, String> entry : mTagWithLevel.entrySet()) {
                    commandLine.add(entry.getKey());
                    commandLine.add(":");
                    commandLine.add(entry.getValue());
                }

                // 没有 tag 和 level 的时候想要 tag:level 生效就得再加 *:S
                // 再加上 *:S 意思是只让 tag:level 生效
                if ((mTags == null || mTags.length == 0)
                        || (mLevels == null || mLevels.length == 0)) {
                    commandLine.add("*:S");
                }
            }

            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {

                Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
                // 获取 logcat
                Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));

                reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8"));
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(mCacheFile), "UTF-8"));

                String str;

                if (mShowLogColors) {
                    writer.write("<body bgcolor=\" " + mBgColor + " \">");
                }
                while (!isCrash && ((str = reader.readLine()) != null)) {
                    Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
                    outputLogcat(writer, str);
                }
                if (mShowLogColors) {
                    writer.write("</body>");
                }

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
        String[] logcats = null;

        if (mShowLogColors) {
            for (int i = 0; i < logcats.length; i++) {
                if (str.contains(logcats[i])) {
                    writeWithColors(writer, mLogcatColors[i], str);
                }
            }
        } else {
            writeWithoutColors(writer, str);
        }
    }

    /**
     * 写数据
     * @param writer BufferedWriter
     * @param str str
     * @throws IOException
     */
    private void writeWithoutColors(BufferedWriter writer, String str) throws IOException {
        writer.write(str);
        flush(writer);
    }


    /**
     * 写数据
     * @param writer BufferedWriter
     * @param color color
     * @param str str
     * @throws IOException
     */
    private void writeWithColors(BufferedWriter writer, String color, String str) throws IOException {
        writer.write("<font size=\"3\" color=\"" + color + "\">" + str + "</font></br>");
        flush(writer);
    }

    private void flush(BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.flush();
    }
}