package com.wisnu.paktukang.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wisnu.paktukang.Constans;
import com.wisnu.paktukang.R;
import com.wisnu.paktukang.UserAreaActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.mEmail)EditText mEmail;
    @BindView(R.id.mPassword) EditText mPassword;
    @BindView(R.id.bLogin) Button mButtonLogin;
    @BindView(R.id.tvSignUp) TextView mSignUp;

//    private ProgressDialog mProgressDialog;

    private ProgressDialog mAuthProgressDialog;
    private Firebase firebase;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;


//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        if(savedInstanceState == null){
            Firebase.setAndroidContext(this);
        }
//      mAuth = FirebaseAuth.getInstance();
        firebase = new Firebase(Constans.FIREBASE_URL);
        mSignUp.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Tunggu");
        mAuthProgressDialog.setMessage("Autentifikasi..");
        mAuthProgressDialog.setCancelable(false);

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonLogin) {
            loginWithPassword();
        }
        if (v == mSignUp) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void loginWithPassword() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        email = email.trim();
        password = password.trim();

        if (email.equals("")) {
            mEmail.setError("Masukan email anda");
        } else if (password.equals("")) {
            mPassword.setError("Password ga boleh kosong");
        } else {
        mAuthProgressDialog.show();

            firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    if (authData!= null){
                        mAuthProgressDialog.dismiss();
                        String userId = authData.getUid();

//                        String userInfo = authData.toString();

                        mSharedPreferencesEditor.putString(Constans.KEY_UID, userId).apply();
                        Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    mAuthProgressDialog.dismiss();
                    switch (firebaseError.getCode()) {
                        case FirebaseError.INVALID_EMAIL:
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            // handle a non existing user
                            mEmail.setError(getString(R.string.salah_email));
                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            // handle an invalid password
                            showErrorToast(getString(R.string.salah_password));
                            break;
                        case FirebaseError.NETWORK_ERROR:
                            showErrorToast(getString(R.string.jaringan_error));
                            break;
                        default:
                            // handle other errors
                            showErrorToast(firebaseError.toString());
                            break;
                    }
                }
            });
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

//    private void createAccount(String email, String password) {
//        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressDialog();
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                        hideProgressDialog();
//
//                    }
//                });
//    }
//
//    private boolean validateForm() {
//        boolean valid = true;
//
//        String email = mEmail.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            mEmail.setError("Required.");
//            valid = false;
//        } else {
//            mEmail.setError(null);
//        }
//
//        String password = mPassword.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            mPassword.setError("Required.");
//            valid = false;
//        } else {
//            mPassword.setError(null);
//        }
//
//        return valid;
//    }
//
//    public void showProgressDialog(){
//        if(mProgressDialog == null){
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(getString(R.string.loading));
//            mProgressDialog.setIndeterminate(true);
//        }
//        mProgressDialog.show();
//    }
//
//    public void hideProgressDialog(){
//        if(mProgressDialog != null && mProgressDialog.isShowing()){
//            mProgressDialog.hide();
//        }
//    }
}
