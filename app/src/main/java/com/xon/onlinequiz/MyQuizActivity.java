package com.xon.onlinequiz;
import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyQuizActivity extends AppCompatActivity {
    ListView lvQuiz;
    ArrayList<HashMap<String, String>> quizList;
    Spinner spcat;
    String userid,name,phone, email;
    Dialog myDialogWindow;
    Button createQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quiz);
        lvQuiz = findViewById(R.id.listviewQuiz);


        spcat = findViewById(R.id.spinner);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userid = bundle.getString("userid");
        name = bundle.getString("name");
        phone = bundle.getString("phone");
        email = bundle.getString("email");
       // Toast.makeText(this, userid, Toast.LENGTH_SHORT).show();
        loadQuiz(spcat.getSelectedItem().toString());
        createQuiz = findViewById(R.id.button3);

        spcat.setSelection(0,false);
        spcat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadQuiz(spcat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lvQuiz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyQuizActivity.this,QuestionsActivity.class);
                Bundle bundle = new Bundle();
                String code =  quizList.get(position).get("quesid1");
                bundle.putString("code1",code);
                intent.putExtras(bundle);
                startActivity(intent);



            }
        });



        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MyQuizActivity.this,CreateQuizActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(MyQuizActivity.this, userid, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //case R.id.phoneNo:
            //loadCartData();
            //  return true;
            case R.id.myprofile:
                Intent intent = new Intent(MyQuizActivity.this,ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                bundle.putString("email",email);
                bundle.putString("username",name);
                bundle.putString("phone",phone);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.mypr:
                Intent intent1 = new Intent(MyQuizActivity.this,LoginActivity.class);
                startActivity(intent1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void loadQuiz(final String cat) {
        class LoadQuiz extends AsyncTask<Void,Void,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                RequestHandler rh = new RequestHandler();
                quizList = new ArrayList<>();
                hashMap.put("category",cat);
                hashMap.put("userid",userid);
                String s = rh.sendPostRequest
                        ("https://www.fussionspark.com/onlinequiz/load_quiz.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                quizList.clear();
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("rest");
                    Log.e("THINESH",jsonObject.toString());
                    for (int i=0;i<restarray.length();i++){
                        JSONObject c = restarray.getJSONObject(i);
                        String rname = c.getString("name");
                        String rcategory = c.getString("category");
                        String rdate = c.getString("date");
                        String rlectname = c.getString("lectname");
                        String qid = c.getString("quesid");
                        //Toast.makeText(MyQuizActivity.this, qid, Toast.LENGTH_SHORT).show();
                        HashMap<String,String> restlisthash = new HashMap<>();
                        restlisthash.put("name1",rname);
                        restlisthash.put("quesid1",qid);
                        restlisthash.put("category1",rcategory);
                        restlisthash.put("date1",rdate);
                        restlisthash.put("lectname1",rlectname);
                        quizList.add(restlisthash);
                       // Toast.makeText(MyQuizActivity.this, qid, Toast.LENGTH_SHORT).show();
                    }
                }catch (final JSONException e){
                    Log.e("JSONERROR",e.toString());
                }

                ListAdapter adapter = new CustomAdapter(
                        MyQuizActivity.this, quizList,
                        R.layout.cust_list_quiz, new String[]
                        {"name1","quesid1","category1","date1","lectname1"}, new int[]
                        {R.id.textViewg,R.id.textView14g,R.id.textView2g,R.id.textView3g,R.id.textView4g});
                lvQuiz.setAdapter(adapter);
            }

        }
        LoadQuiz loadQuiz = new LoadQuiz();
        loadQuiz.execute();
    }


    }

