package com.danhtran12797.thd.app_music2019.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danhtran12797.thd.app_music2019.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.victor.loading.rotate.RotateLoading;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.ANIM_ITEM_DURATION;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.STARTUP_DELAY;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText txtEmail;
    private EditText txtPass;
    private TextView txtReg_now;
    private Button btnLogin;
    private RelativeLayout layout;

    private Intent intent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RotateLoading rotateLoading;

    private PrettyDialog pDialog;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_login);

        showDialog("THD Music", getString(R.string.example_login), R.color.pdlg_color_blue);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initView();

        animate();

        btnLogin.setOnClickListener(this);
        txtReg_now.setOnClickListener(this);
    }

    private void showDialog(String title, String message, int color) {
        pDialog = new PrettyDialog(this)
                .setTitle(title)
                .setTitleColor(color)
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setIconTint(color);
        pDialog.show();
    }

    private void animate() {

        ImageView logoImageView = (ImageView) findViewById(R.id.img_login);
        LinearLayout container = findViewById(R.id.container_login);
        Resources r = getResources();

        ViewCompat.animate(logoImageView)
                .translationYBy(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        -200f,
                        r.getDisplayMetrics()
                ))
                .scaleX(0.6f)
                .scaleY(0.6f)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        ViewCompat.animate(container)
                .translationYBy(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        -300f,
                        r.getDisplayMetrics()
                ))
                .setStartDelay(STARTUP_DELAY * 2)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    private void initView() {
        rotateLoading = findViewById(R.id.rotateloading);
        layout = findViewById(R.id.layout_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        txtReg_now = findViewById(R.id.txtReg_now);

    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        rotateLoading.start();
        btnLogin.setEnabled(false);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            check_data_user(user.getUid());
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        } else { //đăng nhập sai, hoặc k có Internet
                            if (isOnline()) {
                                showDialog(task.getException().getMessage());
                                Log.d("TTT", task.getException().getMessage());
                            } else {
                                Snackbar.make(layout, getString(R.string.no_internet_connect), Snackbar.LENGTH_LONG)
                                        .setAction("Bỏ qua", new View.OnClickListener() { //RETRY
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        }).show();
                            }

                        }

                        // [START_EXCLUDE]
                        rotateLoading.stop();
                        btnLogin.setEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void check_data_user(String id) {
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        intent=new Intent(LoginActivity.this,LoadActivity.class);
//                        startActivity(intent);
                        finish();
                    } else {
                        intent = new Intent(LoginActivity.this, AccountActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    intent = new Intent(LoginActivity.this, AccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = txtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            //Field can't be empty
            txtEmail.setError("Không được bỏ trống");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //Please enter a valid email address
            txtEmail.setError("Định dạng mail k hợp lệ");
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String password = txtPass.getText().toString();
        if (password.isEmpty()) {
            txtPass.setError("Không được bỏ trống");
            valid = false;
        } else {
            txtPass.setError(null);
        }

        return valid;
    }

    private void showDialog(String message) {
        pDialog = new PrettyDialog(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(message)
                .setIcon(R.drawable.ic_info)
                .setIconTint(R.color.pdlg_color_blue)
                .addButton(
                        "OK",                    // button text
                        R.color.pdlg_color_white,        // button text color
                        R.color.pdlg_color_blue,        // button background color
                        new PrettyDialogCallback() {        // button OnClick listener
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        }
                );
        pDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                signIn(txtEmail.getText().toString(), txtPass.getText().toString());
                break;
            case R.id.txtReg_now:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
