package com.example.mypc.viewpagerfrist.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mypc.viewpagerfrist.Activity.LoadActivity;
import com.example.mypc.viewpagerfrist.Activity.MainActivity;
import com.example.mypc.viewpagerfrist.Adapter.MusicAdapter;
import com.example.mypc.viewpagerfrist.Model.Music;
import com.example.mypc.viewpagerfrist.R;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.util.ArrayList;

import static com.example.mypc.viewpagerfrist.Activity.MainActivity.KEY_LIST_SONG;
import static com.example.mypc.viewpagerfrist.Activity.MainActivity.arrMusic;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class Fragment_contact extends Fragment {
    private static final String ARG_ARR_MUSIC="MUSICS";

    View view;
    private RecyclerView recyclerView;
    private ArrayList<Music> arrMusics;
    public static MusicAdapter musicAdapter;
    private RelativeLayout relativeLayout;
    public static FragmentContactListener listener;

    public interface FragmentContactListener {
        void onInpuSent(int position);
    }

    public static Fragment_contact newInstance(ArrayList<Music> list){
        Fragment_contact fragment=new Fragment_contact();
        Bundle bundle=new Bundle();
        bundle.putSerializable(ARG_ARR_MUSIC,list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        arrMusics=new ArrayList<>();

        if(getArguments()!=null){
            arrMusics= (ArrayList<Music>) getArguments().getSerializable(ARG_ARR_MUSIC);
        }


        view = inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        relativeLayout=view.findViewById(R.id.background_list_music);

        musicAdapter = new MusicAdapter(arrMusics, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecorationvider.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecorationvider);

        if(KEY_LIST_SONG.equals("playlists")){
            Log.d("EEE","Fragment_contact playlist");
            relativeLayout.setBackgroundResource(R.drawable.custom_background_playlists);
        }
        if(KEY_LIST_SONG.equals("favorites")){
            Log.d("EEE","Fragment_contact favorites");
            relativeLayout.setBackgroundResource(R.drawable.custom_background_favorites);
        }

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String nameSong = arrMusic.get(position).getNameSong();
                listener.onInpuSent(position);
                //MainActivity.toolbar.setTitle(nameSong);
            }
        });

        Log.d("TTT", "Fragment_contact");

        return view;
    }

    public void setArrMusic(int position){
        arrMusics.remove(position);
        musicAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentContactListener) {
            listener = (FragmentContactListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement FragmentContactListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
