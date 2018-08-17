package cn.ljuns.logcollector;

import android.support.annotation.StringDef;

public class TagUtils {

    public static final String VERBOSE = "V/";
    public static final String DEBUG = "D/";
    public static final String INFO = "I/";
    public static final String WARN = "W/";
    public static final String ERROR = "E/";
    public static final String ASSERT = "A/";

    public static final String[] TAGS = new String[]{VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT};

    @StringDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    @interface LogType {}

    /**
     * 获取对应的 index
     * @param tag tag
     * @return index
     */
    public static int getIndex(String tag) {
        for (int i = 0; i < TAGS.length; i++) {
            if (tag.equals(TAGS[i])) {
                return i;
            }
        }
        return 0;
    }
}