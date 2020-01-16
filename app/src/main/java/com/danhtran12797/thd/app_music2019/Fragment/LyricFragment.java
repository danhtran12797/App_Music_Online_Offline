package com.danhtran12797.thd.app_music2019.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danhtran12797.thd.app_music2019.R;

import java.util.ArrayList;

import static com.danhtran12797.thd.app_music2019.Activity.OfflineActivity.KEY_LIST_SONG;

public class LyricFragment extends Fragment {

    View view;
    public LinearLayout linearLayout;
    private RelativeLayout background_lyric;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lyric, container, false);
        linearLayout = view.findViewById(R.id.layout_lyric);
        background_lyric = view.findViewById(R.id.scrollView);

        if (KEY_LIST_SONG.equals("playlists")) {
            background_lyric.setBackgroundResource(R.drawable.custom_background_playlists);

        }
        if (KEY_LIST_SONG.equals("favorites")) {
            background_lyric.setBackgroundResource(R.drawable.custom_background_favorites);
        }

        Log.d("TTT", "LyricFragment");

        return view;
    }

    // add View => hiển thị lyric
    public void add_lyric_song(ArrayList<String> arrLyric) {
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (String line : arrLyric) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            textView.setText(line);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            linearLayout.addView(textView);
        }
    }
}
