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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import cn.ljuns.logcollector.util.CloseUtils;
import cn.ljuns.logcollector.util.FileUtils;
import cn.ljuns.logcollector.util.LevelUtils;
import cn.ljuns.logcollector.util.TypeUtils;

/**
 * 日志收集
 */
public class LogCollector implements CrashHandlerListener {

    private static final String UTF8 = "UTF-8";

    private Application mContext;
    /**
     * 缓存文件
     */
    private File mCacheFile;
    /**
     * 需要过滤的 TAG
     */
    private String[] mTags;
    /**
     * 需要过滤的列表
     */
    private String[] mLevels;
    /**
     * 需要过滤的字符串
     */
    private String mFilterStr;
    private String mFilterType;

    private Map<String, String> mTagWithLevel;

    /**
     * 是否过滤大小写
     */
    private boolean mIgnoreCase = false;
    /**
     * 是否清除缓存日志文件
     */
    private boolean mCleanCache = false;

    private LogRunnable mLogRunnable;

    private static volatile LogCollector sLogCollector;

    private LogCollector(Application context) {
        this.mContext = context;
        mTagWithLevel = new HashMap<>();
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
     * @param tag tag
     * @param level level
     * @return LogCollector
     */
    public LogCollector setTagWithLevel(@NonNull String tag, @LevelUtils.Level String level) {
        mTagWithLevel.put(tag, level);
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
     * 设置需要过滤的日志类型
     * @param type type
     * @return LogCollector
     */
    public LogCollector setFilterType(@TypeUtils.Type String type) {
        mFilterType = type;
        return this;
    }

    /**
     * 启动
     */
    public synchronized void start() {
        mCacheFile = FileUtils.createLogCacheFile(mContext, mCleanCache);
        CrashHandler.getInstance().init(mContext, mCleanCache).crash(this);
        mLogRunnable = new LogRunnable();
        Executors.newSingleThreadExecutor().execute(mLogRunnable);
    }

    @Override
    public void crashHandler() {
        mLogRunnable.isCrash = true;
    }

    private class LogRunnable implements Runnable {
        volatile boolean isCrash = false;

        @Override
        public void run() {
            List<String> getCommandLine = new ArrayList<>();
            List<String> cleanCommandLine = new ArrayList<>();
            createGetCommand(getCommandLine);
            createCleanCommand(cleanCommandLine);

            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {

                Runtime.getRuntime().exec(
                        cleanCommandLine.toArray(new String[cleanCommandLine.size()]));
                // 获取 logcat
                Process process = Runtime.getRuntime().exec(
                        getCommandLine.toArray(new String[getCommandLine.size()]));

                reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), UTF8));
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(mCacheFile), UTF8));

                String str;
                while (!isCrash && ((str = reader.readLine()) != null)) {
                    Runtime.getRuntime().exec(
                            cleanCommandLine.toArray(new String[cleanCommandLine.size()]));

                    if (mFilterType != null && mFilterStr != null) {
                        String result = str;
                        String filter = mFilterStr;
                        if (mIgnoreCase) {
                            result = result.toLowerCase();
                            filter = filter.toLowerCase();
                        }
                        if (!result.contains(filter)
                                && !str.contains(mFilterType + "/")) continue;
                    } else if (mFilterStr != null) {
                        String result = str;
                        String filter = mFilterStr;
                        if (mIgnoreCase) {
                            result = result.toLowerCase();
                            filter = filter.toLowerCase();
                        }
                        if (!result.contains(filter)) continue;
                    } else if (mFilterType != null) {
                        if (!str.contains(mFilterType + "/")) continue;
                    }

                    // 写数据
                    writer.write(str);
                    writer.newLine();
                    writer.flush();
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
     * 清除缓存日志
     * @param commandLine commandLine
     */
    private void createCleanCommand(List<String> commandLine) {
        commandLine.add("logcat");
        commandLine.add("-c");
    }

    /**
     * 获取日志
     * @param commandLine commandLine
     */
    private void createGetCommand(List<String> commandLine) {
        commandLine.add("logcat");
        commandLine.add("-b");
        commandLine.add("main");
        commandLine.add("-v");
        commandLine.add("time");

        // 过滤 TAG
        if (mTags != null && mTags.length > 0) {
            commandLine.add("-s");
            commandLine.addAll(Arrays.asList(mTags));
        }

        // 过滤字符串
//        if (mFilterStr != null) {
//            commandLine.add("sh");
//            commandLine.add("-c");
//
//            commandLine.add("| grep");
//            if (mIgnoreCase) {
//                commandLine.add("-i");
//            }
//            commandLine.add(mFilterStr);
//        }

//        if (mFilterType != null) {
//            commandLine.add(" | grep ");
//            String type = "\"^..................." + mFilterType + "\"";
//            commandLine.add(type);
//        }

        // 过滤类别
        if (mLevels != null && mLevels.length > 0) {
            commandLine.add("sh");
            commandLine.add("-c");
            for (String level : mLevels) {
                commandLine.add("*:" + level);
            }
        }

        // 过滤 tag:level
        if (!mTagWithLevel.isEmpty()) {
            for (Map.Entry<String, String> entry : mTagWithLevel.entrySet()) {
                commandLine.add(entry.getKey() + ":" + entry.getValue());
            }

            /**
             * 没有 tag 和 level 的时候想要 tag:level 生效就得再加上 *:S，
             * 再加上 *:S 意思是只让 tag:level 生效
             */
            boolean addCommand = (mTags == null || mTags.length == 0) &&
                    (mLevels == null || mLevels.length == 0);
            if (addCommand) {
                commandLine.add("*:S");
            }
        }
    }
}