package com.letssolvetogether.omr.home.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.instacart.library.truetime.TrueTime;
import com.letssolvetogether.omr.main.R;
import com.letssolvetogether.omr.truetime.InitTrueTimeAsyncTask;
import com.letssolvetogether.omr.omrkey.ui.OMRKeyActivity;
import com.letssolvetogether.omr.settings.SettingsActivity;
import com.letssolvetogether.omr.camera.ui.CameraActivity;

public class HomeActivity extends AppCompatActivity{

    private int noOfQuestions = 20;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.scan_omr:
                    Intent cameraIntent = new Intent(HomeActivity.this, CameraActivity.class);

                    if(noOfQuestions == 20) {
                        cameraIntent.putExtra("noOfQuestions", noOfQuestions);
                        startActivity(cameraIntent);
                    }else {
                        new AlertDialog.Builder(HomeActivity.this)
                            .setMessage("Coming soon .. ")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                  @Override
                                  public void onDismiss(DialogInterface dialog) {
                                      dialog.dismiss();
                                  }
                              })
                            .show();
                    }
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

    private RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.radio_omrkey_20:
                    noOfQuestions = 20;
                    break;
                case R.id.radio_omrkey_100:
                    noOfQuestions = 100;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RadioGroup radioGroupOMRTypes = findViewById(R.id.radiogroup_omrtypes);
        radioGroupOMRTypes.setOnCheckedChangeListener(radioGroupOnCheckedChangeListener);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        displayValidityPeriodDialog();
    }

    public void displayAnswerKey(View view){
        Intent omrKeyActivity = new Intent(this, OMRKeyActivity.class);
        omrKeyActivity.putExtra("noOfQuestions", noOfQuestions);
        startActivity(omrKeyActivity);
    }

    public void displayValidityPeriodDialog(){
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun){
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();

            //Display Dialog
            AlertDialog.Builder dialogTips = new AlertDialog.Builder(HomeActivity.this);
            dialogTips.setTitle("Note:");
            dialogTips.setMessage("You can use this app for free until December 31, 2021.");
            dialogTips.setNeutralButton("Ok",null);
            dialogTips.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if(!TrueTime.isInitialized()){
                        new InitTrueTimeAsyncTask(HomeActivity.this).execute();
                    }
                }
            });
            dialogTips.show();
        }else {
            if (!TrueTime.isInitialized()) {
                new InitTrueTimeAsyncTask(HomeActivity.this).execute();
            }
        }
    }
}
