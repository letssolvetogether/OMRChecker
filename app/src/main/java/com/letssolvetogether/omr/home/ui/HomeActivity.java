package com.letssolvetogether.omr.home.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.instacart.library.truetime.TrueTime;
import com.letssolvetogether.omr.main.R;
import com.letssolvetogether.omr.truetime.InitTrueTimeAsyncTask;
import com.letssolvetogether.omr.omrkey.ui.OMRKeyActivity;
import com.letssolvetogether.omr.settings.SettingsActivity;
import com.letssolvetogether.omr.camera.ui.CameraActivity;

public class HomeActivity extends AppCompatActivity{

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.scan_omr:
                    Intent cameraIntent = new Intent(HomeActivity.this, CameraActivity.class);
                    startActivity(cameraIntent);
                    return true;
                case R.id.navigation_more:
                    Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                    settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS,true);
                    settingsIntent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                    startActivity(settingsIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        displayValidityPeriodDialog();
    }

    public void displayAnswerKey(View view){
        Intent omrKeyActivity = new Intent(this, OMRKeyActivity.class);
        startActivity(omrKeyActivity);
    }

    public void displayValidityPeriodDialog(){

        final CheckBox cbDoNotShowAgain;
        final String PREFS_NAME = "INFO_VALIDITY_DATE";
        AlertDialog.Builder dialogDoNotShow = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View doNotShowLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        cbDoNotShowAgain = doNotShowLayout.findViewById(R.id.skip);
        dialogDoNotShow.setView(doNotShowLayout);
        dialogDoNotShow.setTitle("Attention");
        dialogDoNotShow.setMessage(Html.fromHtml("You can use this app for free until December 30,2018"));

        dialogDoNotShow.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (cbDoNotShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                return;
            }
        });

        if (!skipMessage.equals("checked")) {
            dialogDoNotShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if(!TrueTime.isInitialized()){
                        new InitTrueTimeAsyncTask(HomeActivity.this).execute();
                    }
                }
            });
            dialogDoNotShow.show();
        }else{
            if(!TrueTime.isInitialized()){
                new InitTrueTimeAsyncTask(HomeActivity.this).execute();
            }
        }

        super.onResume();
    }
}
