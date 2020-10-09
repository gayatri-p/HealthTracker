package com.gp.healthtracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class UpdateActivity extends AppCompatActivity {

    TimePicker editTime;
    EditText editNote;
    Button btnUpdate, btnDelete;

    String id, time, note, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editTime = findViewById(R.id.edit_time);
        editNote = findViewById(R.id.edit_note);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        getAndSetIntentData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper myDb = new DatabaseHelper(UpdateActivity.this);

                note = editNote.getText().toString().trim();
                time = getFormattedTime(editTime.getHour(), editTime.getMinute());
                myDb.updateData(id, time, note);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,
                        Calendar.MONTH,
                        Calendar.DAY_OF_MONTH,
                        editTime.getHour(),
                        editTime.getMinute(),
                        0);

                setNotification(calendar);

                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    public void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("time") && getIntent().hasExtra("note")) {

            id = getIntent().getStringExtra("id");
            time = getIntent().getStringExtra("time");
            note = getIntent().getStringExtra("note");

            category = getIntent().getStringExtra("category");

            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));

            editTime.setHour(hour);
            editTime.setMinute(minute);
            editNote.setText(note);

        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
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

    public void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper myDb = new DatabaseHelper(UpdateActivity.this);
                        myDb.deleteRow(id);
                        NotifyMe.cancel(getApplicationContext(),id);
                        finish();
                    }
                })
                .create().show();
    }

    private void setNotification(Calendar calendar) {
        NotifyMe.cancel(getApplicationContext(),id);

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
//            Toast.makeText(this, category, Toast.LENGTH_LONG).show();
        }

        if (calendar.before(Calendar.getInstance())) {
//            Toast.makeText(this, "before", Toast.LENGTH_SHORT).show();
            calendar.add(Calendar.DATE, 1);
        } else {
//            Toast.makeText(this, "not before", Toast.LENGTH_SHORT).show();
        }

        notifyMe.title(titleMsg)
                .content(note)
                .key(id)
                .color(0, 0, 255, 255)
                .led_color(255, 255, 255, 255)
                .time(calendar)
                .rrule("FREQ=DAILY;INTERVAL=1;")
                .addAction(new Intent(),"Done")
                .large_icon(R.mipmap.ic_launcher_round)
                .build();

    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
