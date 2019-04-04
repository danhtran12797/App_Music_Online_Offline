package com.danhtran12797.thd.app_music2019.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danhtran12797.thd.app_music2019.Activity.OfflineActivity;
import com.danhtran12797.thd.app_music2019.Adapter.MusicAdapter;
import com.danhtran12797.thd.app_music2019.Model.Music;
import com.danhtran12797.thd.app_music2019.R;

import java.io.File;
import java.util.ArrayList;

import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrFav;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrPlaylist;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrSong;
import static com.danhtran12797.thd.app_music2019.Activity.OfflineActivity.KEY_LIST_SONG;
import static com.danhtran12797.thd.app_music2019.Activity.OfflineActivity.arrMusic;
import static com.danhtran12797.thd.app_music2019.Activity.OfflineActivity.delete_arrMusic_local;

public class ListSongFragment extends Fragment{
    private static final String ARG_ARR_MUSIC = "MUSICS";

    View view;
    View view1;
    public static RecyclerView recyclerView;
    private ArrayList<Music> arrMusics;
    public static MusicAdapter musicAdapter;
    private RelativeLayout relativeLayout;
    public static FragmentContactListener listener;
    private BottomSheetDialog bottomSheetDialog;


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

        musicAdapter = new MusicAdapter(arrMusics, getContext(), new MusicAdapter.OnClickMenuSongBottomSheet() {
            @Override
            public void show_bottom_sheet(final int position) {
                final Music music = arrMusics.get(position);

                view1=LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout,null);

                ImageView imgAvatar=view1.findViewById(R.id.img_avatar_bs);
                TextView txtNameSong=view1.findViewById(R.id.txt_title_bs);
                TextView txtNameAuthor=view1.findViewById(R.id.txt_author_bs);
                ImageView imgLocation=view1.findViewById(R.id.imgLocal_bss);
                LinearLayout playlist=view1.findViewById(R.id.layout_playlist_bs);
                LinearLayout favorite=view1.findViewById(R.id.layout_favorite_bs);
                LinearLayout infor=view1.findViewById(R.id.layout_infor_bs);
                LinearLayout delete=view1.findViewById(R.id.layout_delete_bs);

                playlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(!arrPlaylist.contains(music)){
//                            if(arrFav.contains(music)){
//                                String id=music.getId();
//                                for(int i=0;i<arrFav.size();i++){
//                                    if(arrFav.get(i).equals(id)){
//                                        arrFav.get(i).setCheck_playlist(true);
//                                    }
//                                }
//                            }
//                            music.setCheck_playlist(true);
//                            arrPlaylist.add(music);
//
//                        }
                    }
                });
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                infor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());

                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file=new File(music.getPath());
                        if(file.delete()){
                            arrMusic.remove(position);
                            delete_arrMusic_local(position);
                            musicAdapter.notifyItemRemoved(position);
                        }
                    }
                });

                txtNameSong.setText(music.getNameSong());
                txtNameAuthor.setText(music.getNameSinger());
                if (music.getIdLocal()) {
                    imgLocation.setImageResource(R.drawable.ic_phone_android);
                } else {
                    imgLocation.setImageResource(R.drawable.ic_arrow);
                }

                String path = music.getPath();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(path);

                byte[] data = mmr.getEmbeddedPicture();
                if (data != null) {
                    Bitmap bitmap_disc = BitmapFactory.decodeByteArray(data, 0, data.length);
                    imgAvatar.setImageBitmap(bitmap_disc);
                } else {
                    if (KEY_LIST_SONG.equals("playlists")){
                        imgAvatar.setImageResource(R.drawable.disc_song);
                    }
                    else if (KEY_LIST_SONG.equals("playlists")){
                        imgAvatar.setImageResource(R.drawable.disc_playlist);
                    }
                    else{
                        imgAvatar.setImageResource(R.drawable.disc_favorite);
                    }
                }
                mmr.release();

                bottomSheetDialog=new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();
            }
        });
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
