package com.gp.healthtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.allyants.notifyme.NotifyMe;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private Activity activity;
    private ArrayList arrayListTimes, arrayListNotes, arrayListIds, arrayListIsDones;
    private int position;
    private String category;
//    boolean isChecked = true;

    public ActivityAdapter(Activity activity, Context context, ArrayList arrayListIds, ArrayList arrayListTimes, ArrayList arrayListNotes, ArrayList arrayListIsDones, String category ) {
        this.mContext = context;
        this.arrayListTimes = arrayListTimes;
        this.arrayListNotes = arrayListNotes;
        this.arrayListIds = arrayListIds;
        this.arrayListIsDones = arrayListIsDones;
        this.activity = activity;
        this.category = category;
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        public TextView timeText, noteText;
        public LinearLayout mainItemLayout;
        public CheckBox checkBox;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.tv_item_time);
            noteText = itemView.findViewById(R.id.tv_item_note);
            mainItemLayout = itemView.findViewById(R.id.mainItemLayout);
            checkBox = itemView.findViewById(R.id.checkBoxIsDone);
        }

    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.activity_item, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActivityViewHolder holder, final int position) {
        holder.noteText.setText(String.valueOf(arrayListNotes.get(position)));
        holder.timeText.setText(String.valueOf(arrayListTimes.get(position)));

        // change color of item if isDone = false
        if (arrayListIsDones.get(position).equals("1")) {
            // is checked == false by default
            holder.checkBox.setChecked(true);
            changeItemTextColor(holder, holder.checkBox.isChecked());
        } else {
            holder.checkBox.setChecked(false);
            changeItemTextColor(holder, holder.checkBox.isChecked());
        }

        holder.mainItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(arrayListIds.get(position)));
                intent.putExtra("time", String.valueOf(arrayListTimes.get(position)));
                intent.putExtra("note", String.valueOf(arrayListNotes.get(position)));
                intent.putExtra("category", category);

                activity.startActivityForResult(intent, 1);
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.checkBox.isChecked();
                if (isChecked) {
                    Toast.makeText(mContext, "Activity done", Toast.LENGTH_SHORT).show();
                }
                // change database and
                // cancel / add notification
                changeDatabaseIsDone(holder, isChecked, String.valueOf(arrayListIds.get(position)));

                // change item color
                changeItemTextColor(holder, isChecked);
            }
        });
    }

    private void changeDatabaseIsDone(ActivityViewHolder holder, boolean isChecked, String id) {
        // DATABASE PART
        DatabaseHelper myDb = new DatabaseHelper(mContext);
        myDb.toggleIsDone(isChecked, id);

        // NOTIFICATION PART
        if (isChecked) {
            // parse time & date from timeText
            String timeStr = holder.timeText.getText().toString();
            int hour = Integer.parseInt(timeStr.substring(0, 2));
            int minute = Integer.parseInt(timeStr.substring(3, 5));
            String a = timeStr.substring(6, 8);

            Calendar cal = Calendar.getInstance();
            if (a.equals("AM")) {
                if (hour == 12) {
                    hour = 0;
                }
            } else {
                if (hour != 12) {
                    hour += 12;
                }
            }

            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.clear(Calendar.SECOND);

//            Toast.makeText(mContext, cal.getTime().toString(), Toast.LENGTH_SHORT).show();

            // check if before or after
            if (cal.before(Calendar.getInstance())) {
                cal.add(Calendar.DATE, 1);
            }

            // toggle notification on
            NotifyMe.Builder notifyMe = new NotifyMe.Builder(mContext);
            String note = holder.noteText.getText().toString();

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

            notifyMe.title(titleMsg)
                    .content(note)
                    .key(id)
                    .color(0, 0, 255, 255)
                    .led_color(255, 255, 255, 255)
                    .time(cal)
                    .rrule("FREQ=DAILY;INTERVAL=1;")
                    .addAction(new Intent(),"Done")
                    .large_icon(R.mipmap.ic_launcher_round)
                    .build();

        } else {
            // toggle notification off
            NotifyMe.cancel(mContext,id);
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void changeItemTextColor(ActivityViewHolder holder, boolean isChecked) {
        if (isChecked) {
            holder.noteText.setTextColor(ContextCompat.getColor(mContext, R.color.textDark));
            holder.timeText.setTextColor(ContextCompat.getColor(mContext, R.color.textDark));
        } else {
            holder.noteText.setTextColor(ContextCompat.getColor(mContext, R.color.textItemDone));
            holder.timeText.setTextColor(ContextCompat.getColor(mContext, R.color.textItemDone));
        }
    }


    @Override
    public int getItemCount() {
        return arrayListTimes.size();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}

/*
public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private ArrayList arrayListTimes, arrayListNotes;
    private int position;

    public ActivityAdapter(Context context, ArrayList arrayListTimes, ArrayList arrayListNotes) {
        this.mContext = context;
        this.arrayListTimes = arrayListTimes;
        this.arrayListNotes = arrayListNotes;
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        public TextView timeText, noteText;
        public LinearLayout mainItemLayout;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.tv_item_time);
            noteText = itemView.findViewById(R.id.tv_item_note);
            mainItemLayout = itemView.findViewById(R.id.mainItemLayout);
        }

    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.activity_item, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        this.position = position;

        holder.noteText.setText(String.valueOf(arrayListNotes.get(position)));
        holder.timeText.setText(String.valueOf(arrayListTimes.get(position)));

        holder.mainItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListTimes.size();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}

 */