package com.danhtran12797.thd.app_music_test.Activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.danhtran12797.thd.app_music_test.Model.Music;
import com.danhtran12797.thd.app_music_test.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class LoadActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static int REQUEST_CODE = 1997;

    private Button btnFav, btnPlaylist, btnSong, btnViet, btnAuMy, btnChauA;
    private Switch mSwitch;
    private RelativeLayout layout_on, layout_off;

    //private Dialog dialog_load_data;

    private long backPressedTime;
    private Toast backToast;

    public static final String SHARED_PREFERENCES_NAME = "MUSIC2019";
    public static String KEY = "music";
    public static String KEY_SONGS = "songs";
    public static final String KEY_PLAYLISTS = "playlists";
    public static final String KEY_FAVORITES = "favorites";

    public static ArrayList<Music> arrSong;

    private ArrayList<Music> arrFav;
    private ArrayList<Music> arrPlaylist;

    public ArrayList<String> arrID_Fav;
    public ArrayList<String> arrID_Playlist;

    SharedPreferences sharedPreferences = null;

    private Toolbar toolbar;
    private DrawerLayout drawer;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private View header;
    private TextView txt_name_user, txt_mail;
    private CircleImageView img_avatar;

    private boolean isLogin = false;

    public static String url_avatar;
    public static String name_user;
    private Intent intent;

    private PrettyDialog pDialog;

    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load);

        Log.d("CCC", "onCreate: ");

        MobileAds.initialize(this,
                "ca-app-pub-3584305127333859~7316781574");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        initView();
        createDrawer();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        arrSong = new ArrayList<>();

        // show dialog full screen to load data
