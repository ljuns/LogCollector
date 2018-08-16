package cn.ljuns.logcollector;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ljuns on 2018/8/15
 * I am just a developer.
 * 缓存文件
 */
public class CacheFile {

    private static final String DEFAULT_FORMAT = "yyyyMMdd_HHmmss_SSS";
    private static final String LOG = "log";
    private static final String CRASH = "crash";
    private static final String HTML = ".html";
    private static final String TXT = ".txt";


    /**
     * 文件名
     * @return 返回文件名
     */
    private static String getFileName(String postfix) {
        DateFormat format = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
        return format.format(new Date(System.currentTimeMillis())) + postfix;
    }

    /**
     * 文件路径
     * @param context Context
     * @param dirName 文件夹
     * @return String
     */
    private static String getCacheFileDir(Context context, String dirName) {
        String name = "/" + dirName;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getApplicationContext().getExternalCacheDir() + name;
        } else {
            return context.getApplicationContext().getCacheDir() + name;
        }
    }

    /**
     * 创建 logcat 缓存文件
     * @param context Context
     * @param cleanCache 是否删除缓存
     * @return File
     */
    public static File createLogCacheFile(Context context, boolean cleanCache) {
        String fileName = getFileName(HTML);
        String fileDir = getCacheFileDir(context, LOG);
        return createCacheFile(fileDir, fileName, cleanCache);
    }

    /**
     * 创建 crash 缓存文件
     * @param context Context
     * @param cleanCache 是否删除缓存
     * @return File
     */
    public static File createCrashCacheFile(Context context, boolean cleanCache) {
        String fileName = getFileName(TXT);
        String fileDir = getCacheFileDir(context, CRASH);
        return createCacheFile(fileDir, fileName, cleanCache);
    }

    /**
     * 创建缓存文件
     * @param path path
     * @param fileName fileName
     * @param cleanCache cleanCache
     */
    private static File createCacheFile(String path, String fileName, boolean cleanCache) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 计算缓存日志大小
        computeSize(folder);

        // 初始化
        return initCacheFile(fileName, folder, cleanCache);
    }

    /**
     * 初始化缓存文件
     *
     * @param fileName 缓存文件名
     * @param folder   文件夹
     * @param cleanCache   是否清除缓存文件
     */
    private static File initCacheFile(String fileName, File folder, boolean cleanCache) {
        // 是否删除缓存日志文件
        if (cleanCache) {
            cleanCache(folder);
        }

        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 删除缓存文件
     *
     * @param folder 文件夹
     */
    private static void cleanCache(File folder) {
        for (File file : folder.listFiles()) {
            file.delete();
        }
    }

    /**
     * 获取缓存大小
     *
     * @param folder log 文件夹
     */
    private static void computeSize(File folder) {
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
