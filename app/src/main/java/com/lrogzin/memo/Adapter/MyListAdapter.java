package com.lrogzin.memo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lrogzin.memo.Bean.KoahiPaiHangBean;
import com.lrogzin.memo.R;

import java.util.List;

public class MyListAdapter extends BaseAdapter {

    private List<KoahiPaiHangBean> mStudentDataList;   //创建一个StudentData 类的对象 集合
    private LayoutInflater inflater;

    public  MyListAdapter (List<KoahiPaiHangBean> mStudentDataList, Context context) {
        this.mStudentDataList = mStudentDataList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mStudentDataList == null?0:mStudentDataList.size();  //判断有说个Item
    }

    @Override
    public Object getItem(int position) {
        return mStudentDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.item_kecheng_item,null);
        KoahiPaiHangBean mStudentData = (KoahiPaiHangBean) getItem(position);

        //在view 视图中查找 组件
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_age = (TextView) view.findViewById(R.id.tv_desc);
        TextView im_photo = (TextView) view.findViewById(R.id.tv_time);

        //为Item 里面的组件设置相应的数据
        tv_name.setText(mStudentData.getKaoshi_tittle());
        tv_age.setText(mStudentData.getScore());
        im_photo.setText(mStudentData.getShijian());

        //返回含有数据的view
        return view;
    }
}
