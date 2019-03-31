package com.danhtran12797.thd.app_music2019.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.danhtran12797.thd.app_music2019.Adapter.MusicAdapter;
import com.danhtran12797.thd.app_music2019.Model.Music;
import com.danhtran12797.thd.app_music2019.R;

import java.util.ArrayList;

import static com.danhtran12797.thd.app_music2019.Activity.OfflineActivity.KEY_LIST_SONG;

public class ListSongFragment extends Fragment {
    private static final String ARG_ARR_MUSIC = "MUSICS";

    View view;
    public static RecyclerView recyclerView;
    private ArrayList<Music> arrMusics;
    public static MusicAdapter musicAdapter;
    private RelativeLayout relativeLayout;
    public static FragmentContactListener listener;



    public interface FragmentContactListener {
        void onInpuSent(int position);
    }

    public static ListSongFragment newInstance(ArrayList<Music> list) {
        ListSongFragment fragment = new ListSongFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ARR_MUSIC, list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        arrMusics = new ArrayList<>();

        if (getArguments() != null) {
            arrMusics = (ArrayList<Music>) getArguments().getSerializable(ARG_ARR_MUSIC);
        }

        view = inflater.inflate(R.layout.fragment_list_song, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        relativeLayout = view.findViewById(R.id.background_list_music);

        musicAdapter = new MusicAdapter(arrMusics, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);


        DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_divider);
        dividerItemDecorationvider.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecorationvider);

        if (KEY_LIST_SONG.equals("playlists")) {
            Log.d("EEE", "ListSongFragment playlist");
            relativeLayout.setBackgroundResource(R.drawable.custom_background_playlists);
        }
        if (KEY_LIST_SONG.equals("favorites")) {
            Log.d("EEE", "ListSongFragment favorites");
            relativeLayout.setBackgroundResource(R.drawable.custom_background_favorites);
        }

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //String nameSong = arrMusic.get(position).getNameSong();
                listener.onInpuSent(position);
            }
        });

        Log.d("TTT", "ListSongFragment");

        return view;
    }

    // arrMusic thay đổi = arrMusics cũng đổi theo
    public void setArrMusic(int position) {
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
