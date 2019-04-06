package com.danhtran12797.thd.app_music2019.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.danhtran12797.thd.app_music2019.Model.Background;
import com.danhtran12797.thd.app_music2019.Model.Music;
import com.danhtran12797.thd.app_music2019.Model.MusicOn;
import com.danhtran12797.thd.app_music2019.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thekhaeng.pushdownanim.PushDownAnim;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import io.gresse.hugo.vumeterlibrary.VuMeterView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.KEY;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.arrSong;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.name_user;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.url_avatar;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OnlineActivity extends BaseActivity implements View.OnClickListener, AudioManager.OnAudioFocusChangeListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @SuppressLint("RestrictedApi")
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            layout_anim.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
    private FloatingActionButton fab;

    Spinner spinner;
    ArrayList<String> arrCategoryMusic;
    ArrayAdapter<String> adapter;

    private static final int STORAGE_PERMISSION_CODE_WRITE = 2;

    private MusicOn song_download = null;

    private TextView txt_name_song;
    private TextView txt_name_singer;
    private TextView txt_name_user;
    private CircleImageView imgAvatar;
    // private Toolbar toolbar;
    private LinearLayout layout_anim; // layout home, spinner, account

    private FrameLayout layout; // layout main(background)

    private Switch mSwitch;
    private ImageView img_off_music;
    private ImageView img_play_music;
    private ImageView img_random_music;
    private ImageView img_anim;
    private ImageView img_account;
    private ImageView img_home;

    private ImageView img_cycle;

    private MediaPlayer mediaPlayer = null;

    //private Animation animation;

    private int check_play_stop = 0;
    private boolean check_anim = true;

    private StarAnimationView mAnimationView;
    private LinearLayout layout_animation_view;
    private AVLoadingIndicatorView avi; //dialog load music ...
    public PrettyDialog prettyDialog;

    private String url = null;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRef1;

    //private ArrayList<MusicOn> arrMusicOn;
    private ArrayList<MusicOn> arrMusicVN;
    private ArrayList<MusicOn> arrMusicAu;
    private ArrayList<MusicOn> arrMusicA;

    private ArrayList<Background> arrBackground;

    private ArrayList<String> arrFlower;
    private ArrayList<String> arrCycle;

    public static int id_start_anim; //id background layout main

    private Intent intent;

    String name_path_song;

    AudioManager audioManager;

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    private SeekBar seekBar;
    private boolean check_onBackPressed = false;
    private VuMeterView mVuMeterView;
    Switch switch_sk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_online);

        mediaPlayer = new MediaPlayer();

        switch_sk = findViewById(R.id.switch_sk);
        mVuMeterView = findViewById(R.id.vumeter_on);
        seekBar = findViewById(R.id.seekBar_on);

        switch_sk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    show_seekbar();
                } else {
                    hide_seekbar();
                }
            }
        });

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

        layout = findViewById(R.id.frame_layout);

        txt_name_user = findViewById(R.id.txt_name_user);
        txt_name_song = findViewById(R.id.txt_name_song);
        txt_name_singer = findViewById(R.id.txt_name_singer);
        imgAvatar = findViewById(R.id.imgAvatar);

        mSwitch = findViewById(R.id.swState);
        img_anim = findViewById(R.id.img_anim);
        img_anim.setEnabled(false);
        img_off_music = findViewById(R.id.img_baseline);
        img_play_music = findViewById(R.id.img_play_stop);
        img_random_music = findViewById(R.id.img_reload);
        img_account = findViewById(R.id.img_account);
        img_home = findViewById(R.id.img_home_online);

        img_cycle = findViewById(R.id.img_cycle);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        PushDownAnim.setPushDownAnimTo(img_anim, img_off_music, img_play_music, img_random_music, img_home, img_account)
                .setScale(MODE_SCALE, 0.8f)
                .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setOnClickListener(this);

        layout_anim = findViewById(R.id.layout_anim);
        fab = findViewById(R.id.fab);

        String arr[] = getResources().getStringArray(R.array.lstFlower);
        arrFlower = new ArrayList<>(Arrays.asList(arr));

        String arr1[] = getResources().getStringArray(R.array.arr_cycle);
        arrCycle = new ArrayList<>(Arrays.asList(arr1));

        spinner = findViewById(R.id.spinner);
        arrCategoryMusic = new ArrayList<>();
        arrCategoryMusic.add("VN");
        arrCategoryMusic.add("US-UK");
        arrCategoryMusic.add("K-POP");
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arrCategoryMusic);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                ((TextView) parent.getChildAt(0)).setTypeface(((TextView) parent.getChildAt(0)).getTypeface(), Typeface.BOLD);
                ((TextView) parent.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //mAnimationView = (StarAnimationView) findViewById(R.id.animated_view);
        layout_animation_view = findViewById(R.id.layout_animation_view); //contain ainmation
        layout_animation_view.removeAllViews();
        avi = findViewById(R.id.avi);
        avi.hide();

        //animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        //imgAvatar.startAnimation(animation);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_anim = true;
                    img_anim.setEnabled(true);
                    img_anim.setImageResource(R.drawable.ic_start_anim);
                    //imgAvatar.startAnimation(animation);
                    Random_animaton_view();
                } else {
                    imgAvatar.clearAnimation();
                    layout_animation_view.removeAllViews();
                    img_anim.setEnabled(false);
                    //imgAvatar.clearAnimation();
                }
            }
        });

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionForWriteExtertalStorage()) {

                    startDownload(song_download);
                } else {
                    ActivityCompat.requestPermissions((Activity) OnlineActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE_WRITE);
                }
            }
        });

        database = FirebaseDatabase.getInstance();
        myRef1 = database.getReference("2").child("data");
        myRef = database.getReference("3").child("data");

        arrMusicVN = new ArrayList<>();
        arrMusicAu = new ArrayList<>();
        arrMusicA = new ArrayList<>();

        //arrMusicOn = new ArrayList<>();
        arrBackground = new ArrayList<>();

        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Background background = dataSnapshot.getValue(Background.class);
                arrBackground.add(background);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MusicOn music_on = dataSnapshot.getValue(MusicOn.class);
                String id_category = music_on.getId_category();
                if (id_category.equals("0")) {
                    arrMusicVN.add(music_on);
                } else if (id_category.equals("1")) {
                    arrMusicAu.add(music_on);
                } else {
                    arrMusicA.add(music_on);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        intent = getIntent();
        if (intent != null) {
            //selected = intent.getIntExtra(KEY, 0);
            spinner.setSelection(intent.getIntExtra(KEY, 0));
            Picasso.get()
                    .load(url_avatar)
                    .placeholder(R.drawable.image_load)
                    .error(R.drawable.ic_error_outline)
                    .into(imgAvatar);
            txt_name_user.setText(name_user);
        }
    }

    public void show_seekbar() {
        seekBar.setVisibility(View.VISIBLE);
        mVuMeterView.setVisibility(View.VISIBLE);
        //mVuMeterView.resume(true);
    }

    public void hide_seekbar() {
        seekBar.setVisibility(View.GONE);
        mVuMeterView.setVisibility(View.GONE);
        //mVuMeterView.stop(true);
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
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        }, 100);
    }

    public void setTimeTotal() {
        seekBar.setMax(mediaPlayer.getDuration());
    }

    public class DownloadLyricFromURL extends AsyncTask<MusicOn, Void, Void> {

        @Override
        protected Void doInBackground(MusicOn... song_downs) {

            int count;
            try {
                URL url = new URL(song_downs[0].getDownload_url());
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory()
                        + "/THD Music/lyric/" + song_downs[0].getId());

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    // publishing the progress....
                    // After this onProgressUpdate will be called

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     */
    class DownloadSongFromURL extends AsyncTask<MusicOn, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(MusicOn... musicOns) {
            int count;
            try {
                URL url = new URL(musicOns[0].getSong_url());
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/THD Music/" + name_path_song);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            add_ArrSong(Environment.getExternalStorageDirectory() + "/THD Music/" + name_path_song);
            Toast.makeText(OnlineActivity.this, "Bài hát '" + song_download.getName_song() + "' đã được download thành công.", Toast.LENGTH_LONG).show();
        }

    }

    public boolean checkPermissionForWriteExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }


    public void startDownload(final MusicOn song) {
        if (song == null) {
            Toast.makeText(this, "Vui lòng bấm play bài hát, để tôi xác định bài hát cần download.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isOnline()) {
            Snackbar.make(layout, getString(R.string.no_internet_connect), Snackbar.LENGTH_LONG)
                    .setAction("Bỏ qua", new View.OnClickListener() { //RETRY
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
            return;
        }

        File dir_song = new File(Environment.getExternalStorageDirectory(), "THD Music");
        if (!dir_song.exists()) {
            dir_song.mkdirs(); // creates dirs THD Music
        }

        File dir_lyric = new File(Environment.getExternalStorageDirectory() + "/THD Music", "lyric");
        if (!dir_lyric.exists()) {
            dir_lyric.mkdirs(); // creates dirs lyric
        }

        name_path_song = song.getName_song() + "_" + song.getName_author() + "_-" + song.getId() + ".mp3"; // đường dẫn bài hát

        final File file_song_download = new File(Environment.getExternalStorageDirectory() + "/THD Music", name_path_song);

        // kiểm tra file có tồn tại
        if (file_song_download.exists()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("THD Music");
            builder.setMessage("Bài hát này đã có sẵn offline. Bạn có muốn tải lại?");
            builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (file_song_download.delete()) {
                        new DownloadSongFromURL().execute(song);
                    }
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //fabProgressCircle.hide();
                }
            });
            builder.show();

        } else {
            new DownloadSongFromURL().execute(song);
            new DownloadLyricFromURL().execute(song);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("FFF", "onStop");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã được cho phép", Toast.LENGTH_LONG).show();
                startDownload(song_download);
            } else {
                Toast.makeText(this, "Từ chối xác nhận quyền", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Hm...Bạn không thể download nhạc online được :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Random View Tại đây....

    public void Random_animaton_view() {
        Collections.shuffle(arrFlower);
        id_start_anim = getResources().getIdentifier(arrFlower.get(0), "drawable", getPackageName());

        layout_animation_view.removeAllViews();
        mAnimationView = new StarAnimationView(this, id_start_anim);
        layout_animation_view.addView(mAnimationView);
    }

    // Random background và set color nameSong, nameAuthor
    public void Random_background() {
        Collections.shuffle(arrBackground);
        if (arrBackground.size() == 0) {
            return;
        }
        final Background background = arrBackground.get(0);

        Picasso.get().load(background.getUrl()).into(new Target() {
            @SuppressLint("NewApi")
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable dr = new BitmapDrawable(bitmap);
                layout.setBackground(dr);

                if (background.getColor().equals("1")) {
                    txt_name_user.setTextColor(Color.WHITE);
                    txt_name_song.setTextColor(Color.WHITE);
                    txt_name_singer.setTextColor(Color.WHITE);
                } else {
                    txt_name_user.setTextColor(Color.BLACK);
                    txt_name_song.setTextColor(Color.BLACK);
                    txt_name_singer.setTextColor(Color.BLACK);
                }
                Log.d("PPP", "onBitmapLoaded");
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d("PPP", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d("PPP", "onPrepareLoad");
            }
        });
    }

    // Random vòng Avatar
    public void Random_cycle() {
        Collections.shuffle(arrCycle);
        int id_cycle = getResources().getIdentifier(arrCycle.get(0), "drawable", getPackageName()); // lấy id từ tên
        img_cycle.setImageResource(id_cycle);
    }

    // get url và set Text nameSong, author
    public void get_url_music() {

        int selected = spinner.getSelectedItemPosition();

        //arrMusicOn.clear();
        MusicOn music = null;

        if (selected == 0) {
            //arrMusicOn.addAll(arrMusicVN);
            if (arrMusicVN.size() == 0)
                return;
            Collections.shuffle(arrMusicVN);
            music = arrMusicVN.get(0);
        } else if (selected == 1) {
            //arrMusicOn.addAll(arrMusicAu);
            if (arrMusicAu.size() == 0)
                return;
            Collections.shuffle(arrMusicAu);
            music = arrMusicAu.get(0);
        } else {
            //arrMusicOn.addAll(arrMusicA);
            if (arrMusicA.size() == 0)
                return;
            Collections.shuffle(arrMusicA);
            music = arrMusicA.get(0);
        }

        song_download = music; // đường dẫn download file

        txt_name_song.setText(music.getName_song());
        txt_name_singer.setText(music.getName_author());

        url = music.getSong_url();
    }

    // check can start mediaPlayer or return error
    public boolean asyn_load_music(String url) {
        if (!isOnline() || arrMusicVN.size() == 0 || arrMusicAu.size() == 0 || arrMusicA.size() == 0) {
            Log.d("RRR", " come on danh");
            check_onBackPressed = true;
            return false;
        }

        stopPlayer(); //gán null mediaPlayer

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {

            mediaPlayer.setDataSource(url); //khởi tạo nhạc online

        } catch (IOException e) {
            return false;
        }

        try {
            mediaPlayer.prepare(); // might take long! (for buffering, etc)

        } catch (IOException e) {
            return false;
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new AsynTask_Start_MusicOn().execute();
            }
        });

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("DDD", "could not get audio focus");
            // could not get audio focus.
        } else {
            mediaPlayer.start();

            Log.d("DDD", "Start playback");
        }


        return true;
    }

    public void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pausePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        layout_anim.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        mControlsView.setVisibility(View.GONE);
        //fabProgressCircle.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint({"InlinedApi", "RestrictedApi"})
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_anim:
                if (check_anim == false) {
                    check_anim = true;
                    mAnimationView.resume();
                    img_anim.setImageResource(R.drawable.ic_start_anim);
                    //imgAvatar.startAnimation(animation);
                } else {
                    check_anim = false;
                    mAnimationView.pause();
                    img_anim.setImageResource(R.drawable.ic_stop_anim);
                    //imgAvatar.clearAnimation();
                }
                break;
            case R.id.img_baseline:
                check_onBackPressed = true;
                seekBar.setProgress(0);
                mVuMeterView.stop(true);

                check_play_stop = 0;
                img_play_music.setImageResource(R.drawable.ic_play);
                stopPlayer();
                txt_name_song.setText(getString(R.string.name_song_wecome));
                txt_name_singer.setText(getString(R.string.app_name));
                break;
            case R.id.img_play_stop:
                if (check_play_stop == 0) {
                    new AsynTask_Start_MusicOn().execute();
                } else if (check_play_stop == 1) {
                    mVuMeterView.pause();
                    pausePlayer();
                    img_play_music.setImageResource(R.drawable.ic_play);
                    check_play_stop = 2;
                } else {
                    mVuMeterView.resume(true);
                    mediaPlayer.start();
                    img_play_music.setImageResource(R.drawable.ic_pause);
                    check_play_stop = 1;
                }
                break;
            case R.id.img_reload:
                new AsynTask_Start_MusicOn().execute();
                break;
            case R.id.img_account:
                intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.img_home_online:
                finish();
                break;
        }
    }

    public void add_ArrSong(String path) {
        if (song_download != null) {
            arrSong.add(0, new Music(song_download.getName_song(), song_download.getName_author(), false, path, song_download.getId(), false, false));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.abandonAudioFocus(this);
        check_onBackPressed = true;
        stopPlayer();
        Log.d("TTT", "onDestroy");
    }

    public class AsynTask_Start_MusicOn extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mVuMeterView.stop(false);
            startAnim();
            get_url_music();
        }

        protected Boolean doInBackground(String... strings) {
            return asyn_load_music(url);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            stopAnim();

            img_play_music.setImageResource(R.drawable.ic_pause);
            if (aBoolean.equals(false)) {
                showDialog("THD Music", "Vui lòng kiểm tra Internet", R.color.pdlg_color_blue);

                check_play_stop = 0;
                img_play_music.setImageResource(R.drawable.ic_play);
                stopPlayer();
                txt_name_song.setText(getString(R.string.name_song_wecome));
                txt_name_singer.setText(getString(R.string.app_name));
            } else {
                Random_cycle();
                Random_background();
                mVuMeterView.resume(true);
                //show_seekbar();
                check_onBackPressed = false;
                setTimeTotal();
                updateTimeSong();
                if (mSwitch.isChecked()) {
                    if (check_anim)
                        Random_animaton_view();
                }
                check_play_stop = 1;
            }
            Log.d("TTT", "onPostExecute_load_song");
        }
    }

    void startAnim() {
        //avi.show();
        avi.smoothToShow();
        txt_name_singer.setVisibility(View.GONE);
        txt_name_song.setVisibility(View.GONE);

        img_off_music.setEnabled(false);
        img_play_music.setEnabled(false);
        img_random_music.setEnabled(false);
    }

    void stopAnim() {
        //avi.hide();
        avi.smoothToHide();
        txt_name_singer.setVisibility(View.VISIBLE);
        txt_name_song.setVisibility(View.VISIBLE);

        img_off_music.setEnabled(true);
        img_play_music.setEnabled(true);
        img_random_music.setEnabled(true);
    }

    void showDialog(String title, String message, int color) {
        prettyDialog = new PrettyDialog(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setIconTint(color)
                .addButton(
                        "OK",                    // button text
                        R.color.pdlg_color_white,        // button text color
                        color,        // button background color
                        new PrettyDialogCallback() {        // button OnClick listener
                            @Override
                            public void onClick() {
                                prettyDialog.dismiss();
                            }
                        }
                );
        prettyDialog.show();
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

                check_play_stop = 0;
                img_play_music.setImageResource(R.drawable.ic_play);
                check_onBackPressed = true;
                stopPlayer();
                txt_name_song.setText(getString(R.string.name_song_wecome));
                txt_name_singer.setText(getString(R.string.app_name));

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d("DDD", "AUDIOFOCUS_LOSS_TRANSIENT");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) {
                    img_play_music.setImageResource(R.drawable.ic_play);
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

    @Override
    protected void onStart() {
        super.onStart();
    }
}
