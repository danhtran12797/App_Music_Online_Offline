package com.danhtran12797.thd.app_music2019.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danhtran12797.thd.app_music2019.Adapter.ViewPagerAdapter;
import com.danhtran12797.thd.app_music2019.Fragment.DiscFragment;
import com.danhtran12797.thd.app_music2019.Fragment.ListSongFragment;
import com.danhtran12797.thd.app_music2019.Fragment.LyricFragment;
import com.danhtran12797.thd.app_music2019.Model.Music;
import com.danhtran12797.thd.app_music2019.R;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;

import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.KEY;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.KEY_FAVORITES;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.KEY_PLAYLISTS;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.KEY_SONGS;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.SHARED_PREFERENCES_NAME;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrFav;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrPlaylist;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrSong;
import static com.danhtran12797.thd.app_music2019.Fragment.DiscFragment.objectAnimator;
import static com.danhtran12797.thd.app_music2019.Fragment.ListSongFragment.musicAdapter;
import static com.danhtran12797.thd.app_music2019.Fragment.ListSongFragment.recyclerView;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;


public class OfflineActivity extends AppCompatActivity implements ListSongFragment.FragmentContactListener, View.OnClickListener, AudioManager.OnAudioFocusChangeListener {
    private ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    //private TabLayout tabLayout;
    public static Toolbar toolbar;

    private final String path_lyric_MP3 = Environment.getExternalStorageDirectory().getPath() + "/Zing MP3/Lyrics/";
    private final String path_lyric_THD = Environment.getExternalStorageDirectory().getPath() + "/THD Music/lyric/";

    private ImageButton btnPre, btnPlay, btnLoop, btnNext, btnRandom, btnFav, btnPlaylist;
    private SeekBar seekBar;
    private TextView txtProgress;
    String timeTotal = "0:00";
    View thumbView;
    SimpleDateFormat format;
    Window window;

    private MediaPlayer mediaPlayer;
    public static ArrayList<Music> arrMusic;
    private Intent intent;

    private int position = 0;

    private boolean check_onBackPressed = false;
    public static String KEY_LIST_SONG = "";

    private boolean check_frist_play_music = false;
    AudioManager audioManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        thumbView = LayoutInflater.from(this).inflate(R.layout.layout_seekbar_thumb, null, false);

        viewPager = findViewById(R.id.viewPager);
        //tabLayout = findViewById(R.id.tabLayout);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_call);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_contact);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_favorite);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("danhtran12797");
        setSupportActionBar(toolbar);

        seekBar = findViewById(R.id.seekBar);

        btnRandom = findViewById(R.id.btnRandom);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPre = findViewById(R.id.btnPre);
        btnLoop = findViewById(R.id.btnLoop);
        btnPlaylist = findViewById(R.id.imgPlaylist);
        btnFav = findViewById(R.id.imgFav);

        btnFav.setTag(false);
        btnPlaylist.setTag(false);

        btnLoop.setTag(false);
        btnRandom.setTag(false);

        format = new SimpleDateFormat("m:ss");

        txtProgress = thumbView.findViewById(R.id.tvProgress);
        seekBar.setThumb(getThumb(0));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.d("DDD", "onCreate: Main");

        intent = getIntent();
        if (intent != null) {

            arrMusic = (ArrayList<Music>) intent.getSerializableExtra(KEY);
            KEY_LIST_SONG = intent.getStringExtra(KEY_SONGS);

            if (KEY_LIST_SONG.equals("songs")) {
                toolbar.setTitle("Songs(" + arrMusic.size() + ")");
            } else if (KEY_LIST_SONG.equals("playlists")) {
                setBackgroundListMusic(R.color.color_playlist);
                toolbar.setTitle("Playlists(" + arrMusic.size() + ")");
                toolbar.setBackgroundResource(R.color.color_playlist);
            } else {
                setBackgroundListMusic(R.color.color_favorites);
                toolbar.setTitle("Favorites(" + arrMusic.size() + ")");
                toolbar.setBackgroundResource(R.color.color_favorites);
                Log.d("DDD", "Favorites: " + arrMusic.size());
            }
            Log.d("AAA", "intent");

            check_fav_playlist();
        }


