package com.xon.onlinequiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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

    int progressValue = 0;
    int sum=0;
    CountDownTimer mCountDown; // for progressbar
    List questionPlay = new ArrayList<>(); //total Question
    int index=1,score=0,thisQuestion=0,totalQuestion,correctAnswer;
    String mode="";
    ProgressBar progressBar;
    Button btnA,btnB,btnC,btnD,nextbtn,prevbtn,submit;
    TextView txtScore,txtQuestion,txtquesno,optA,optB,optC,optD;
    String optd,opta,optb,optc,quizID,quizName,question,ans,lname,num,currentans;
    String selected[] = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String code2 = bundle.getString("code1");
        questions(code2,1);



        txtScore = findViewById(R.id.txtScore);
        txtQuestion =findViewById(R.id.textViewQues);
        progressBar = findViewById(R.id.progressBar);
        txtquesno = findViewById(R.id.txtSlide);
        btnA=findViewById(R.id.btnAnswerA);
        btnB=findViewById(R.id.btnAnswerB);
        btnC=findViewById(R.id.btnAnswerC);
        btnD=findViewById(R.id.btnAnswerD);
        optA = findViewById(R.id.textViewA);
        optB = findViewById(R.id.textViewB);
        optC = findViewById(R.id.textViewC);
        optD = findViewById(R.id.textViewD);
        nextbtn = findViewById(R.id.btnNext);
        prevbtn = findViewById(R.id.btnPrev);
        submit = findViewById(R.id.buttonsubmit);
        txtScore.setText("1");

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            currentans = "A";

            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentans = "B";

            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentans = "C";

            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentans = "D";
            }
        });



       nextbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(currentans!=null&&index<4){
                   if(index<3) {
                       index = index + 1;
                   }
                   questions(code2, index);
                   String no = String.valueOf(index);
                   txtScore.setText(no);
                   Boolean check =answers(index,currentans);
                   if (check==true){
                       sum++;
                       currentans=null;
                   }else if(currentans==null){
                       Toast.makeText(QuestionsActivity.this, "Please Select the answer", Toast.LENGTH_SHORT).show();
                   }

               }else if(index==3){


                   Toast.makeText(QuestionsActivity.this, "You Have Been Answer Click On Submit to Check Your Result", Toast.LENGTH_SHORT).show();
               }

           }
       });

       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String tes = String.valueOf(sum);
               Toast.makeText(QuestionsActivity.this, "Score : "+tes +"/3", Toast.LENGTH_SHORT).show();;
           }
       });

       prevbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(index>1){
               index=index-1;
               questions(code2,index);
               String no = String.valueOf(index);
               txtScore.setText(no);
           }}
       });


    }



    private void questions(String code,int index){
           String no = String.valueOf(index);
           loadQuestion(code, no);

    }

    private void storeans(int no,String option){
        selected[no] = option;

    }

    private boolean answers(int no,String option){
        String realAns=ans;
            if (option.equalsIgnoreCase(realAns)){
                return true;
            }else{
                return false;
            }

    }
    private int marks(){
       int no=1;
        int marks=0;
        Boolean sum =false;

        for (int i = no;i<5;no++) {
            String option=selected[no];
            sum=answers(no, option);
            if(sum=true){
                marks++;
            }
        }
        return marks;
    }


    private void showQuestion(int index,String code2) {
        if(index < totalQuestion){
            thisQuestion++;
            txtQuestion.setText(String.format("%c/%c",thisQuestion,num));
            progressBar.setProgress(0);
            progressValue = 0;
            String no = String.valueOf(index);
            loadQuestion(code2,"2");

           /* btnA.setText(questionPlay.get(index).getAnswerA());
            btnB.setText(questionPlay.get(index).getAnswerB());
            btnC.setText(questionPlay.get(index).getAnswerC());
            btnD.setText(questionPlay.get(index).getAnswerD());
           */

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





    void loadQuestion(final String code,final String no){
        class LoadQuestion extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("code", code);
                hashMap.put("number",no);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("https://www.fussionspark.com/onlinequiz/load_question.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
               if(s.equalsIgnoreCase("failed")){
                   Toast.makeText(QuestionsActivity.this, "No Quiz Created", Toast.LENGTH_SHORT).show();
               }else{
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray quesArray = jsonObject.getJSONArray("questions");
                    JSONObject c = quesArray.getJSONObject(0);
                    quizID = c.getString("quizid");
                    quizName = c.getString("category");
                    num = c.getString("quesNum");
                    question = c.getString("ques");
                    opta = c.getString("optA");
                    optb = c.getString("optB");
                    optc = c.getString("optC");
                    optd = c.getString("optD");
                    ans = c.getString("anws");
                    lname = c.getString("name");

                    //set total num of quiz
                    txtquesno.setText(num);

                } catch (JSONException e) {
                    Toast.makeText(QuestionsActivity.this,"Json Error", Toast.LENGTH_SHORT).show();
                }}

                txtQuestion.setText(question);
                optA.setText(opta);
                optB.setText(optb);
                optC.setText(optc);
                optD.setText(optd);
            }
        }
        LoadQuestion loadQuestion = new LoadQuestion();
        loadQuestion.execute();
}


}