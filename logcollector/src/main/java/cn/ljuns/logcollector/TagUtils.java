package cn.ljuns.logcollector;

import android.support.annotation.StringDef;

public class TagUtils {

    public static final String VERBOSE = "V/";
    public static final String DEBUG = "D/";
    public static final String INFO = "I/";
    public static final String WARN = "W/";
    public static final String ERROR = "E/";
    public static final String ASSERT = "A/";

    @StringDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    @interface LogType {}
}
