package cn.ljuns.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        LogCollector.getInstance()
////                .setLogType(TagUtils.DEBUG)
//                .setCleanCache(true)
////                .setLogcatColors(R.color.colorAccent, R.color.colorPrimaryDark, Color.RED, Color.GREEN)
//                .start(this);

        Log.d(TAG, "onCreate: ");

        try {
            Thread.sleep(5000);
            String str = null;
            str.equals("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        System.out.println("black = " + R.color.colorPrimaryDark);

        int color = R.color.colorPrimaryDark;
        int alpha = (color & 0xff000000) >>> 24;
        int red   = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue  = (color & 0x000000ff);
        System.out.println("black = " + alpha + red + green + blue);
        System.out.println("red = " + Integer.toHexString(alpha & 0xff));
        System.out.println("red = " + Integer.toHexString(red & 0xff));
        System.out.println("red = " + Integer.toHexString(green & 0xff));
        System.out.println("red = " + Integer.toHexString(blue & 0xff));

//        System.out.println("parse = " + ColorUtils.parseColor(R.color.colorPrimaryDark));
    }


}
