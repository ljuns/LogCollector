package cn.ljuns.logcollector;

import android.content.Context;
import android.util.Log;

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


        int alpha = (color & 0xff000000) >>> 24;
        int red   = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue  = (color & 0x000000ff);

        Log.d("ljuns", "parseColor: " + color + ": " + parseARGB(alpha) +
                parseARGB(red) +
                parseARGB(green) +
                parseARGB(blue)
                .toUpperCase());

        return ("#" +
                parseARGB(alpha) +
                parseARGB(red) +
                parseARGB(green) +
                parseARGB(blue))
                .toUpperCase();
    }

    /**
     * parseARGB
     * @param argb argb
     * @return String
     */
    private static String parseARGB(int argb) {
        StringBuilder sb = new StringBuilder(Integer.toHexString(argb & 0xff));
        if (sb.length() < 2) {
            sb.append("0");
        }
        return sb.toString();
    }
}
