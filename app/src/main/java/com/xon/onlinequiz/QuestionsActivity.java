package com.xon.onlinequiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QuestionsActivity extends AppCompatActivity {

    final static long INTERVAL = 1000; // 1 second
    final static long TIMEOUT = 7000; // 7 sconds
    int progressValue = 0;

    CountDownTimer mCountDown; // for progressbar
    List questionPlay = new ArrayList<>(); //total Question
    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;
    String mode="";

    //Control
    ProgressBar progressBar;
    Button btnA,btnB,btnC,btnD;
    TextView txtScore,txtQuestion;
    String optd,opta,optb,optc,quizID,quizName,question,ans,lname,num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //Get Data from MainActivity
        loadQuestion("2020");
        Bundle extra = getIntent().getExtras();
        if(extra != null)
            mode=extra.getString("MODE");

        txtScore = findViewById(R.id.txtScore);
        txtQuestion =findViewById(R.id.txtQuestion);
        progressBar = findViewById(R.id.progressBar);
        btnA=findViewById(R.id.btnAnswerA);
        btnB=findViewById(R.id.btnAnswerB);
        btnC=findViewById(R.id.btnAnswerC);
        btnD=findViewById(R.id.btnAnswerD);

       /* btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
*/
    }




    private void showQuestion(int index) {
        if(index < totalQuestion){
            thisQuestion++;
            txtQuestion.setText(String.format("%d/%d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

           /* btnA.setText(questionPlay.get(index).getAnswerA());
            btnB.setText(questionPlay.get(index).getAnswerB());
            btnC.setText(questionPlay.get(index).getAnswerC());
            btnD.setText(questionPlay.get(index).getAnswerD());
           */
            mCountDown.start();
        }
        else{
            //Intent intent = new Intent(this,Done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
           // intent.putExtras(dataSend);
            //startActivity(intent);
            finish();
        }
    }

    public void onClick(View v) {

        mCountDown.cancel();
        if(index < totalQuestion){
            Button clickedButton = (Button)v;
            if(clickedButton.getText().equals(questionPlay.get(index)))
            {
                score+=10; // increase score
                correctAnswer++ ; //increase correct answer
                showQuestion(++index);
            }
            else
                showQuestion(++index); // If choose right , just go to next question

            txtScore.setText(String.format("%d",score));
        }

    }
    void loadQuestion(final String code){
        class LoadQuestion extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("code", code);
                //hashMap.put("index", index);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://fussionspark.com/onlinequiz/load_question.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restArray = jsonObject.getJSONArray("user");
                    JSONObject c = restArray.getJSONObject(0);
                    quizID = c.getString("quizid");
                    quizName = c.getString("category");
                    num = c.getString("quesnum");
                    question = c.getString("ques");
                    opta = c.getString("optA");
                    optb = c.getString("optB");
                    optc = c.getString("optC");
                    optd = c.getString("optD");
                    ans = c.getString("anws");
                    lname = c.getString("name");
                    if(s.equalsIgnoreCase("Success")){
                        Toast.makeText(QuestionsActivity.this, "Connection Verfied", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(QuestionsActivity.this, num, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {

                }

                /*tvuserid.setText(userid);
                tvname.setText(name);
                tvemail.setText(email);
                */

            }
        }
        LoadQuestion loadQuestion = new LoadQuestion();
        loadQuestion.execute();
}
}