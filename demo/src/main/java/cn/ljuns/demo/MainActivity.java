package cn.ljuns.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        Log.d(TAG, "onCreate: ");

//        try {
//            Thread.sleep(5000);
//            String str = null;
//            str.equals("");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    public static void main(String[] args) {
//        List<String> commandList = new ArrayList<>();
//        String command = "logcat -d -v time -f " + "/mnt/sdcard/bugLog/logcat.txt";
//        commandList.add(command);
//
//        String[] coms = commandList.toArray(new String[commandList.size()]);
//        for (String com : coms) {
//            System.out.println(com);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