        PushDownAnim.setPushDownAnimTo(btnRandom, btnPlay, btnNext, btnLoop, btnPre, btnFav, btnPlaylist)
                .setScale(MODE_SCALE, 0.8f)
                .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!check_onBackPressed)
                    mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        ListSongFragment listSongFragment = ListSongFragment.newInstance(arrMusic);
        DiscFragment discFragment = new DiscFragment();
        LyricFragment lyricFragment = new LyricFragment();

        viewPagerAdapter.addFragment(listSongFragment, "");
        viewPagerAdapter.addFragment(discFragment, "");
        viewPagerAdapter.addFragment(lyricFragment, "");

        viewPager.setOffscreenPageLimit(2); // k cho tạo mới khi lướt tới
        //viewPager.setOffscreenPageLimit(0);

        viewPager.setAdapter(viewPagerAdapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        viewPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        mediaPlayer = new MediaPlayer();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    }

    //check is fav, playlist.Then set image.
    public void check_fav_playlist() {
        if (arrMusic.size() != 0) {
            Music music = arrMusic.get(position);

            if (music.isCheck_fav()) {
                btnFav.setTag(true);
                btnFav.setImageResource(R.drawable.ic_favorite_red);
            } else {
                btnFav.setTag(false);
                btnFav.setImageResource(R.drawable.ic_favorite);
            }
            if (music.isCheck_playlist()) {
                btnPlaylist.setTag(true);
                btnPlaylist.setImageResource(R.drawable.ic_playlist_blu);
            } else {
                btnPlaylist.setTag(false);
                btnPlaylist.setImageResource(R.drawable.ic_playlist);
            }
        }
    }

    //set color statusBar
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setBackgroundListMusic(int color) {
        window = this.getWindow();
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

    //custom seekbar
    public Drawable getThumb(int progress) {
        txtProgress.setText(format.format(progress) + "/" + timeTotal);

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);

        return new BitmapDrawable(getResources(), bitmap);
    }

    //check song is Favorite => chuỗi fav để gửi lên sharePreference
    public String get_string_fav_id_1(ArrayList<Music> arr) {
        String s = "";
        if (arr.size() != 0) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).isCheck_fav()) {
                    if (i == 0) {
                        s += arr.get(i).getId();
                    } else {
                        s += "-" + arr.get(i).getId();
                    }
                }
            }
        }
        return s;
    }

    //check song is Playlist => chuỗi playlists để gửi lên sharePreference
    public String get_string_playlist_id_1(ArrayList<Music> arr) {
        String s = "";
        if (arr.size() != 0) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).isCheck_playlist()) {
                    if (i == 0) {
                        s += arr.get(i).getId();
                    } else {
                        s += "-" + arr.get(i).getId();
                    }
                }
            }
        }
        return s;
    }


    // lưu chuỗi id favorites, playlists vào sharedPreference
    public void save_preferent() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_FAVORITES, get_string_fav_id_1(arrFav));
        editor.putString(KEY_PLAYLISTS, get_string_playlist_id_1(arrPlaylist));

        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        save_preferent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        //audioManager.abandonAudioFocus(this);
        super.onPause();
        Log.d("DDD", "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("DDD", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("DDD", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("DDD", "onDestroy");
        audioManager.abandonAudioFocus(this);
        check_onBackPressed = true;
        stopPlayer();

    }

    public void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (check_onBackPressed) {
                    handler.removeCallbacks(this);//dừng handler
                    stopPlayer();
                } else {
                    seekBar.setThumb(getThumb(mediaPlayer.getCurrentPosition())); //thiết lập Thumb cho seekbar
                    //txtTimeSong.setText(format.format(mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        }, 100);
    }

    public void setTimeTotal() {
        //SimpleDateFormat format_time=new SimpleDateFormat("mm:ss");
        //txtTimeTotal.setText(format_time.format(mediaPlayer.getDuration()));
        timeTotal = format.format(mediaPlayer.getDuration());
        seekBar.setMax(mediaPlayer.getDuration());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (arrMusic.size() == 0) {
            Toast.makeText(this, "NO SONGS!!!", Toast.LENGTH_SHORT).show();
            btnFav.setImageResource(R.drawable.ic_favorite);
            btnPlaylist.setImageResource(R.drawable.ic_playlist);
            toolbar.setTitle("NO SONGS!!!");
            return;
        }

        switch (v.getId()) {
            case R.id.btnPlay:
                if (!check_frist_play_music) {
                    position = new Random().nextInt(arrMusic.size());
                    check_frist_play_music = true;
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    Log.d("FFF", "check_frist_play_music");
                    startPlayer();
                } else {
                    if (mediaPlayer.isPlaying()) {
                        Log.d("FFF", "isPlaying");
                        objectAnimator.pause();
                        musicAdapter.setCheckPause(true); // pause VuMeterView (heart)
                        btnPlay.setImageResource(R.drawable.ic_play);
                        mediaPlayer.pause();
                    } else {
                        Log.d("FFF", "else");
                        objectAnimator.resume();
                        musicAdapter.setCheckPause(false); // pause VuMeterView (heart)
                        btnPlay.setImageResource(R.drawable.ic_pause);
                        mediaPlayer.start();
                    }
                }
                break;
            case R.id.btnPre:
                btnPlay.setImageResource(R.drawable.ic_pause);
                if (!check_frist_play_music) {
                    position = new Random().nextInt(arrMusic.size());
                    check_frist_play_music = true;
                    btnPlay.setImageResource(R.drawable.ic_pause);
                } else {
                    if (btnLoop.getTag().equals(false) && btnRandom.getTag().equals(false)) {
                        if (position == 0) {
                            position = arrMusic.size() - 2;
                        } else if (position == 1) {
                            position = -1;
                        } else
                            position -= 2;
                    }
                    if (btnLoop.getTag().equals(true)) {
                        position--;
                        if (position <= -1) {
                            position = arrMusic.size() - 1;
                        }
                    }
                }
                startPlayer();

                break;
            case R.id.btnLoop:
                if (btnLoop.getTag().equals(false)) {
                    btnLoop.setImageResource(R.drawable.ic_sync_blu);
                    btnLoop.setTag(true);
                } else {
                    btnLoop.setImageResource(R.drawable.ic_sync);
                    btnLoop.setTag(false);
                }
                btnRandom.setImageResource(R.drawable.ic_random);
                btnRandom.setTag(false);

                break;
            case R.id.btnNext:
                btnPlay.setImageResource(R.drawable.ic_pause);
                if (!check_frist_play_music) {
                    position = new Random().nextInt(arrMusic.size());
                    check_frist_play_music = true;
                    btnPlay.setImageResource(R.drawable.ic_pause);
                } else {
                    if (btnLoop.getTag().equals(true)) {
                        position++;
                        if (position >= arrMusic.size()) {
                            position = 0;
                        }
                    }
                }
                startPlayer();

                break;
            case R.id.btnRandom:
                if (btnRandom.getTag().equals(false)) {
                    btnRandom.setImageResource(R.drawable.ic_random_blu);
                    btnRandom.setTag(true);
                } else {
                    btnRandom.setImageResource(R.drawable.ic_random);
                    btnRandom.setTag(false);
                }
                btnLoop.setImageResource(R.drawable.ic_sync);
                btnLoop.setTag(false);

                break;
            case R.id.imgFav:
                if (btnFav.getTag().equals(false)) {
                    btnFav.setTag(true);
                    btnFav.setImageResource(R.drawable.ic_favorite_red);
                    arrMusic.get(position).setCheck_fav(true);
                    arrFav.add(arrMusic.get(position));
                    Toast.makeText(this, "Đã thêm '" + arrMusic.get(position).getNameSong() + "' vào Yêu Thích", Toast.LENGTH_SHORT).show();
                    if (KEY_LIST_SONG.equals("playlists")) {
                        arrSong.get(getIdMusic(arrSong)).setCheck_fav(true);
                        arrPlaylist.get(position).setCheck_fav(true);
                    } else if (KEY_LIST_SONG.equals("songs")) {
                        arrSong.get(position).setCheck_fav(true);
                    }
                } else {
                    btnFav.setTag(false);
                    btnFav.setImageResource(R.drawable.ic_favorite);
                    arrMusic.get(position).setCheck_fav(false);
                    Toast.makeText(this, "Đã xóa '" + arrMusic.get(position).getNameSong() + "' từ Yêu Thích", Toast.LENGTH_SHORT).show();
                    if (KEY_LIST_SONG.equals("playlists")) {
                        arrFav.remove(getIdMusic(arrFav));
                        arrSong.get(getIdMusic(arrSong)).setCheck_fav(false);
                    } else if (KEY_LIST_SONG.equals("songs")) {
                        arrSong.get(position).setCheck_fav(false);
                        arrFav.remove(getIdMusic(arrPlaylist));
                    } else {
                        arrSong.get(getIdMusic(arrSong)).setCheck_fav(false);
                        Fragment fragment = viewPagerAdapter.getItem(1);
                        if (fragment instanceof ListSongFragment) {
                            ((ListSongFragment) fragment).setArrMusic(position);
                        }
                        //arrMusic.remove(position);
                        arrFav.remove(position);
                        check_fav_playlist();
                    }
                }
                break;
            case R.id.imgPlaylist:
                if (btnPlaylist.getTag().equals(false)) {
                    btnPlaylist.setTag(true);
                    btnPlaylist.setImageResource(R.drawable.ic_playlist_blu);
                    arrMusic.get(position).setCheck_playlist(true);
                    arrPlaylist.add(arrMusic.get(position));
                    Toast.makeText(this, "Đã thêm '" + arrMusic.get(position).getNameSong() + "' vào Playlist", Toast.LENGTH_SHORT).show();
                    if (KEY_LIST_SONG.equals("favorites")) {
                        arrFav.get(position).setCheck_playlist(true);
                        arrSong.get(getIdMusic(arrSong)).setCheck_playlist(true);
                    } else if (KEY_LIST_SONG.equals("songs")) {
                        arrSong.get(position).setCheck_playlist(true);
                    }
                } else {
                    btnPlaylist.setTag(false);
                    btnPlaylist.setImageResource(R.drawable.ic_playlist);
                    arrMusic.get(position).setCheck_playlist(false);
                    Toast.makeText(this, "Đã xóa '" + arrMusic.get(position).getNameSong() + "' từ Playlist", Toast.LENGTH_SHORT).show();
                    if (KEY_LIST_SONG.equals("favorites")) {
                        arrPlaylist.remove(getIdMusic(arrPlaylist));
                        arrSong.get(getIdMusic(arrSong)).setCheck_playlist(false);
                    } else if (KEY_LIST_SONG.equals("songs")) {
                        arrSong.get(position).setCheck_playlist(false);
                        arrPlaylist.remove(getIdMusic(arrPlaylist));
                    } else {
                        arrSong.get(getIdMusic(arrSong)).setCheck_playlist(false);
                        Fragment fragment = viewPagerAdapter.getItem(1);
                        if (fragment instanceof ListSongFragment) {
                            ((ListSongFragment) fragment).setArrMusic(position);
                        }
                        //arrMusic.remove(position);
                        arrPlaylist.remove(position);
                        check_fav_playlist();
                    }
                }
                break;
        }
    }

    // kiểm tra arr đưa vào có trùng với id bài hát hiện tại
    public int getIdMusic(ArrayList<Music> arr) {
        String idMusic = arrMusic.get(position).getId();
        for (int i = 0; i < arr.size(); i++) {
            if (idMusic.equals(arr.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public void startPlayer() {
        stopPlayer();
        check_onBackPressed = false;
        if (btnLoop.getTag().equals(false) && btnRandom.getTag().equals(false)) {
            position++;
            if (position >= arrMusic.size()) {
                position = 0;
            }
        }

        if (btnRandom.getTag().equals(true)) {
            position = new Random().nextInt(arrMusic.size());
        }
        if (btnLoop.getTag().equals(true)) {
            if (position >= arrMusic.size())
                position = 0;
            Log.d("FFF", "hello 3");
        }

        if (arrMusic.size() == 0) {
            toolbar.setTitle("NO SONGS!!!");
            objectAnimator.cancel();
            objectAnimator.clone();
            check_onBackPressed = true;
            Toast.makeText(this, "NO SONGS!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Music music = arrMusic.get(position);
        if (!new File(music.getPath()).exists()) {
            Toast.makeText(this, "Bài hát '" + music.getNameSong() + "' đã bị xóa khỏi bộ nhớ!", Toast.LENGTH_SHORT).show();
            arrMusic.remove(position);
            delete_arrMusic_local(position); // xóa arrSong(arrSong, arrFav, arrPlaylist) gốc

            Fragment fragment = viewPagerAdapter.getItem(0);
            if (fragment instanceof ListSongFragment) {
                ((ListSongFragment) fragment).setArrMusic(position);
                startPlayer();
            }
            return;
        }

        //String lyric_song= path_lyric_MP3+music.getId(); //đường dẫn lời nhạc zingmp3 bài hát hiện tại

        Fragment fragment = viewPagerAdapter.getItem(2);
        if (fragment instanceof LyricFragment) {
            ((LyricFragment) fragment).add_lyric_song(getArrLyric(music.getId(), music));
        }

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(music.getPath()));
        } catch (IOException e) {
            Log.d("AAAA", e.getMessage() + "danh_1");
        }


        try {
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            //Toast.makeText(this, "prepare ERROR", Toast.LENGTH_SHORT).show();
            Log.d("AAAA", e.getMessage() + "danh_2");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startPlayer();
            }
        });

        musicAdapter.setPosition(position);
        recyclerView.scrollToPosition(position);

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("DDD", "could not get audio focus");
            // could not get audio focus.
        } else {
            mediaPlayer.start();
            Log.d("DDD", "Start playback");
        }

        check_fav_playlist();
        toolbar.setTitle(music.getNameSong());
        DiscFragment.CreateMusic(position, KEY_LIST_SONG);

        setTimeTotal();
        updateTimeSong();

        Log.d("FFF", "end StartPlayer");
    }

    // Create lyric(arrList)
    public ArrayList<String> getArrLyric(String id, Music music) {
        ArrayList<String> arrLyric = new ArrayList<>();
        File file_lyric_zing = new File(path_lyric_MP3 + id);
        File file_lyric_thd_music = new File(path_lyric_THD + id);

        Log.d("LLL", "id: " + id);

        if (file_lyric_zing.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file_lyric_zing));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.length()==0||line.charAt(1) != '0')
                        continue;
                    arrLyric.add(line.substring(10));
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file_lyric_thd_music.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file_lyric_thd_music));
                String line;
                while ((line = br.readLine()) != null) {
                    arrLyric.add(line.trim());
                }
                br.close();
            } catch (IOException e) {
                Log.d("MMM", e.getMessage());
                e.printStackTrace();
            }
        } else {
            arrLyric.add("Song: " + music.getNameSong());
            arrLyric.add("Artist: " + music.getNameSinger());
            arrLyric.add("No Lyric");
            arrLyric.add(getResources().getString(R.string.simle));
        }

        return arrLyric;
    }

    public void stopPlayer() {
        if (mediaPlayer != null) {
            //mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                musicAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    //even click item recyclerView
    @Override
    public void onInpuSent(int position) {
        this.position = position;

        this.position--;
        btnPlay.setImageResource(R.drawable.ic_pause);
        if (check_frist_play_music == false) {
            check_frist_play_music = true;
        }

        if (btnLoop.getTag().equals(true))
            this.position++;
        startPlayer();
    }

    // xóa arrSong(arrSong, arrFav, arrPlaylist) gốc
    public static void delete_arrMusic_local(int position) {
        Music song_delete = arrSong.get(position);
        arrSong.remove(position);
        if (arrFav.contains(song_delete)){
            Log.d("LLL","Why");
            arrFav.remove(song_delete);
        }
        if (arrPlaylist.contains(song_delete))
            arrPlaylist.remove(song_delete);
    }

    @Override
    public void onAudioFocusChange(int focusChange) { // check audio từ bên ngoài
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d("DDD", "AUDIOFOCUS_GAIN");
                // resume playback
//                if (mediaPlayer == null){ //initMediaPlayer();
//                    mediaPlayer=new MediaPlayer();
//                }
//                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
//                mediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d("DDD", "AUDIOFOCUS_LOSS");
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) {
                    check_onBackPressed = true; //
                    btnPlay.setImageResource(R.drawable.ic_play);
                    mediaPlayer.stop();
                }
                check_frist_play_music = false;
                mediaPlayer.release();
                mediaPlayer = null;

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d("DDD", "AUDIOFOCUS_LOSS_TRANSIENT");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d("DDD", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }
}
