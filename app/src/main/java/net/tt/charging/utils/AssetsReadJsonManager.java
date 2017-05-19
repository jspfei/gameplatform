package net.tt.charging.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by admin on 2017/5/19.
 */

public class AssetsReadJsonManager {

    public static String getJson(Context context) {

        String data = "";
        try{
            InputStream inputStream = context.getResources().getAssets().open("map.json");
            InputStreamReader isReadr = new InputStreamReader(inputStream,"utf-8");
            BufferedReader reader = new BufferedReader(isReadr);
            String out = "";
            int index = 0;
            while((out=reader.readLine())!=null){
                // Log.d("读取到的文件信息:",out);
                // out.concat(out);
                data = data+out;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

}
