package com.danhtran12797.thd.app_music2019.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danhtran12797.thd.app_music2019.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText txt_profile_name;
    private CircleImageView img_profile;
    private Button btnSave;
    private TextView txt_mail_user;
    private ProgressBar setupProgress;

    private RelativeLayout layout;

    private boolean check_document;

    private Uri mainImageURI = null;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private PrettyDialog pDialog;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        initView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tài khoản của tôi");

        txt_mail_user.setText(getString(R.string.email_id_user, "danhtran12797", "12345678"));

        PushDownAnim.setPushDownAnimTo(btnSave, img_profile)
                .setScale(MODE_SCALE, 0.8f)
                .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
                .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setOnClickListener(this);

        final FirebaseUser user = mAuth.getCurrentUser();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    txt_mail_user.setText(getString(R.string.email_id_user, user.getEmail(), user.getUid()));

                    if (document.exists()) {
                        check_document = true;
                        Toast.makeText(AccountActivity.this, "Chào mừng " + document.getString("name"), Toast.LENGTH_LONG).show();
                        txt_profile_name.setText(document.getString("name"));
                        Picasso.get()
                                .load(document.getString("image"))
                                .placeholder(R.drawable.image_load)
                                .error(R.drawable.ic_error_outline)
                                .into(img_profile);
                    } else {
                        check_document = false;
                        showDialog(user.getEmail(), "Bạn cần nhập tên và có thể click vào 'con mèo' để chọn ảnh đại diện. Sau đó click SAVE",R.color.pdlg_color_green);
                    }
                } else {
                    Toast.makeText(AccountActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                img_profile.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("DDD",error.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                mAuth.signOut();
                finish();
                break;
            case R.id.menu_home:
                if (!check_document) {
                    Toast.makeText(this, "Vui lòng nhập thông tin của bạn", Toast.LENGTH_SHORT).show();
                    return false;
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_account);
        txt_profile_name = findViewById(R.id.txt_profile_name);
        img_profile = findViewById(R.id.img_profile);
        btnSave = findViewById(R.id.btnSave);
        txt_mail_user = findViewById(R.id.txt_mail_user);
        setupProgress = findViewById(R.id.setup_progress);
        layout = findViewById(R.id.layout_account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                final String user_name = txt_profile_name.getText().toString().trim();
                if (!validateForm(user_name)) {
                    return;
                }

                btnSave.setEnabled(false);
                setupProgress.setVisibility(View.VISIBLE);

                if (!isOnline()) {
                    Snackbar.make(layout, getString(R.string.no_internet_connect), Snackbar.LENGTH_LONG)
                            .setAction("Bỏ qua", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                    setupProgress.setVisibility(View.INVISIBLE);
                    return;
                }


                final String user_id = mAuth.getCurrentUser().getUid();
                final StorageReference image_path = mStorageRef.child("profile_image").child(user_id + ".png");

                if(mainImageURI!=null){ // user có chọn ảnh
                    File file=new File(mainImageURI.getPath());
                    try {
                        compressedImageFile = new Compressor(this).compressToBitmap(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{ //user k chọn ảnh
                    img_profile.setDrawingCacheEnabled(true);
                    img_profile.buildDrawingCache();
                    compressedImageFile = ((BitmapDrawable) img_profile.getDrawable()).getBitmap();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = image_path.putBytes(data);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return image_path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri download_uri = task.getResult();

                            Map<String, String> user = new HashMap<>();
                            user.put("name", user_name);
                            user.put("image", download_uri.toString());

                            db.collection("users").document(user_id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Thông tin của bạn đã được cập nhật", Toast.LENGTH_SHORT).show();

                                        finish();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(AccountActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                    btnSave.setEnabled(true);
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(AccountActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
            case R.id.img_profile:
                BringImagePicker();
                break;
        }
    }

    private boolean validateForm(String name) {
        boolean valid = true;

        if (name.isEmpty()) {
            txt_profile_name.setError("Không được bỏ trống");
            valid = false;
        } else if (name.length() > 15) {
            txt_profile_name.setError("Đặt tên không quá 15 ký tự");
            valid = false;
        } else {
            txt_profile_name.setError(null);
        }

        return valid;
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!check_document) {
            Toast.makeText(this, "Bạn cần lưu lại thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog(String title, String message, int color) {
        pDialog = new PrettyDialog(this)
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
                                txt_profile_name.requestFocus();
                                pDialog.dismiss();
                            }
                        }
                );
        pDialog.show();
    }
}
