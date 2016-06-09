package com.wisnu.paktukang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.wisnu.paktukang.adapters.RealmUsersAdapter;
import com.wisnu.paktukang.adapters.UsersAdapter;
import com.wisnu.paktukang.app.Prefs;
import com.wisnu.paktukang.models.User;
import com.wisnu.paktukang.models.User1;
import com.wisnu.paktukang.realm.RealmController;
import com.wisnu.paktukang.ui.LoginActivity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserAreaActivity extends AppCompatActivity {

    private static final String TAG = UserAreaActivity.class.getSimpleName();

    private RecyclerView recycler;

    private UsersAdapter adapter;
    private Realm realm;

    private Firebase mUserRef;
    private String mUId;
    final Firebase firebase = new Firebase(Constans.FIREBASE_URL);

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        if(savedInstanceState == null){
            Firebase.setAndroidContext(this);
        }

        this.realm = RealmController.with(this).getRealm();
        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {
            setRealmData();
        }

        RealmController.with(this).refresh();
        setRealmAdapter(RealmController.with(this).getUsers());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mUId = mSharedPreferences.getString(Constans.KEY_UID, null);
        mUserRef = new Firebase(Constans.FIREBASE_URL_USERS).child(mUId);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User1 user1 = dataSnapshot.getValue(User1.class);
//                mWelcome.setText(user1.getName());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "Read failed");
            }
        });

    }

    public void setRealmAdapter(RealmResults<User> books) {
        RealmUsersAdapter realmAdapter = new RealmUsersAdapter(this.getApplicationContext(), books, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setRealmData() {

        ArrayList<User> Users = new ArrayList<>();

        User User = new User();
        User.setId(1+System.currentTimeMillis());
        User.setNama("wisnu");
        User.setAhli("bangunan");
        User.setDaerah("sleman");
        User.setNoHp("082151155541");
        Users.add(User);

        User = new User();
        User.setId(2+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(3+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(4+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(5+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(6+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(7+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(8+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(9+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(10+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        User = new User();
        User.setId(11+System.currentTimeMillis());
        User.setNama("andi");
        User.setAhli("listrik");
        User.setDaerah("kulonprogo");
        User.setNoHp("04132323222");
        Users.add(User);

        for (User b : Users) {
            realm.beginTransaction();
            realm.copyToRealm(b);
            realm.commitTransaction();
        }
        Prefs.with(this).setPreLoad(true);
    }

    private void setupRecycler() {
        recycler.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        adapter = new UsersAdapter(this);
        recycler.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                firebase.unauth();
                loadLoginView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
