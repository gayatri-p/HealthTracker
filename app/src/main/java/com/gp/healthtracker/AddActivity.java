package com.gp.healthtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    TimePicker addTime;
    EditText addNote;
    Button btnAdd;

    String note, category;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        myDb = new DatabaseHelper(this);

        category = getCategoryFromIntent();

        addTime = findViewById(R.id.add_time);
        addNote = findViewById(R.id.add_note);
        btnAdd = findViewById(R.id.btnAdd);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note = addNote.getText().toString().trim();
                String sTime = getFormattedTime(addTime.getHour(), addTime.getMinute());

                long isInserted = myDb.addData(sTime, category, note, "hour");
                if (isInserted == -1) {
                    id = isInserted;
                    Toast.makeText(AddActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddActivity.this, "Activity added", Toast.LENGTH_SHORT).show();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, addTime.getHour());
                calendar.set(Calendar.MINUTE, addTime.getMinute());
                calendar.clear(Calendar.SECOND);

                setAlarm(calendar);

                finish();
            }
        });
    }

    private String getFormattedTime(int hour, int minute) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.clear(Calendar.SECOND); //reset seconds to zero

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String sTime = sdf.format(time.getTime()); // 08:00 pm
        return sTime;
    }

    private void setAlarm(Calendar calendar) {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(this, MyAlarm.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        intent.putExtra("title")
//        intent.putExtra("id")
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
//        Toast.makeText(this, calendar.toString(), Toast.LENGTH_LONG).show();

        NotifyMe.Builder notifyMe = new NotifyMe.Builder(getApplicationContext());

        if (note.isEmpty()) {
            note = "Get going";
        }

        String titleMsg;

        if (category.equals("exercise")) {
            titleMsg = "Time to exercise " + getEmojiByUnicode(0x1F4AA);
        } else if (category.equals("meditate")) {
            titleMsg = "Time to meditate " + getEmojiByUnicode(0x1F9D8);
        } else if (category.equals("medication")) {
            titleMsg = "Time for medications " + getEmojiByUnicode(0x1F48A);
        } else if (category.equals("dental")) {
            titleMsg = "Time to take care of your teeth " + getEmojiByUnicode(0x1F9B7);
        } else {
            titleMsg = "Time for your health";
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        notifyMe.title(titleMsg)
                .content(note)
                .key(Long.toString(id))
                .color(0, 0, 255, 255)
                .led_color(255, 255, 255, 255)
                .time(calendar)
                .rrule("FREQ=DAILY;INTERVAL=1;")
                .addAction(new Intent(),"Dismiss")
                .large_icon(R.mipmap.ic_launcher_round)
                .build();
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    public String getCategoryFromIntent() {
        if (getIntent().hasExtra("category")) {
            String category = getIntent().getStringExtra("category");
            return category;
        } else {
            return "";
        }
    }
}
