package com.example.mypc.viewpagerfrist.Adapter;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypc.viewpagerfrist.Model.Music;
import com.example.mypc.viewpagerfrist.R;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements Filterable {

    private ArrayList<Music> arrMusic;
    private ArrayList<Music> arrMusicFull;
    private Context context;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public MusicAdapter(ArrayList<Music> arrMusic, Context context) {
        this.arrMusic = arrMusic;
        arrMusicFull=new ArrayList<>(arrMusic);
        this.context = context;
    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_contact,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder viewHolder, int i) {
        Music music=arrMusic.get(i);
        viewHolder.txtNameSong.setText(music.getNameSong());
        viewHolder.txtNameSinger.setText(music.getNameSinger());
        if(music.getIdLocal()){
            viewHolder.imgLocal.setImageResource(R.drawable.ic_phone_android);
        }else{
            viewHolder.imgLocal.setImageResource(R.drawable.ic_arrow);
        }

    }

    @Override
    public int getItemCount() {
        return arrMusic.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameSong;
        TextView txtNameSinger;
        ImageView imgLocal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameSong=itemView.findViewById(R.id.txtNameSong);
            txtNameSinger=itemView.findViewById(R.id.txtSinger);
            imgLocal=itemView.findViewById(R.id.imgLocal);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
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

    private Filter MusicFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Music> filteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0){
                filteredList.addAll(arrMusicFull);
            }else{
                String filteredPattern=constraint.toString().toLowerCase().trim();

                for(Music item:arrMusicFull){
                    if(item.getNameSong().toLowerCase().contains(filteredPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrMusic.clear();
            arrMusic.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
