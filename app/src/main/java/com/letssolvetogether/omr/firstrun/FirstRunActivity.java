package com.letssolvetogether.omr.firstrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.letssolvetogether.omr.tutorial.TutorialActivity;
import com.letssolvetogether.omr.home.ui.HomeActivity;

public class FirstRunActivity extends AppCompatActivity {

    private static final String FIRST_RUN = "first_run";

    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getBoolean(FIRST_RUN, false)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }else{
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_RUN, true);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
