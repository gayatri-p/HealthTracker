package com.gp.healthtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    EditText editHeight, editWeight;
    Button btnSubmit;
    TextView bmiTextView, userName, healthStatus;

    String PERSON_NAME;
    int PERSON_HEIGHT, PERSON_WEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("My Profile");

        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        btnSubmit = findViewById(R.id.btnSubmit);
        bmiTextView = findViewById(R.id.bmi_field);
        userName = findViewById(R.id.user_name);
        healthStatus = findViewById(R.id.health_status);

        loadSharedPrefs();

        String sPERSON_HEIGHT = Integer.toString(PERSON_HEIGHT);
        String sPERSON_WEIGHT = Integer.toString(PERSON_WEIGHT);
        userName.setText(PERSON_NAME);
        editHeight.setText(sPERSON_HEIGHT);
        editWeight.setText(sPERSON_WEIGHT);
        updateBMI(sPERSON_HEIGHT, sPERSON_WEIGHT);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strHeight = editHeight.getText().toString();
                String strWeight = editWeight.getText().toString();

                if (strHeight.isEmpty() || strWeight.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter a value", Toast.LENGTH_SHORT);
                } else {
                    updateBMI(strHeight, strWeight);
                    hideKeyboard();
                    changeSharedPrefs(strHeight, strWeight);
                }
            }
        });

    }

    public void updateBMI(String strHeight, String strWeight) {
        double height = Integer.parseInt(strHeight);
        height /= 100;
        double weight = Integer.parseInt(strWeight);
        double bmi = weight*100 / (height*height);
        double fbmi = Math.round(bmi);
        fbmi /= 100;
        bmiTextView.setText(Double.toString(fbmi));

        String msg;
        int col;

        if (fbmi < 18.5) {
            msg = "Underweight";
            col = ContextCompat.getColor(getApplicationContext(), R.color.underweight);
        } else if (fbmi >= 18.5 && fbmi < 25) {
            msg = "Healthy";
            col = ContextCompat.getColor(getApplicationContext(), R.color.healthy);
        } else if (fbmi >= 25 && fbmi < 30) {
            msg = "Overweight";
            col = ContextCompat.getColor(getApplicationContext(), R.color.overweight);
        } else {
            msg = "Obese";
            col = ContextCompat.getColor(getApplicationContext(), R.color.obese);
        }

        String message = "Your status: " + msg;
        healthStatus.setText(message);
        healthStatus.setTextColor(col);
    }

    public void changeSharedPrefs(String height, String weight) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(MainActivity.KEY_PERSON_HEIGHT, Integer.parseInt(height));
        editor.putInt(MainActivity.KEY_PERSON_WEIGHT, Integer.parseInt(weight));

        editor.apply();
    }

    public void loadSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        PERSON_NAME = sharedPreferences.getString(MainActivity.KEY_PERSON_NAME, "");
        PERSON_HEIGHT = sharedPreferences.getInt(MainActivity.KEY_PERSON_HEIGHT, 0);
        PERSON_WEIGHT = sharedPreferences.getInt(MainActivity.KEY_PERSON_WEIGHT, 0);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
