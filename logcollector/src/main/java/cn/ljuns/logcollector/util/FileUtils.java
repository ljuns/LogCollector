package cn.ljuns.logcollector.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

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
public class FileUtils {

    private static final String DEFAULT_FORMAT = "yyyyMMdd_HHmmss_SSS";
    private static final int SYSTEM = 1024;
    private static final int DIRECTORY_SIZE = 10;

    /**
     * 创建 logcat 缓存文件
     *
     * @param context    Context
     * @param cleanCache cleanCache
     * @return File
     */
    public static File createLogCacheFile(Context context, File file, boolean cleanCache) {
        if (file == null) {
            return createCacheFile(getCacheFileDir(context, "log"), getFileName(), cleanCache);
        } else {
            return FileUtils.createLogCacheFile(file);
        }
    }

    /**
     * 创建 logcat 缓存文件
     *
     * @param file file
     * @return File
     */
    private static File createLogCacheFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                return createFile(file);
            } else if (file.isDirectory()) {
                return createCacheFile(file.getAbsolutePath(), getFileName(), false);
            }
        } else {
            if (file.mkdirs()) {
                return createLogCacheFile(file);
            }
        }
        return file;
    }

    /**
     * 创建 crash 缓存文件
     *
     * @param context    Context
     * @param cleanCache cleanCache
     * @return File
     */
    public static File createCrashCacheFile(Context context, boolean cleanCache) {
        return createCacheFile(getCacheFileDir(context, "crash"), getFileName(), cleanCache);
    }

    /**
     * 创建缓存文件
     *
     * @param path       path
     * @param fileName   fileName
     * @param cleanCache cleanCache
     */
    private static File createCacheFile(String path, String fileName, boolean cleanCache) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 是否删除缓存日志文件
        if (cleanCache) {
            computeSize(directory);
        }

        File file = new File(directory, fileName);
        return createFile(file);
    }

    /**
     * 新建文件
     *
     * @param file
     * @return
     */
    @NonNull
    private static File createFile(File file) {
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
     * 获取缓存大小
     *
     * @param directory directory
     */
    private static void computeSize(File directory) {
        long length = 0L;
        if (directory.exists()) {
            for (File file1 : directory.listFiles()) {
                length += file1.length();
            }
        }

        //限定大小 10M
        if ((length / SYSTEM / SYSTEM) >= DIRECTORY_SIZE) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }

    /**
     * 文件名
     *
     * @return FileName
     */
    private static String getFileName() {
        DateFormat format = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
        return format.format(new Date(System.currentTimeMillis())) + ".txt";
    }

    /**
     * 文件路径
     *
     * @param context Context
     * @param dirName dirName
     * @return FileDir
     */
    private static String getCacheFileDir(Context context, String dirName) {
        String name = "/" + dirName;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir() + name;
        } else {
            return context.getCacheDir() + name;
        }
    }
}
