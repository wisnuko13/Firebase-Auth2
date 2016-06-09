package com.wisnu.paktukang.adapters;

import android.content.Context;

import com.wisnu.paktukang.models.User;

import io.realm.RealmResults;

/**
 * Created by private on 09/06/2016.
 */
public class RealmUsersAdapter extends RealmModelAdapter<User> {
    public RealmUsersAdapter(Context context, RealmResults<User> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}
