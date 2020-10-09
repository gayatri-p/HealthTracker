package com.gp.healthtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public EditText eName, eHeight, eWeight;
    public Button btnLogin;
    public boolean isLoggedIn = false;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_PERSON_NAME = "personName";
    public static final String KEY_PERSON_WEIGHT = "personWeight";
    public static final String KEY_PERSON_HEIGHT = "personHeight";

    public static String PERSON_NAME;
    public static int PERSON_WEIGHT, PERSON_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadLoginData();

        isLoggedIn  = (PERSON_NAME != "");

        if (isLoggedIn) {
            openHomePage();
        } else {
            eName = findViewById(R.id.etName);
            eHeight = findViewById(R.id.etHeight);
            eWeight = findViewById(R.id.etWeight);
            btnLogin = findViewById(R.id.btnLogin);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginBtnOnClick();
                }
            });
        }
    }

    public void loginBtnOnClick() {
        String personName = eName.getText().toString();
        String personHeight = eHeight.getText().toString();
        String personWeight = eWeight.getText().toString();


        if (personName.isEmpty() || personHeight.isEmpty() || personWeight.isEmpty()) {
            Toast.makeText(MainActivity.this, "Invalid Entry", Toast.LENGTH_SHORT);
        } else {
            saveLoginData(personName, personHeight, personWeight);
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT);
            // GO TO HOME & SAVE DATA
            openHomePage();
        }
    }

    public void saveLoginData(String personName, String personHeight, String personWeight) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_PERSON_NAME, personName);
        editor.putInt(KEY_PERSON_WEIGHT, Integer.parseInt(personWeight));
        editor.putInt(KEY_PERSON_HEIGHT, Integer.parseInt(personHeight));

        editor.apply();
    }

    public void loadLoginData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        PERSON_NAME = sharedPreferences.getString(KEY_PERSON_NAME, "");
        PERSON_HEIGHT = sharedPreferences.getInt(KEY_PERSON_HEIGHT, 0);
        PERSON_WEIGHT = sharedPreferences.getInt(KEY_PERSON_WEIGHT, 0);
    }

    public void openHomePage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }
}
