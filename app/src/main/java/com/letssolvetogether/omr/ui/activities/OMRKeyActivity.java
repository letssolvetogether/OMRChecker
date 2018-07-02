package com.letssolvetogether.omr.ui.activities;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.letssolvetogether.omr.db.AppDatabase;
import com.letssolvetogether.omr.db.OMRKey;
import com.letssolvetogether.omr.main.R;
import com.letssolvetogether.omr.utils.OMRUtils;

public class OMRKeyActivity extends AppCompatActivity implements RadioButton.OnCheckedChangeListener{

    private int circleIds[] = new int[]{R.mipmap.ic_omr_circle_a, R.mipmap.ic_omr_circle_b, R.mipmap.ic_omr_circle_c, R.mipmap.ic_omr_circle_d, R.mipmap.ic_omr_circle_e};

    private int[] correctAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omrkey);

        createAnswerKey();
        loadCorrectAnswers();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

        int id = compoundButton.getId();
        CheckBox checkBox;

        for (int i = (id/5)*5; i < (id/5)*5 + 5; i++){
            checkBox = findViewById(i);
            if(checkBox.isChecked() && i != id){
                checkBox.setButtonDrawable(circleIds[i%5]);
                checkBox.setChecked(false);
                break;
            }
        }

        if(checked){
            compoundButton.setButtonDrawable(R.mipmap.ic_omr_black_circle);
            compoundButton.setChecked(true);
        }
        else {
            compoundButton.setButtonDrawable(circleIds[id%5]);
            compoundButton.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        storeCorrectAnswers();
    }

    public void createAnswerKey(){

        ScrollView scrollView = findViewById(R.id.scrollView);
        TextView textView;

        LinearLayout horzLinearLayout, vertLinearLayout;
        CheckBox checkBox;

        vertLinearLayout = new LinearLayout(this);
        vertLinearLayout.setOrientation(LinearLayout.VERTICAL);

        for(int i=0;i<20;i++){

            textView = new TextView(this);
            if(i<9)
                textView.setText("\t"+String.valueOf(i+1)+")\t\t");
            else
                textView.setText(String.valueOf(i+1)+")\t\t");

            textView.setTextSize(20);

            horzLinearLayout = new LinearLayout(this);
            horzLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            horzLinearLayout.addView(textView);
            //horzLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            for(int j=0; j<5; j++){

                checkBox = new CheckBox(this);
                checkBox.setId((i*5)+j);
                checkBox.setButtonDrawable(circleIds[j]);
                checkBox.setScaleX(0.85f);
                checkBox.setScaleY(0.85f);
                checkBox.setPadding(10,30,0,30);
                checkBox.setOnCheckedChangeListener(this);

                horzLinearLayout.addView(checkBox);
            }
            vertLinearLayout.addView(horzLinearLayout);
        }
        scrollView.addView(vertLinearLayout);
    }

    public void loadCorrectAnswers(){

        final String[] strCorrectAnswers = {""};
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "omr").build();
        final OMRKey omrKey = new OMRKey();
        omrKey.setOmrkeyid(1);
        omrKey.setStrCorrectAnswers(strCorrectAnswers[0]);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(db.omrKeyDao().findById(1) != null)
                    strCorrectAnswers[0] = db.omrKeyDao().findById(1).getStrCorrectAnswers();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                int[] answers;
                int correctAnswer;
                CheckBox checkBox;

                answers = OMRUtils.strtointAnswers(strCorrectAnswers[0]);

                if(answers != null){
                    for(int i=0; i< answers.length; i++){
                        correctAnswer = answers[i];
                        if(correctAnswer != 0){
                            checkBox = findViewById((i*5) + (correctAnswer - 1));
                            checkBox.setChecked(true);
                        }
                    }
                }
            }
        }.execute();
    }

    public void storeCorrectAnswers(){
        correctAnswers = new int[20];
        int cnt = -1;
        CheckBox checkBox;
        for(int i=0; i<100; i++){
            checkBox = findViewById(i);

            if(i%5 == 0)
                cnt++;

            if(checkBox.isChecked()){
                correctAnswers[cnt] = (i % 5) + 1;
            }
        }

        String strCorrectAnswers = OMRUtils.inttostrAnswers(correctAnswers);

        if(strCorrectAnswers != null){
            final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "omr").build();
            final OMRKey omrKey = new OMRKey();
            omrKey.setOmrkeyid(1);
            omrKey.setStrCorrectAnswers(strCorrectAnswers);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db.omrKeyDao().insertOMRKey(omrKey);
                    return null;
                }
            }.execute();
            Toast.makeText(this,"Answers saved",Toast.LENGTH_LONG).show();
        }
    }
}