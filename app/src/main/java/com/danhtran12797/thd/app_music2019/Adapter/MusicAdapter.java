package com.danhtran12797.thd.app_music2019.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danhtran12797.thd.app_music2019.Model.Music;
import com.danhtran12797.thd.app_music2019.R;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements Filterable {

    private ArrayList<Music> arrMusic;
    private ArrayList<Music> arrMusicFull;
    private Context context;
    public int selectedPosition=-1;

    public void setPosition(int position){
        selectedPosition=position;
        notifyDataSetChanged();
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public MusicAdapter(ArrayList<Music> arrMusic, Context context) {
        this.arrMusic = arrMusic;
        arrMusicFull = new ArrayList<>(arrMusic);
        this.context = context;
    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicAdapter.ViewHolder viewHolder, final int i) {
        final Music music = arrMusic.get(i);
        viewHolder.txtNameSong.setText(music.getNameSong());
        viewHolder.txtNameSinger.setText(music.getNameSinger());
        if (music.getIdLocal()) {
            viewHolder.imgLocal.setImageResource(R.drawable.ic_phone_android);
        } else {
            viewHolder.imgLocal.setImageResource(R.drawable.ic_arrow);
        }

        if(selectedPosition==i)
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#AD383838"));
        else
            viewHolder.itemView.setBackgroundColor(Color.parseColor("#00ffffff"));

    }

    @Override
    public int getItemCount() {
        return arrMusic.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameSong;
        TextView txtNameSinger;
        ImageView imgLocal;
        RelativeLayout layout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtNameSong = itemView.findViewById(R.id.txtNameSong);
            txtNameSinger = itemView.findViewById(R.id.txtSinger);
            imgLocal = itemView.findViewById(R.id.imgLocal);
            layout = itemView.findViewById(R.id.layout_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return MusicFilter;
    }

    private Filter MusicFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Music> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrMusicFull);
            } else {
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (Music item : arrMusicFull) {
                    if (item.getNameSong().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrMusic.clear();
            arrMusic.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
