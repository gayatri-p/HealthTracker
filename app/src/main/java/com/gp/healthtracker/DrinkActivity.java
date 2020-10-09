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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DrinkActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper myDb;
    ActivityAdapter mAdapter;
    FloatingActionButton fab;
    TextView emptyView;

    ArrayList<String> arrayListTimes, arrayListNotes, arrayListIds, arrayListIsDones;

    String TABLE_CATEGORY = "drink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        setTitle("Drink Water");

        // initialize variables
        emptyView = findViewById(R.id.empty_view_drink);
        recyclerView = findViewById(R.id.recyclerView_drink);
        recyclerView.setNestedScrollingEnabled(true);

        fab = findViewById(R.id.fab5);
        myDb = new DatabaseHelper(this);

        // array lists
        arrayListTimes = new ArrayList<>();
        arrayListNotes = new ArrayList<>();
        arrayListIds = new ArrayList<>();
        arrayListIsDones = new ArrayList<>();

        storeDataInArrays();

        mAdapter = new ActivityAdapter(DrinkActivity.this, DrinkActivity.this, arrayListIds, arrayListTimes, arrayListNotes, arrayListIsDones, TABLE_CATEGORY);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinkActivity.this, AddActivity.class);
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
