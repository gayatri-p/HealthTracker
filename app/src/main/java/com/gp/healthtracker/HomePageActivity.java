package com.gp.healthtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class HomePageActivity extends AppCompatActivity {

    TextView title, completed;

    String PERSON_NAME;
    FrameLayout fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        loadSharedPrefs();
        title = findViewById(R.id.tvTitle);
        String titleText = "Hello " + PERSON_NAME;
        title.setText(titleText);

        fab = findViewById(R.id.btnAbout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        completed = findViewById(R.id.tvCompleted);
        DatabaseHelper myDb = new DatabaseHelper(this);
        String count = (myDb.getTotalCount());
        String completedText = "Tasks completed: " + count;
        completed.setText(completedText);

        if (!count.equals("0")) {
            setRefreshAlarm();
        }
    }

    private void setRefreshAlarm() {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 18);
        midnight.set(Calendar.MINUTE, 55);
        midnight.clear(Calendar.SECOND);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void openExerciseActivity(View view) {
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }

    public void openMedicationActivity(View view) {
        Intent intent = new Intent(this, MedicationActivity.class);
        startActivity(intent);
    }

    public void openDentalActivity(View view) {
        Intent intent = new Intent(this, DentalActivity.class);
        startActivity(intent);
    }

    public void openTimerActivity(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }

    public void openDrinkActivity(View view) {
        Intent intent = new Intent(this, DrinkActivity.class);
        startActivity(intent);
    }

    public void openReadActivity(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void loadSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        PERSON_NAME = sharedPreferences.getString(MainActivity.KEY_PERSON_NAME, "");
    }


    // OVERRIDE GOING TO THE LOGIN PAGE
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
