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
    private static volatile LogCollector sLogCollector;
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
    public LogCollector setCacheFile(@NonNull File file) {
        this.mCacheFile = file;
        return this;
    }

    public LogCollector setCacheFile(@NonNull String path) {
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
     *
     * @param tags tags
     * @return LogCollector
     */
    public LogCollector setTag(@NonNull String... tags) {
        this.mTags = tags;
        return this;
    }

    /**
     * 设置需要过滤的类型
     *
     * @param levels levels
     * @return LogCollector
     */
    public LogCollector setLevel(@LevelUtils.Level String... levels) {
        this.mLevels = levels;
        return this;
    }

    /**
     * 设置需要过滤的 tag:level
     *
     * @param tag   tag
     * @param level level
     * @return LogCollector
     */
    public LogCollector setTagWithLevel(@NonNull String tag, @LevelUtils.Level String level) {
        this.mTagWithLevel.put(tag, level);
        return this;
    }

    /**
     * 设置需要过滤的字符串，默认区分大小写
     *
     * @param str str
     * @return LogCollector
     */
    public LogCollector setString(@NonNull String str) {
        return setString(str, false);
    }

    /**
     * 设置需要过滤的字符串
     *
     * @param str        str
     * @param ignoreCase ignoreCase
     * @return LogCollector
     */
    public LogCollector setString(@NonNull String str, boolean ignoreCase) {
        this.mFilterStr = str;
        this.mIgnoreCase = ignoreCase;
        return this;
    }

    /**
     * 设置需要过滤的日志类型
     *
     * @param type type
     * @return LogCollector
     */
    public LogCollector setType(@TypeUtils.Type String type) {
        this.mFilterType = type;
        return this;
    }

    /**
     * 设置需要过滤的字符串和日志类型，默认区分大小写
     *
     * @param str  str
     * @param type type
     * @return LogCollector
     */
    public LogCollector setStringWithType(@NonNull String str, @TypeUtils.Type String type) {
        return setStringWithType(str, type, false);
    }

    /**
     * 设置需要过滤的字符串和日志类型
     *
     * @param str        str
     * @param type       type
     * @param ignoreCase ignoreCase
     * @return LogCollector
     */
    public LogCollector setStringWithType(@NonNull String str, @TypeUtils.Type String type, boolean ignoreCase) {
        this.mFilterStr = str;
        this.mFilterType = type;
        this.mIgnoreCase = ignoreCase;
        return this;
    }

    /**
     * 启动
     */
    public synchronized void start() {
        mCacheFile = FileUtils.createLogCacheFile(mContext, mCacheFile, mCleanCache);
        CrashHandler.getInstance().init(mContext, mCleanCache).crash(this);

        mLogRunnable = new LogRunnable();
        Executors.newSingleThreadExecutor().execute(mLogRunnable);
    }

    @Override
    public void crashHandler() {
        mLogRunnable.isCrash = true;
    }

    /**
     * 过滤字符串和日志类别
     *
     * @param str str
     * @return boolean
     */
    private boolean filterStringType(String str) {
        if (mFilterType != null && mFilterStr != null) {
            String result = str;
            String filter = mFilterStr;
            if (mIgnoreCase) {
                result = result.toLowerCase();
                filter = filter.toLowerCase();
            }
            return !result.contains(filter)
                    && !str.contains(mFilterType + "/");
        } else if (mFilterStr != null) {
            String result = str;
            String filter = mFilterStr;
            if (mIgnoreCase) {
                result = result.toLowerCase();
                filter = filter.toLowerCase();
            }
            return !result.contains(filter);
        } else if (mFilterType != null) {
            return !str.contains(mFilterType + "/");
        }
        return false;
    }

    /**
     * 清除缓存日志
     */
    private void createCleanCommand() throws IOException {
        List<String> commandLine = new ArrayList<>();
        commandLine.add("logcat");
        commandLine.add("-c");
        Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
    }

    /**
     * 获取日志
     *
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

    private class LogRunnable implements Runnable {
        volatile boolean isCrash = false;

        @Override
        public void run() {
            List<String> getCommandLine = new ArrayList<>();
            createGetCommand(getCommandLine);

            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                createCleanCommand();
                // 获取 logcat
                Process process = Runtime.getRuntime().exec(
                        getCommandLine.toArray(new String[getCommandLine.size()]));

                reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), UTF8));
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(mCacheFile), UTF8));

                String str;
                while (!isCrash && ((str = reader.readLine()) != null)) {
                    createCleanCommand();
                    if (filterStringType(str)) { continue; }

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
}