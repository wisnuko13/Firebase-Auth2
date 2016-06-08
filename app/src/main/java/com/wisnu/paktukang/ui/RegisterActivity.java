package com.wisnu.paktukang.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.wisnu.paktukang.Constans;
import com.wisnu.paktukang.R;
import com.wisnu.paktukang.UserAreaActivity;
import com.wisnu.paktukang.models.User;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @BindView(R.id.mEmail) EditText mEmail;
    @BindView(R.id.mNama) EditText mNama;
    @BindView(R.id.mPassword) EditText mPassword;
    @BindView(R.id.mConfirmPassword) EditText mConfirm;
    @BindView(R.id.bSignUp) Button mSignUp;

    private ProgressDialog mAuthProgressDialog;

    private Firebase firebase;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        if(savedInstanceState == null){
            Firebase.setAndroidContext(this);
        }
        firebase = new Firebase(Constans.FIREBASE_URL);

        mSignUp.setOnClickListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferencesEditor = mSharedPreferences.edit();

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("LOADING");
        mAuthProgressDialog.setMessage("Autentifikasi ke Firebase...");
        mAuthProgressDialog.setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        if (v == mSignUp) {
            createNewUser();
        }
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            mPassword.setError("Password minimal 6 karakter");
            return false;
        } else if (!password.equals(confirmPassword)) {
            mPassword.setError("Passwords tidak sama");
            return false;
        }
        return true;
    }

    private void createNewUser() {
        final String email = mEmail.getText().toString();
        final String nama = mNama.getText().toString();
        final String password = mPassword.getText().toString();
        final String confirmPassword = mConfirm.getText().toString();

        email.trim();
        nama.trim();
        password.trim();
        confirmPassword.trim();

        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(nama);
        boolean validPassword = isValidPassword(password, confirmPassword);
        if (!validEmail || !validName || !validPassword) return;

        mAuthProgressDialog.show();

            firebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    String uid = stringObjectMap.get("uid").toString();
                    createUserInFirebaseHelper(nama, email, uid);
                    firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            mAuthProgressDialog.dismiss();
                            if(authData != null){
                                String userId = authData.getUid();

                                String userInfo = authData.toString();
                                Log.d(TAG,"yang login: "+ userInfo);

                                mSharedPreferencesEditor.putString(Constans.KEY_UID, userId).apply();
                                Intent intent = new Intent(RegisterActivity.this, UserAreaActivity.class);
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
                                    mEmail.setError(getString(R.string.salah_password));
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

                @Override
                public void onError(FirebaseError firebaseError) {
                    Log.d(TAG, "error occurred " +
                            firebaseError);

                }
            });
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEmail.setError("Masukan email anda");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            mNama.setError("Masukan nama anda");
            return false;
        }
        return true;
    }

    private void showErrorToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void createUserInFirebaseHelper(final String name, final String email, final String uid) {
        final Firebase userLocation = new Firebase(Constans.FIREBASE_URL_USERS).child(uid);
        User newUser = new User(name, email);
        userLocation.setValue(newUser);
    }
}
