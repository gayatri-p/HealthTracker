package com.gp.healthtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity  {

    // todo
    // update row ids ✔
    // pass category as a parameter to add (not update) ✔
    // make update work ✔
    // change add dialog to new add activity ✔
    // add delete activity ✔
    // refresh home page number ✔
    // fix time format crash ✔
    // fix notification
    // update recycler view on add/update ✔
    // add info under recycler view ✔
    // copy the activity x700 ✔
    // add icons..? ah shit. ✔
    // ✌🏼

    RecyclerView recyclerView;
    DatabaseHelper myDb;
    ActivityAdapter mAdapter;
    FloatingActionButton fab;
    TextView emptyView;

    String TABLE_CATEGORY = "meditate";

    ArrayList<String> arrayListTimes, arrayListNotes, arrayListIds, arrayListIsDones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle("Meditation Timings");

        // initialize variables
        emptyView = findViewById(R.id.empty_view_timer);
        recyclerView = findViewById(R.id.recyclerView_timer);
        recyclerView.setNestedScrollingEnabled(true);

        fab = findViewById(R.id.fab1);
        myDb = new DatabaseHelper(this);

        // array lists
        arrayListTimes = new ArrayList<>();
        arrayListNotes = new ArrayList<>();
        arrayListIds = new ArrayList<>();
        arrayListIsDones = new ArrayList<>();

        storeDataInArrays();

        // adapter
        mAdapter = new ActivityAdapter(TimerActivity.this, TimerActivity.this, arrayListIds, arrayListTimes, arrayListNotes, arrayListIsDones, TABLE_CATEGORY);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, AddActivity.class);
                intent.putExtra("category", TABLE_CATEGORY);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void storeDataInArrays() {
        Cursor cursor = myDb.retrieveData(TABLE_CATEGORY);
        if (cursor.getCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            while (cursor.moveToNext()) {
                arrayListIds.add(cursor.getString(0));
                arrayListTimes.add(cursor.getString(1));
                arrayListNotes.add(cursor.getString(3));
                arrayListIsDones.add(cursor.getString(4));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // REFRESH ACTIVITY ON GETTING RESULT
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }
}

/*

public class TimerActivity extends AppCompatActivity implements AddDialog.AddDialogListener  {

    RecyclerView activityList;
    DatabaseHelper myDb;
    ActivityAdapter mAdapter;
    FloatingActionButton fab;

    String TABLE_CATEGORY = "exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        activityList = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        myDb = new DatabaseHelper(this);

        activityList = findViewById(R.id.recyclerView);
        activityList.setLayoutManager(new LinearLayoutManager((this)));

        getAllItems();
        activityList.addItemDecoration(new DividerItemDecoration(activityList.getContext(), DividerItemDecoration.VERTICAL));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    public void addItem() {
        AddDialog addDialog = new AddDialog();
        addDialog.show(getSupportFragmentManager(), "add dialog");
    }

    private void getAllItems() {
        mAdapter = new ActivityAdapter(this, myDb.retrieveData(TABLE_CATEGORY));
        activityList.setAdapter(mAdapter);
    }

    @Override
    public void applyData(String time, String note) {
        // GET THE TIME AND NOTE... DO SOMETHING HERE
        boolean isInserted = myDb.addData(time, TABLE_CATEGORY, note, "hour");
        if (isInserted) {
            Toast.makeText(this, time, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        getAllItems();
    }
}



 */
