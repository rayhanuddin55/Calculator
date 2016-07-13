package com.rayhan.calculator.mycalculator;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class History extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String PreferencesName = "tempMemory";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedPreferences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText display = (EditText) findViewById(R.id.editTextShowHistory);
       display.setText(sharedPreferences.getString("history", ""));
    }

    public  void deleteHistory(View v){
        sharedPreferences = getSharedPreferences(PreferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("history", "");
        editor.commit();
        EditText display = (EditText) findViewById(R.id.editTextShowHistory);
        display.setText(sharedPreferences.getString("history", ""));
    }
}
