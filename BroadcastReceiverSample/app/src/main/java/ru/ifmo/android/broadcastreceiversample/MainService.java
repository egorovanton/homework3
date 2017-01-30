package ru.ifmo.android.broadcastreceiversample;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static ru.ifmo.android.broadcastreceiversample.Constants.*;

/**
 * Created by Anton on 19.01.2017.
 */
public class MainService extends IntentService {
    private static final String TAG = MainService.class.getName();

    public MainService() {
        super(MainService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        String urlString = intent.getStringExtra(KEY_URL);
        ResultReceiver receiver = intent.getParcelableExtra(KEY_RESULT_RECEIVER);

        Log.d(TAG, "onHandleIntent: urlString = " + urlString);
        Log.d(TAG, "onHandleIntent: receiver = " + receiver);

        boolean success = false;
        File file = null;
        String picName = Uri.parse(urlString).getLastPathSegment();
        try {
            file = new File(getFilesDir(), picName);

            Log.d(TAG, "onHandleIntent: downloading file " + picName);
            Log.d(TAG, "onHandleIntent: filePath = " + file.getPath());

            URLConnection connection = new URL(urlString).openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(3000);
            connection.connect();

            InputStream is  = new BufferedInputStream(connection.getInputStream());
            OutputStream os = new FileOutputStream(file);
            byte buff[] = new byte[2048];
            while(is.read(buff) != -1) {
                os.write(buff);
            }
            os.close();
            is.close();

            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle resultData = new Bundle();
        resultData.putBoolean(KEY_SUCCESS, success);
        resultData.putString(KEY_PICTURE_PATH, picName);
        receiver.send(1, resultData);
    }
}
