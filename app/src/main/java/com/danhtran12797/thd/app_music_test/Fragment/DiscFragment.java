package com.danhtran12797.thd.app_music_test.Fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.danhtran12797.thd.app_music_test.Activity.OfflineActivity;
import com.danhtran12797.thd.app_music_test.Model.Music;
import com.danhtran12797.thd.app_music_test.R;
import com.danhtran12797.thd.app_music_test.Activity.OfflineActivity;
import com.danhtran12797.thd.app_music_test.Model.Music;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.danhtran12797.thd.app_music_test.Activity.OfflineActivity.KEY_LIST_SONG;

public class DiscFragment extends Fragment {

    View view;
    public static CircleImageView imgAvatarSong;
    private RelativeLayout relativeLayout;
    public static ObjectAnimator objectAnimator;

    public static DiscFragment newInstance() {
        return new DiscFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_disc, container, false);
        imgAvatarSong = view.findViewById(R.id.imgAvatarSong);
        relativeLayout = view.findViewById(R.id.background_disc);

        if (OfflineActivity.KEY_LIST_SONG.equals("playlists")) {
            Log.d("EEE", "DiscFragment playlist");
            relativeLayout.setBackgroundResource(R.drawable.custom_background_playlists);
            imgAvatarSong.setImageResource(R.drawable.disc_playlist);
        }
        if (OfflineActivity.KEY_LIST_SONG.equals("favorites")) {
            Log.d("EEE", "DiscFragment favorites");
            relativeLayout.setBackgroundResource(R.drawable.custom_background_favorites);
            imgAvatarSong.setImageResource(R.drawable.disc_favorite);
        }

        objectAnimator = ObjectAnimator.ofFloat(imgAvatarSong, "rotation", 0f, 360f);
        objectAnimator.setDuration(13000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setTarget(imgAvatarSong);

        return view;
    }

    public static void CreateMusic(int position, String disc) {
        Music music = OfflineActivity.arrMusic.get(position);

        String path = music.getPath();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        objectAnimator.start();

        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            Bitmap bitmap_disc = BitmapFactory.decodeByteArray(data, 0, data.length);
            imgAvatarSong.setImageBitmap(bitmap_disc);

        } else {
            if (disc.equals("songs")){
                imgAvatarSong.setImageResource(R.drawable.disc_song);
            }
            else if (disc.equals("playlists")){
                imgAvatarSong.setImageResource(R.drawable.disc_playlist);
            }
            else{
                imgAvatarSong.setImageResource(R.drawable.disc_favorite);
            }
        }

        mmr.release();
    }
}
