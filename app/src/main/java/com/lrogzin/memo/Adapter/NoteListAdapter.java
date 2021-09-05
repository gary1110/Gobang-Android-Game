package com.lrogzin.memo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lrogzin.memo.Bean.NoteBean;

import java.util.ArrayList;
import java.util.List;

import static com.lrogzin.memo.R.drawable;
import static com.lrogzin.memo.R.id;
import static com.lrogzin.memo.R.layout;

/**
 .
 * RecyclerView适配器
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder>
        implements View.OnClickListener {
    private Context mContext;
    private List<NoteBean> mNotes;
    private OnRecyclerViewItemClickListener mOnItemClickListener ;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public NoteListAdapter() {
        mNotes = new ArrayList<>();
    }

    public void setmNotes(List<NoteBean> notes) {
        this.mNotes = notes;
}

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(NoteBean)v.getTag());
        }
    }


    @Override
    public void onViewRecycled(ViewHolder holder){
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, NoteBean note);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i(TAG, "###onCreateViewHolder: ");
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(layout.list_item_note,parent,false);
        view.setOnClickListener(this);
        //view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final NoteBean note = mNotes.get(position);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(note);
        holder.tv_list_title.setText(note.getTitle());
        holder.tv_list_summary.setText(note.getContent()+"秒");

    }

    @Override
    public int getItemCount() {
        //Log.i(TAG, "###getItemCount: ");
        if (mNotes != null && mNotes.size()>0){
            return mNotes.size();
        }
        return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_list_title;
        public TextView tv_list_summary;

        public ViewHolder(View view){
            super(view);
            tv_list_title = (TextView) view.findViewById(id.tv_list_title);
            tv_list_summary = (TextView) view.findViewById(id.tv_list_summary);

        }


    }
}
