package com.gp.healthtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;

public class MyAlarm extends BroadcastReceiver {

    DatabaseHelper myDb;
    @Override
    public void onReceive(Context context, Intent intent) {
        myDb = new DatabaseHelper(context);
        myDb.refreshData();
    }
}
