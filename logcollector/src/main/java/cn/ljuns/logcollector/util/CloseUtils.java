package cn.ljuns.logcollector.util;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {

    private CloseUtils() {}

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
