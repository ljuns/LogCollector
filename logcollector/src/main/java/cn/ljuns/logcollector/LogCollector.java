package cn.ljuns.logcollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogCollector {

    public static final String VERBOSE = "-v";
    public static final String DEBUG = "-d";
    public static final String INFO = "-i";
    public static final String WARN = "-w";
    public static final String ERROR = "-e";
    public static final String ASSERT = "-a";

    private static final String DEFAULT_FORMAT = "yyyyMMdd_HHmmss_SSS";

    @StringDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    public @interface LogType{}

    private File mCacheFile;
    private String mLogType;
    private boolean mCleanCache = false;

    private LogCollector(){}

    public static LogCollector getInstance() {
        return new LogCollector();
    }

    /**
     * 设置缓存文件
     * @param file
     * @return
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
     * @param type 缓存类型
     * @return
     */
    public LogCollector setLogType(@LogType String type) {
        this.mLogType = type;
        return this;
    }

    /**
     * 是否清除之前的缓存
     * @param cleanCache 是否清除
     * @return
     */
    public LogCollector setCleanCache(boolean cleanCache) {
        this.mCleanCache = cleanCache;
        return this;
    }

    @SuppressLint("SimpleDateFormat")
    public void start(Context context) {
        // 缓存文件
        DateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
        String fileName = format.format(new Date(System.currentTimeMillis())) + ".txt";
        String path = context.getApplicationContext().getExternalCacheDir() + "/log";

        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 计算缓存日志大小
        computeSize(folder);

        // 是否删除缓存日志
        if (mCleanCache) {
            cleanCache(folder);
        }

        // 初始化缓存文件
        initCacheFile(fileName, folder);

        if (mLogType == null) {
            mLogType = "-v";
        }

        new Thread(mLogRunnable).start();
    }

    /**
     * 初始化缓存文件
     * @param fileName 缓存文件名
     * @param folder 文件夹
     */
    private void initCacheFile(String fileName, File folder) {
        if (mCacheFile == null) {
            mCacheFile = new File(folder,  fileName);
            try {
                mCacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mCacheFile.exists()) {
                mCacheFile.delete();
            }
        }
    }

    /**
     * 删除缓存文件
     * @param folder 文件夹
     */
    private void cleanCache(File folder) {
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }

    /**
     * 获取缓存大小
     * @param folder log 文件夹
     */
    private void computeSize(File folder) {
        long length = 0L;
        if (folder.exists()) {
            for (File file1 : folder.listFiles()) {
                length += file1.length();
            }
        }

        //限定大小 10M
        if (length / 1024 / 1024 >= 10) {
            cleanCache(folder);
        }
    }

    private Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            FileOutputStream os = null;
            try {
                List<String> list = new ArrayList<>();
                list.add("logcat");
                list.add(mLogType);
                if (VERBOSE.equals(mLogType)) {
                    list.add("time");
                }

                Process process = Runtime.getRuntime().exec(list.toArray(new String[list.size()]));
                InputStream in = process.getInputStream();

                os = new FileOutputStream(mCacheFile);
                int len;
                byte[] buf = new byte[1024];
                while (((len = in.read(buf)) != -1)) {
                    os.write(buf, 0, len);
                    os.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
