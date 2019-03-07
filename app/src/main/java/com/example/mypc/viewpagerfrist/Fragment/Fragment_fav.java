package com.example.mypc.viewpagerfrist.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.viewpagerfrist.Activity.MainActivity;
import com.example.mypc.viewpagerfrist.Model.Music;
import com.example.mypc.viewpagerfrist.R;

import java.util.ArrayList;

import static com.example.mypc.viewpagerfrist.Activity.MainActivity.KEY_LIST_SONG;

public class Fragment_fav extends Fragment {

    View view;
    public LinearLayout linearLayout;
    private RelativeLayout background_lyric;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_fav,container,false);
        linearLayout=view.findViewById(R.id.layout_lyric);
        background_lyric=view.findViewById(R.id.scrollView);

        if(KEY_LIST_SONG.equals("playlists")){
            background_lyric.setBackgroundResource(R.drawable.custom_background_playlists);

        }
        if(KEY_LIST_SONG.equals("favorites")){
            background_lyric.setBackgroundResource(R.drawable.custom_background_favorites);
        }

        Log.d("TTT","Fragment_fav");

        return view;
    }

    public void add_lyric_song(ArrayList<String> arrLyric){
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        for(String line:arrLyric){
            TextView textView= new TextView(getContext());
            textView.setLayoutParams(params);
            textView.setText(line);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            linearLayout.addView(textView);
        }
    }
}
