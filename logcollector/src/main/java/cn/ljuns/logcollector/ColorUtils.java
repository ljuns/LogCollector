package cn.ljuns.logcollector;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by ljuns on 2018/8/17
 * I am just a developer.
 */
public class ColorUtils {

    /**
     * parseColor
     * @param color color
     * @return String
     */
    public static String parseColor(Context context, int color) {
        if (color > 0) {
            color = context.getResources().getColor(color);
        }

        return "#" +
                parseARGB(Color.alpha(color)) +
                parseARGB(Color.red(color)) +
                parseARGB(Color.green(color)) +
                parseARGB(Color.blue(color));

    }

    /**
     * parseARGB
     * @param argb argb
     * @return String
     */
    private static String parseARGB(int argb) {
        StringBuilder sb = new StringBuilder(Integer.toHexString(argb));
        if (sb.length() < 2) {
            sb.append("0");
        }
        return sb.toString().toUpperCase();
    }
}
