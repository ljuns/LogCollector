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

        Log.d(TAG, "onCreate: ");

//        try {
//            Thread.sleep(5000);
//            String str = null;
//            str.equals("");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}
