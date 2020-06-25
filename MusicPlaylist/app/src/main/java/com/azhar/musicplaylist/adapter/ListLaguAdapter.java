package com.azhar.musicplaylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.azhar.musicplaylist.R;
import com.azhar.musicplaylist.model.ModelListLagu;

import java.util.List;

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */

public class ListLaguAdapter extends RecyclerView.Adapter<ListLaguAdapter.ViewHolder> {

    private List<ModelListLagu> items;
    private ListLaguAdapter.onSelectData onSelectData;
    private Context mContext;

    public interface onSelectData {
        void onSelected(ModelListLagu modelListLagu);
    }

    public ListLaguAdapter(Context context, List<ModelListLagu> items, ListLaguAdapter.onSelectData xSelectData) {
        this.mContext = context;
        this.items = items;
        this.onSelectData = xSelectData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_music, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ModelListLagu data = items.get(position);

        //Get Image
        Glide.with(mContext)
                .load(data.strCoverLagu)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_no_image)
                .into(holder.imgCover);

        holder.tvBand.setText(data.strNamaBand);
        holder.tvTitleMusic.setText(data.strJudulMusic);
        holder.cvListMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectData.onSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //Class Holder
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBand;
        public TextView tvTitleMusic;
        public CardView cvListMusic;
        public ImageView imgCover;

        public ViewHolder(View itemView) {
            super(itemView);
            cvListMusic = itemView.findViewById(R.id.cvListMusic);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvBand = itemView.findViewById(R.id.tvBand);
            tvTitleMusic = itemView.findViewById(R.id.tvTitleMusic);
        }
    }

}
