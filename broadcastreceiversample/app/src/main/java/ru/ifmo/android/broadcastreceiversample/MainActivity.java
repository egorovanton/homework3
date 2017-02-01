package ru.ifmo.android.broadcastreceiversample;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static ru.ifmo.android.broadcastreceiversample.Constants.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();


    private String imageUrl = IMAGE_SOURCE_URL;
    private ImageView imageView;
    private TextView progressBar;

    private ResultReceiver picDownloadedReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "onReceiveResult: result is received");

            boolean success = resultData.getBoolean(KEY_SUCCESS);
            String picPath  = resultData.getString(KEY_PICTURE_PATH);

            Log.d(TAG, "onReceiveResult: success = " + success);
            Log.d(TAG, "onReceiveResult: picPath = " + picPath);
            if (success && picPath != null) {

                File file = new File(picPath);
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                progressBar.setText(R.string.picture_downloaded);

            } else {
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            Log.d(TAG, "onReceive: BATTERY state changed");

            Intent intent = new Intent(context, MainService.class);
            intent.putExtra(KEY_URL, imageUrl);
            intent.putExtra(KEY_RESULT_RECEIVER, picDownloadedReceiver);
            startService(intent);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.image_source)).setText(imageUrl);
        imageView = (ImageView) findViewById(R.id.image);
        progressBar = (TextView) findViewById(R.id.image_status);

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(batteryReceiver);
    }
}
