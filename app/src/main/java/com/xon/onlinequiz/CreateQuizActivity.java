package com.xon.onlinequiz;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class CreateQuizActivity extends AppCompatActivity {
    EditText qname,qno,quizid,cat;
    String userid,quizid1,qname1,qno1,cat1;
    Button addbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userid = bundle.getString("userid");

        quizid = findViewById(R.id.quizid);
        qname = findViewById(R.id.quiznme);
        qno = findViewById(R.id.quesno);
        cat =findViewById(R.id.cate);
        addbtn = findViewById(R.id.addquiz);

        quizid1 = quizid.getText().toString();
        qname1 = qname.getText().toString();
        qno1 = qno.getText().toString();
        cat1 = cat.getText().toString();


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertQuiz();
            }
        });


    }



    private void insertQuiz() {
        class InsertQuiz extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CreateQuizActivity.this,
                        "Registration", "...", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userid", userid);
                hashMap.put("quizid", quizid1);
                hashMap.put("qcategory", cat1);
                hashMap.put("quizname", qname1);
                hashMap.put("quesno", qno1);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest
                        ("http://www.fussionspark.com/onlinequiz/insert_quiz.php", hashMap);
                return s;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("Success")) {
                    Intent intent = new Intent(CreateQuizActivity.this, MyQuizActivity.class);
                    CreateQuizActivity.this.finish();
                    startActivity(intent);


                }else if(s.equalsIgnoreCase("Failed")){
                    Toast.makeText(CreateQuizActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }else if(s.equalsIgnoreCase("Exist")){
                    Toast.makeText(CreateQuizActivity.this, "Quiz Id must be Unique", Toast.LENGTH_SHORT).show();
                }
                InsertQuiz insertQuiz = new InsertQuiz();
                insertQuiz.execute();
            }


        }

    }
}
