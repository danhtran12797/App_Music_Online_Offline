package com.example.mypc.viewpagerfrist.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mypc.viewpagerfrist.Adapter.MusicAdapter;
import com.example.mypc.viewpagerfrist.Model.Music;
import com.example.mypc.viewpagerfrist.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.mypc.viewpagerfrist.Activity.MainActivity.KEY_LIST_SONG;


public class LoadActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFav, btnPlaylist, btnSong;

    private Dialog dialog_load_data;

    private long backPressedTime;
    private Toast backToast;

    public static final String SHARED_PREFERENCES_NAME = "MUSIC2019";
    public static String KEY = "music";
    public static String KEY_SONGS = "songs";
    public static final String KEY_PLAYLISTS = "playlists";
    public static final String KEY_FAVORITES = "favorites";

    public static ArrayList<Music> arrSong;
    public static ArrayList<Music> arrFav;
    public static ArrayList<Music> arrPlaylist;

    public ArrayList<String> arrID_Fav;
    public ArrayList<String> arrID_Playlist;

    SharedPreferences sharedPreferences=null;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Log.d("CCC","LoadActivity: onCreate");

        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        btnFav=findViewById(R.id.btnFav);
        btnPlaylist=findViewById(R.id.btnPlaylists);
        btnSong=findViewById(R.id.btnSongs);

        btnFav.setOnClickListener(this);
        btnSong.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);

        arrSong=new ArrayList<>();


        dialog_load_data=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog_load_data.setContentView(R.layout.layout_dialog_load_data);

        if (ContextCompat.checkSelfPermission(LoadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            new LoadSong().execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(arrFav!=null)
            Log.d("CCC","LoadActivity: onStart "+arrFav.size());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("CCC","LoadActivity: onRestart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(arrFav!=null&&arrPlaylist!=null){
            btnSong.setText("Songs("+arrSong.size()+")");
            btnPlaylist.setText("Playlists("+arrPlaylist.size()+")");
            btnFav.setText("Favorites("+arrFav.size()+")");
        }
    }

    public ArrayList<Music> scanDeviceForMp3Files() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                //MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        ArrayList<Music> mp3Files = new ArrayList<>();

        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = getContentResolver().query(uri, projection, selection, null, "");
            if (cursor != null) {
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    String path = cursor.getString(2);
                    String nameSinger = cursor.getString(1);
                    String nameSong = cursor.getString(0);
                    String id = cursor.getString(3);

                    cursor.moveToNext();
                    if (path != null && path.endsWith(".mp3")) {
                        boolean check_fav=false;
                        boolean check_playlist=false;
                        if(arrID_Fav.contains(id))
                            check_fav=true;
                        if(arrID_Playlist.contains(id))
                            check_playlist=true;

                        mp3Files.add(new Music(nameSong, nameSinger,true,path, id, check_fav, check_playlist));
                    }
                }
            }


        } catch (Exception e) {
            Log.e("TAG", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return mp3Files;
    }

    public ArrayList<Music> getPlayList(String rootPath) {
        ArrayList<Music> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    String parent= file.getParentFile().getName();

                    if(parent.equals("Zing MP3")){
                        String arr[]=file.getName().split("_");
                        String id=arr[2].substring(1,11);
                        boolean check_fav=false;
                        boolean check_playlist=false;
                        if(arrID_Fav.contains(id))
                            check_fav=true;
                        if(arrID_Playlist.contains(id))
                            check_playlist=true;

                        fileList.add(new Music(arr[0], arr[1],false, file.getPath(),id, check_fav, check_playlist));
                    }
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<String> get_arr_id(String id_song){
        String arr[]=id_song.split("-");
        return  new ArrayList<>(Arrays.asList(arr));
    }

    public ArrayList<Music> create_fav_playlist_music(ArrayList<String> arrayList){
        ArrayList<Music> arrTemp=new ArrayList<>();
        for(String id:arrayList){
            for(Music music:arrSong){
                if(id.equals(music.getId())){
                    arrTemp.add(music);
                    break;
                }
            }
        }
        return  arrTemp;
    }

    public void addMusic() {
        sharedPreferences=getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE);
        if(!(sharedPreferences.contains(KEY_FAVORITES)&&sharedPreferences.contains(KEY_PLAYLISTS))){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(KEY_FAVORITES,"");
            editor.putString(KEY_PLAYLISTS,"");
            editor.apply();
        }
        String fav=sharedPreferences.getString(KEY_FAVORITES,"");
        String playlist=sharedPreferences.getString(KEY_PLAYLISTS,"");

        arrID_Fav=get_arr_id(fav);
        arrID_Playlist=get_arr_id(playlist);

        arrSong = new ArrayList<>();
        arrSong = getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath());
        arrSong.addAll(scanDeviceForMp3Files());

        if(fav.equals(""))
            arrFav=new ArrayList<>();
        else {
            arrFav=create_fav_playlist_music(arrID_Fav);
        }
        if(playlist.equals(""))
            arrPlaylist=new ArrayList<>();
        else {
            arrPlaylist=create_fav_playlist_music(arrID_Playlist);
        }
    }


    public class LoadSong extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog_load_data.show();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            dialog_load_data.dismiss();
            btnFav.setText("Favorites("+arrFav.size()+")");
            btnPlaylist.setText("Playlists("+arrPlaylist.size()+")");
            btnSong.setText("Songs("+arrSong.size()+")");
            Log.d("CCC","LoadActivity: onPostExecute");
        }

        @Override
        protected String doInBackground(Void... voids) {
            addMusic();
            return "Good";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                new LoadSong().execute();
            } else {
                arrSong=new ArrayList<>();
                arrFav=new ArrayList<>();
                arrPlaylist=new ArrayList<>();

                btnSong.append("(0)");
                btnPlaylist.append("(0)");
                btnFav.append("(0)");

                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "You can't listen to music because you have not confirm Permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this,MainActivity.class);;
        switch (v.getId()){
            case R.id.btnSongs:
                KEY_SONGS="songs";
                intent.putExtra(KEY,arrSong);
                intent.putExtra(KEY_SONGS,KEY_SONGS);
                startActivity(intent);
                break;
            case R.id.btnPlaylists:
                KEY_SONGS="playlists";
                intent.putExtra(KEY,arrPlaylist);
                intent.putExtra(KEY_SONGS,KEY_PLAYLISTS);
                startActivity(intent);
                break;
            case R.id.btnFav:
                KEY_SONGS="favorites";
                intent.putExtra(KEY,arrFav);
                intent.putExtra(KEY_SONGS,KEY_FAVORITES);
                startActivity(intent);
                break;
        }
    }
}
