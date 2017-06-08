package net.tt.charging.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by admin on 2017/5/24.
 */

public class DownloadService extends IntentService {

    public static final int UPDATE_PROGRESS = 8344;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        File tmpFile = new File("/sdcard/update");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        String urlToDownload = intent.getStringExtra("url");
        String fileName = intent.getStringExtra("name");
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");

        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream("/sdcard/update/"+fileName +".apk");
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                resultData.putString("name" ,fileName);
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        receiver.send(UPDATE_PROGRESS, resultData);
    }
}
