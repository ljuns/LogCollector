package cn.ljuns.logcollector;

import android.support.annotation.StringDef;

import java.util.HashMap;
import java.util.Map;

public class TagUtils {

    public static final String VERBOSE = "V/";
    public static final String DEBUG = "D/";
    public static final String INFO = "I/";
    public static final String WARN = "W/";
    public static final String ERROR = "E/";
    public static final String ASSERT = "A/";

    public static final String VERBOSE_COLOR = "#ED008C";
    public static final String DEBUG_COLOR = "#00FFOO";
    public static final String INFO_COLOR = "#00FFFF";
    public static final String WARN_COLOR = "#FFFFOO";
    public static final String ERROR_COLOR = "#FF00OO";
    public static final String ASSERT_COLOR = "#00AEEF";
    public static final String WHITE_COLOR = "#FFFFFF";
    public static final String BLACK_COLOR = "#000000";

    public static final String[] TAGS = new String[]{VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT};
    public static final String[] COLORS = new String[]{VERBOSE_COLOR, DEBUG_COLOR, INFO_COLOR, WARN_COLOR, ERROR_COLOR, ASSERT_COLOR};
    public static Map<String, String> tagWithColor = new HashMap<>();

    static {
        for (int i = 0; i < TAGS.length; i++) {
            tagWithColor.put(TAGS[i], COLORS[i]);
        }
    }

    @StringDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    @interface LogType {}
}
