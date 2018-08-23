package cn.ljuns.logcollector.util;

import android.support.annotation.StringDef;

/**
 * Created by ljuns on 2018/8/23
 * I am just a developer.
 */
public class TypeUtils {

    public static final String VERBOSE = "V";
    public static final String DEBUG = "D";
    public static final String INFO = "I";
    public static final String WARN = "W";
    public static final String ERROR = "E";
    public static final String ASSERT = "A";

    @StringDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    public @interface Type {}
}
