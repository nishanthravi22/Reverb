package com.example.reverb;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterVideoList extends RecyclerView.Adapter<AdapterVideoList.MyViewHolder> {

    static ArrayList<ModelVideo> videosList = new ArrayList<ModelVideo>();
    Context context;

    AdapterVideoList(Context context, ArrayList<ModelVideo> videosList){
        this.context = context;
        this.videosList = videosList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ModelVideo item = videosList.get(position);
        int s=getItemCount();

            holder.tv_title.setText(item.getTitle());
            holder.tv_duration.setText(item.getDuration());
            Glide.with(context).load(item.getData()).into(holder.imgView_thumbnail);
            //holder.setIsRecyclable(false);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VideoPlayer.class);
                    intent.putExtra("position", position);

                    v.getContext().startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public void filterList(ArrayList<ModelVideo> filterelist) {
        videosList = filterelist;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView_thumbnail;
        TextView tv_title,tv_duration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.txtvidname);
            tv_duration = itemView.findViewById(R.id.vidduration);
        imgView_thumbnail = itemView.findViewById(R.id.vidimg);
        }
    }
}
