package com.danhtran12797.thd.app_music2019.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
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
import com.victor.loading.rotate.RotateLoading;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.ANIM_ITEM_DURATION;
import static com.danhtran12797.thd.app_music2019.Activity.LoadActivity.STARTUP_DELAY;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText txtReg_email;
    private EditText txtReg_conf_pass;
    private EditText txtReg_pass;
    private Button btnRegister;
    private TextView txtLogin_now;
    private Intent intent;

    private RelativeLayout layout;

    private FirebaseAuth mAuth;

    private RotateLoading rotateLoading;

    private PrettyDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_register);

        initView();
        animate();

        rotateLoading = findViewById(R.id.rotateloading);

        mAuth = FirebaseAuth.getInstance();

        txtLogin_now.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void animate() {

        ImageView logoImageView = (ImageView) findViewById(R.id.img_register);
        LinearLayout container = findViewById(R.id.container_register);
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

    private void createAccount(String email, String password) {
        if (!validateForm() || !check_password_match()) {
            return;
        }

        rotateLoading.start();
        btnRegister.setEnabled(false);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                            intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
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

                        rotateLoading.stop();
                        btnRegister.setEnabled(true);
                    }
                });
        // [END create_user_with_email]
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

    private boolean validateForm() {
        boolean valid = true;

        String email = txtReg_email.getText().toString().trim();
        if (email.isEmpty()) {
            txtReg_email.setError("Không được bỏ trống");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtReg_email.setError("Định dạng mail không hợp lệ");
            valid = false;
        } else {
            txtReg_email.setError(null);
        }

        String password = txtReg_pass.getText().toString();
        if (password.isEmpty()) {
            txtReg_pass.setError("Không được bỏ trống");
            valid = false;
        } else if (password.length() < 6) {
            // Password should be at least 6 characters
            txtReg_pass.setError("Mật khẩu phải ít nhất 6 ký tự");
            valid = false;
        } else {
            txtReg_pass.setError(null);
        }

        return valid;
    }

    public boolean check_password_match() {
        if (!txtReg_pass.getText().toString().equals(txtReg_conf_pass.getText().toString())) {
            //Those passwords didn't match. Try again
            txtReg_conf_pass.setError("Xác nhận mật khẩu không chính xác");
            return false;
        } else
            txtReg_pass.setError(null);
        return true;
    }

    private void initView() {
        txtReg_email = findViewById(R.id.txtReg_email);
        txtReg_pass = findViewById(R.id.txtReg_pass);
        txtReg_conf_pass = findViewById(R.id.txtReg_conf_pass);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin_now = findViewById(R.id.txtLogin_now);

        layout = findViewById(R.id.layout_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                createAccount(txtReg_email.getText().toString(), txtReg_pass.getText().toString());
                break;
            case R.id.txtLogin_now:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
