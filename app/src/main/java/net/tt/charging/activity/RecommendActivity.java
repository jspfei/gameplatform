package net.tt.charging.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import net.tt.charging.Constant;
import net.tt.charging.R;
import net.tt.charging.adapter.RecommendAdapter;
import net.tt.charging.bean.Product;
import net.tt.charging.service.DownloadService;
import net.tt.charging.utils.AssetsReadJsonManager;
import net.tt.charging.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecommendActivity extends Activity {

    private ListView recommend_list_view;
    ProgressDialog mProgressDialog;
    private int[] icon={R.drawable.icon_1,R.drawable.icon_2,R.drawable.icon_3,R.drawable.icon_4,
            R.drawable.icon_5,R.drawable.icon_6,R.drawable.icon_7,R.drawable.icon_8};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);




        mProgressDialog = new ProgressDialog(RecommendActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMax(100);
        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        mProgressDialog.setIndeterminate(false);
        // 设置ProgressDialog 进度条进度

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });


        recommend_list_view = (ListView) findViewById(R.id.recommend_list_view);
        recommend_list_view.setAdapter(new RecommendAdapter(this, getJson(), new RecommendAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemClick(final Product product) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RecommendActivity.this);
                builder.setMessage("下载 " + product.getName() + " 需要 " + product.getPrize() + "元,是否支付？");
                builder.setTitle("计费提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (Constant.isWhite) {
                            sendDownLoad(product);//file 下载
                           // downData(product); //service 下载

                        } else {

                            // 这里接计费代码 如果成功调用    paySuccess(product);

                        }

                        //   showNotice();
                        //设置你的操作事项
                    }
                });

                builder.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        }));
    }

    public void paySuccess(Product product){
        sendDownLoad(product);
    }



    private void showNotice(){

        Toast.makeText(RecommendActivity.this,"购买成功",Toast.LENGTH_SHORT).show();

    }

    private List<Product>   getJson(){
        List<Product> list = new ArrayList<Product>();
        String json =AssetsReadJsonManager.getJson(this);
        try {
            JSONObject jsonObject2 = new JSONObject(json);

            JSONArray jsonArray = jsonObject2.getJSONArray("list");
            for (int i=0;i<jsonArray.length();i++)
            {
                Product product = new Product();
                JSONObject jsonObjectSon= (JSONObject)jsonArray.opt(i);
                product.setId(jsonObjectSon.getInt("id"));
                product.setName(jsonObjectSon.getString("name"));
                product.setDesc(jsonObjectSon.getString("desc"));
                product.setDowntimes(jsonObjectSon.getInt("times"));
                product.setSize(jsonObjectSon.getString("size"));
                product.setIcon(icon[jsonObjectSon.getInt("icon")]);
                product.setFileName("a"+String.valueOf(i)+".apk");
                product.setPrize(jsonObjectSon.getInt("prize"));
                product.setDownurl(jsonObjectSon.getString("url"));
                list.add(product);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  Log.i("HACK-TAG",json ) ;

        return list;

    }

    public boolean fileIsExists(String name){
        try{
            File f= new File("/sdcard/update/"+name);
            if(!f.exists()){
                return false;
            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
    private void sendDownLoad( Product product){
        final String fileName = product.getFileName();
        final String url = product.getDownurl();

        Log.i("HACK-TAG", product.getId() + "");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = FileUtils.downLoadFile(url, fileName);
                if (file != null) {
                    FileUtils.openFile(file, RecommendActivity.this);
                }
            }
        });
        t.start();

    }

    public void downData(Product product){

        mProgressDialog.show();
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("url", product.getDownurl());
        intent.putExtra("name", product.getFileName());
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        startService(intent);

    }
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                String filename = resultData.getString("name");
                Log.i("HACK-TAG", "progress: "+progress);
                mProgressDialog.setProgress(progress);
                if (progress == 100) {

                    mProgressDialog.dismiss();
                    File file = new File("/sdcard/update/"+filename+".apk");
                    FileUtils.openFile(file, RecommendActivity.this);
                }
            }
        }
    }
}
