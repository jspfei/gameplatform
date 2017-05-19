package net.tt.charging.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.tt.charging.R;
import net.tt.charging.bean.Product;

import java.util.List;

/**
 * Created by admin on 2017/5/19.
 */

public class RecommendAdapter extends BaseAdapter {
    private List<Product> data;
    private LayoutInflater layoutInflater;
    private Context context;

    private OnItemButtonClickListener listener;
    public interface OnItemButtonClickListener{
        void onItemClick(Product video);
    }

    public RecommendAdapter(Context context,List<Product> list,OnItemButtonClickListener listener){
        this.context = context;
        this.data = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HandView handView = null;
        if(convertView == null){
            handView = new HandView();
            convertView = layoutInflater.inflate(R.layout.adapter_recommend_list,null);
            handView.tv_product_id = (TextView) convertView.findViewById(R.id.tv_product_id);
            handView.tv_product_desc = (TextView) convertView.findViewById(R.id.tv_product_desc);
            handView.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
            handView.tv_product_size = (TextView) convertView.findViewById(R.id.tv_product_size);
            handView.tv_product_down_times = (TextView) convertView.findViewById(R.id.tv_product_down_times);
            handView.iv_product_icon = (ImageView) convertView.findViewById(R.id.iv_product_icon);
            handView.iv_down_load = (ImageButton) convertView.findViewById(R.id.iv_down_load);
            convertView.setTag(handView);
        }else
        {
            handView =(HandView) convertView.getTag();
        }

        final Product product = data.get(position);
        handView.tv_product_id.setText(String.valueOf(product.getId()));
        handView.tv_product_desc.setText(product.getDesc());
        handView.tv_product_name.setText(product.getName());
        handView.tv_product_size.setText(product.getSize());
        handView.tv_product_down_times.setText(product.getDowntimes()+"次下载");
        handView.iv_product_icon.setImageResource(product.getIcon());
        handView.iv_down_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(product);
            }
        });
        //显示数据
        return convertView;
    }

    public class HandView{
        public TextView tv_product_id;
        public ImageView iv_product_icon;
        public TextView tv_product_desc;
        public TextView tv_product_name;
        public TextView tv_product_size;
        public TextView tv_product_down_times;
        public ImageButton iv_down_load;
    }
}