//        dialog_load_data = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        dialog_load_data.setContentView(R.layout.layout_dialog_load_data);

        // check permission
        if (ContextCompat.checkSelfPermission(LoadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            new LoadSong().execute();
        }

        //event change switch
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isOnline()) {
                        if (!isLogin) {
                            mSwitch.setChecked(false);
                            pDialog = new PrettyDialog(LoadActivity.this)
                                    .setTitle("Bạn có muốn đăng nhập?")
                                    .setMessage("Nghe nhạc online bạn cần đăng nhập")
                                    .setIcon(R.drawable.ic_info)
                                    .setIconTint(R.color.pdlg_color_green)
                                    .addButton(
                                            "Đồng ý",                    // button text
                                            R.color.pdlg_color_white,        // button text color
                                            R.color.pdlg_color_green,        // button background color
                                            new PrettyDialogCallback() {        // button OnClick listener
                                                @Override
                                                public void onClick() {
                                                    pDialog.dismiss();
                                                    Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                    )
                                    .addButton(
                                            "Hủy",
                                            R.color.pdlg_color_white,
                                            R.color.pdlg_color_gray,

                                            new PrettyDialogCallback() {
                                                @Override
                                                public void onClick() {
                                                    mSwitch.setChecked(false);
                                                    pDialog.dismiss();
                                                }
                                            }
                                    );
                            pDialog.show();

                        } else {
                            layout_on.setVisibility(View.VISIBLE);
                            layout_off.setVisibility(View.GONE);
                            toolbar.setTitle("Music Online");
                        }
                    } else {
                        mSwitch.setChecked(false);
                        Snackbar.make(layout_off, getString(R.string.no_internet_connect), Snackbar.LENGTH_LONG)
                                .setAction("Bỏ qua", new View.OnClickListener() { //RETRY
                                    @Override
                                    public void onClick(View view) {
                                    }
                                }).show();
                    }
                } else {
                    layout_on.setVisibility(View.GONE);
                    layout_off.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Music Offline");
                }
            }
        });
    }

    private void createDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        txt_mail = header.findViewById(R.id.txt_mail_nav);
        txt_name_user = header.findViewById(R.id.txt_name_user_nav);
        img_avatar = header.findViewById(R.id.img_avatar_nav);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        layout_on = findViewById(R.id.layout_on);
        layout_off = findViewById(R.id.layout_off);

        btnFav = findViewById(R.id.btnFav);
        btnPlaylist = findViewById(R.id.btnPlaylists);
        btnSong = findViewById(R.id.btnSongs);
        btnViet = findViewById(R.id.btnVietNam);
        btnAuMy = findViewById(R.id.btnAuMy);
        btnChauA = findViewById(R.id.btnChauA);

        mSwitch = findViewById(R.id.sw_On_Off);

        btnFav.setOnClickListener(this);
        btnSong.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);
        btnViet.setOnClickListener(this);
        btnChauA.setOnClickListener(this);
        btnAuMy.setOnClickListener(this);
    }

    private void animate() {
        ViewGroup container = findViewById(R.id.layout_off);

        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (v instanceof Button) {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(500);
                viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
            }
        }
    }

    public void display_user(DocumentSnapshot document, String mail_user) {
        url_avatar = document.getString("image"); //gửi qua intent...OnlineActivity
        name_user = document.getString("name"); //gửi qua intent...OnlineActivity
        Picasso.get()
                .load(url_avatar)
                .placeholder(R.drawable.image_load)
                .error(R.drawable.ic_error_outline)
                .into(img_avatar);
        txt_name_user.setText(name_user);
        txt_mail.setText(mail_user);
    }

    public boolean check_is_login() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            display_user(document, currentUser.getEmail());
                        } else {
//                            dialog_load_data.dismiss();
                            Intent intent = new Intent(LoadActivity.this, AccountActivity.class);
                            startActivity(intent);
                            //finish();
                        }
                    } else {
                        Toast.makeText(LoadActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        } else {
            background_no_current_user();
            return false;
        }
    }

    public void background_no_current_user() {
        layout_on.setVisibility(View.GONE);
        layout_off.setVisibility(View.VISIBLE);
        toolbar.setTitle("Music Offline");
        mSwitch.setChecked(false);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CCC", "onStart");
        if (!isOnline()) {
            layout_on.setVisibility(View.GONE);
            layout_off.setVisibility(View.VISIBLE);
            toolbar.setTitle("Music Offline");
            mSwitch.setChecked(false);
        }
        isLogin = check_is_login();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("CCC", "onRestart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CCC", "onResume");
        btnSong.setText("Bài hát(" + arrSong.size() + ")");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("CCC", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CCC", "onDestroy: ");
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
                        if (!new File(path).getParentFile().getName().equals("THD Music")) {
                            boolean check_fav = false;
                            boolean check_playlist = false;

                            if (arrID_Fav.contains(id))
                                check_fav = true;
                            if (arrID_Playlist.contains(id))
                                check_playlist = true;

                            mp3Files.add(new Music(nameSong, nameSinger, true, path, id, check_fav, check_playlist));
                        }
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
                    //String parent = file.getParentFile().getName();

                    String arr[] = file.getName().split("_");
                    String id = arr[2].substring(1, arr[2].indexOf("."));
                    boolean check_fav = false;
                    boolean check_playlist = false;
                    if (arrID_Fav.contains(id))
                        check_fav = true;
                    if (arrID_Playlist.contains(id))
                        check_playlist = true;

                    fileList.add(new Music(arr[0], arr[1], false, file.getPath(), id, check_fav, check_playlist));
                }
            }
            return fileList;
        } catch (Exception e) {
            return fileList;
        }

    }

    // lấy chuõi id fav(or playlist) gán vào 1 danh sách String
    public ArrayList<String> get_arr_id(String id_song) {
        String arr[] = id_song.split("-");
        Log.d("III", "arr[]: " + arr.length);
        return new ArrayList<>(Arrays.asList(arr));
    }

    // lấy danh sách String id của fav(or playtlist) gán vào danh sách bài hát Fav(or Playlist)
    public ArrayList<Music> create_fav_playlist_music(ArrayList<String> arrayList) {
        ArrayList<Music> arrTemp = new ArrayList<>();
        for (String id : arrayList) {
            for (Music music : arrSong) {
                if (id.equals(music.getId())) {
                    arrTemp.add(music);
                    break;
                }
            }
        }
        return arrTemp;
    }

    public void addMusic() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (!(sharedPreferences.contains(KEY_FAVORITES) && sharedPreferences.contains(KEY_PLAYLISTS))) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_FAVORITES, "");
            editor.putString(KEY_PLAYLISTS, "");
            editor.apply();
        }
        String fav = sharedPreferences.getString(KEY_FAVORITES, "");
        String playlist = sharedPreferences.getString(KEY_PLAYLISTS, "");

        arrID_Fav = get_arr_id(fav);
        arrID_Playlist = get_arr_id(playlist);

        arrSong = new ArrayList<>();
        arrSong = getPlayList(Environment.getExternalStorageDirectory() + "/THD Music");

        Collections.sort(arrSong, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                return compare(new File(o2.getPath()).lastModified(), new File(o1.getPath()).lastModified());
            }

            private int compare(long lastModified, long lastModified1) {
                return (int) (lastModified - lastModified1);
            }
        });


        arrSong.addAll(getPlayList(Environment.getExternalStorageDirectory() + "/Zing MP3"));
        arrSong.addAll(scanDeviceForMp3Files());

        if (fav.equals(""))
            arrFav = new ArrayList<>();
        else {
            arrFav = create_fav_playlist_music(arrID_Fav);
        }
        if (playlist.equals(""))
            arrPlaylist = new ArrayList<>();
        else {
            arrPlaylist = create_fav_playlist_music(arrID_Playlist);
        }
    }

    public void Exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("THD Music");
        alertDialogBuilder
                .setMessage("Bạn có muốn thoát ứng dụng?")
                .setCancelable(false)
                .setPositiveButton("Đồng ý",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_account:
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_infor_app:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông tin ứng dụng");
                builder.setIcon(R.drawable.music_load_data);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_infor_app, null);
                builder.setView(dialogView);

                TextView txtDeveloper = dialogView.findViewById(R.id.txtDeveloper);
                txtDeveloper.setMovementMethod(LinkMovementMethod.getInstance());

                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            case R.id.menu_rate_app:
                rateMe();
                break;
            case R.id.menu_logout_app:
                Toast.makeText(this, "Đã đăng xuất tài khoản " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                background_no_current_user();
                isLogin = false;
                break;
            case R.id.menu_exit_app:
                Exit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class LoadSong extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog_load_data.show();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            animate();
//            dialog_load_data.dismiss();
            btnFav.setText("Yêu thích(" + arrFav.size() + ")");
            btnPlaylist.setText("Playlist(" + arrPlaylist.size() + ")");
            btnSong.setText("Bài hát(" + arrSong.size() + ")");
        }

        @Override
        protected String doInBackground(Void... voids) {
            addMusic();
            return "Good";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã được cho phép", Toast.LENGTH_SHORT).show();
                new LoadSong().execute();
            } else {
                arrSong = new ArrayList<>();
                arrFav = new ArrayList<>();
                arrPlaylist = new ArrayList<>();

                btnSong.append("(0)");
                btnPlaylist.append("(0)");
                btnFav.append("(0)");

                Toast.makeText(this, "Từ chối xác nhận quyền", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Bạn không thể nghe nhạc vì bạn chưa xác nhận quyền!", Toast.LENGTH_SHORT).show();
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
            backToast = Toast.makeText(getBaseContext(), "Nhấn lại lần nữa để thoát", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Boolean check_change_share = data.getBooleanExtra("check_change_share", false);
            btnSong.setText("Bài hát(" + arrSong.size() + ")");
            if (check_change_share) {
                arrFav.clear();
                arrPlaylist.clear();
                for (Music music : arrSong) {
                    if (music.isCheck_fav()) {
                        arrFav.add(music);
                    }
                    if (music.isCheck_playlist()) {
                        arrPlaylist.add(music);
                    }
                }
                btnPlaylist.setText("Playlist(" + arrPlaylist.size() + ")");
                btnFav.setText("Yêu thích(" + arrFav.size() + ")");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSongs:
                intent = new Intent(this, OfflineActivity.class);
                KEY_SONGS = "songs";
                intent.putExtra(KEY, arrSong);
                intent.putExtra(KEY_SONGS, KEY_SONGS);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnPlaylists:
                intent = new Intent(this, OfflineActivity.class);
                KEY_SONGS = "playlists";
                intent.putExtra(KEY, arrPlaylist);
                intent.putExtra(KEY_SONGS, KEY_PLAYLISTS);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnFav:
                intent = new Intent(this, OfflineActivity.class);
                KEY_SONGS = "favorites";
                intent.putExtra(KEY, arrFav);
                intent.putExtra(KEY_SONGS, KEY_FAVORITES);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnVietNam:
                intent = new Intent(this, OnlineActivity.class);
                intent.putExtra(KEY, 0);
                startActivity(intent);
                break;
            case R.id.btnAuMy:
                intent = new Intent(this, OnlineActivity.class);
                intent.putExtra(KEY, 1);
                startActivity(intent);
                break;
            case R.id.btnChauA:
                intent = new Intent(this, OnlineActivity.class);
                intent.putExtra(KEY, 2);
                startActivity(intent);
                break;
        }
    }
}
