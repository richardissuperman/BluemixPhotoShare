package com.example.qingzhong.bluemixphototshare;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by qingzhong on 24/7/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RViewHolder> {


    private ArrayList<UserDataModel> list;

    public RecyclerViewAdapter(ArrayList<UserDataModel> list){

        this.list=list;

    }


    @Override
    public RecyclerViewAdapter.RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =View.inflate(parent.getContext(),R.layout.recycleitem,null);
        RViewHolder viewHolder=new RViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.RViewHolder holder, int position) {


       // ArrayList<UserDataModel> list=(ArrayList<UserDataModel>)(this.list.get(position));
        if(list==null||list.size()==0){
            return;
        }

        Log.e("list size",this.list.size()+"");
        UserDataModel model=this.list.get(position);
        holder.img.setImageBitmap(model.photo);
        holder.post.setText(model.userName);
        holder.date.setText(model.date.toString());


    }


    @Override
    public int getItemCount() {
        if(this.list!=null){
        return list.size()>0?list.size():10;}
        return 10;
    }



    public ArrayList<UserDataModel> getDataList(){
        return this.list;
    }

    public void setDataList(ArrayList<UserDataModel> list){
        this.list=list;
    }

    public class  RViewHolder extends RecyclerView.ViewHolder{

        public ImageView img;
        public TextView post;
        public TextView date;
        public RViewHolder(View view){
            super(view);
            img=(ImageView)view.findViewById(R.id.photo);
            post=(TextView)view.findViewById(R.id.post);
            date=(TextView)view.findViewById(R.id.time);
        }
    }
}
