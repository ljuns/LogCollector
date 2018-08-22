package cn.ljuns.logcollector;

import android.support.annotation.StringDef;

public class LevelUtils {

    public static final String V = "V";
    public static final String D = "D";
    public static final String I = "I";
    public static final String W = "W";
    public static final String E = "E";
    public static final String F = "F";
    public static final String S = "S";

    @StringDef({V, D, I, W, E, F, S})
    @interface Level {}

}