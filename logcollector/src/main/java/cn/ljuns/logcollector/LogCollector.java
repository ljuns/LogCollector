package cn.ljuns.logcollector;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogCollector {

    private static final String DEFAULT_FORMAT = "yyyyMMdd_HHmmss_SSS";

    private File mCacheFile;    //缓存文件
    private String mLogType;    //过滤类型
    private String mBgColor;    //背景颜色
    private String[] mLogcatColors; //默认的颜色集合
    private boolean mCleanCache = false;    //是否清除缓存日志文件
    private boolean mHasColors = true;  //是否设置颜色

    private LogCollector() {
        mLogcatColors = new String[]{"#ED008C", "#00FFOO", "#00FFFF", "#FFFFOO", "#FF00OO", "#00AEEF", "#FDC68C"};
        mBgColor = "#000000";
    }

    public static LogCollector getInstance() {
        return new LogCollector();
    }

    /**
     * 设置缓存文件
     *
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
     *
     * @param type 缓存类型
     * @return
     */
    public LogCollector setLogType(@TagUtils.LogType String type) {
        this.mLogType = type;
        return this;
    }

    /**
     * 是否清除之前的缓存
     *
     * @param cleanCache 是否清除
     * @return
     */
    public LogCollector setCleanCache(boolean cleanCache) {
        this.mCleanCache = cleanCache;
        return this;
    }

    /**
     * 是否显示颜色
     * @param hasColors
     * @return
     */
    public LogCollector setHasColors(boolean hasColors) {
        this.mHasColors = hasColors;
        return this;
    }

    /**
     * 设置背景颜色
     * @param bgColor
     * @return
     */
    public LogCollector setBgColor(String bgColor) {
        this.mBgColor = bgColor;
        return this;
    }

    /**
     * 设置各种 logcat 颜色
     * @param verboseColor
     * @return
     */
    public LogCollector setColors(String verboseColor) {
        // TODO: 2018/8/10  
        return this;
    }

    public void start(Context context) {
        createCacheFile(context);
        new Thread(mLogRunnable).start();
    }

    private Runnable mLogRunnable = new Runnable() {
        @Override
        public void run() {
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                // 获取 logcat
                String[] logcat = new String[]{"logcat", "-v", "time"};
                Process process = Runtime.getRuntime().exec(logcat);

                reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8"));
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(mCacheFile), "UTF-8"));

                if (!mHasColors) {
                    for (int i = 0; i < mLogcatColors.length; i++) {
                        mLogcatColors[i] = "";
                    }
                    mBgColor = "#FFFFFF";
                }

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
    };

    /**
     * 输出 logcat
     * @param writer
     * @param str
     * @throws IOException
     */
    private void outputLogcat(BufferedWriter writer, String str) throws IOException {
        if (mLogType != null) {
            if (str.contains(mLogType)) {
                write(writer, mLogcatColors[1], str);
            }
        } else {
            if (str.contains(TagUtils.VERBOSE)) {
                write(writer, mLogcatColors[0], str);
            } else if (str.contains(TagUtils.DEBUG)) {
                write(writer, mLogcatColors[1], str);
            } else if (str.contains(TagUtils.INFO)) {
                write(writer, mLogcatColors[2], str);
            } else if (str.contains(TagUtils.WARN)) {
                write(writer, mLogcatColors[3], str);
            } else if (str.contains(TagUtils.ERROR)) {
                write(writer, mLogcatColors[4], str);
            } else if (str.contains(TagUtils.ASSERT)) {
                write(writer, mLogcatColors[5], str);
            } else {
                write(writer, mLogcatColors[6], str);
            }
        }
    }

    /**
     * 写数据
     * @param writer
     * @param color
     * @param str
     * @throws IOException
     */
    private void write(BufferedWriter writer, String color, String str) throws IOException {
        writer.write("<font size=\"3\" color=\"" + color + "\">" + str + "</font></br>");
        writer.newLine();
    }

    /**
     * 创建缓存文件
     * @param context
     */
    private void createCacheFile(Context context) {
        // 缓存文件
        DateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
        String fileName = format.format(new Date(System.currentTimeMillis())) + ".html";

        String path = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            path = context.getApplicationContext().getExternalCacheDir() + "/log";
        } else {
            path = context.getApplicationContext().getCacheDir() + "/log";
        }

        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 计算缓存日志大小
        computeSize(folder);
        // 初始化
        initCacheFile(fileName, folder);
    }

    /**
     * 初始化缓存文件
     *
     * @param fileName 缓存文件名
     * @param folder   文件夹
     */
    private void initCacheFile(String fileName, File folder) {
        // 是否删除缓存日志文件
        if (mCleanCache) {
            cleanCache(folder);
        }

        if (mCacheFile == null) {
            mCacheFile = new File(folder, fileName);
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
     *
     * @param folder 文件夹
     */
    private void cleanCache(File folder) {
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }

    /**
     * 获取缓存大小
     *
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
}
