package com.xon.onlinequiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyQuizActivity extends AppCompatActivity {
    ListView lvQuiz;
    ArrayList<HashMap<String, String>> quizList;
    Spinner sploc;
    String userid,name,phone, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quiz);
        lvQuiz = findViewById(R.id.listviewQuiz);


        sploc = findViewById(R.id.spinner);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userid = bundle.getString("userid");
        name = bundle.getString("name");
        phone = bundle.getString("phone");
        email = bundle.getString("email");

        loadRestaurant(sploc.getSelectedItem().toString());

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void loadRestaurant(final String loc) {
        class LoadRestaurant extends AsyncTask<Void,Void,String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("category",loc);
                hashMap.put("userid",userid);
                RequestHandler rh = new RequestHandler();
                quizList = new ArrayList<>();
                String s = rh.sendPostRequest
                        ("https://www.fussionspark.com/onlinequiz/load_quiz.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(MyQuizActivity.this, s, Toast.LENGTH_LONG).show();
                quizList.clear();
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("rest");
                    Log.e("THINESH",jsonObject.toString());
                    for (int i=0;i<restarray.length();i++){
                        JSONObject c = restarray.getJSONObject(i);
                        //String no = c.getString("quesno");
                        String rname = c.getString("name");
                        String rcategory = c.getString("category");
                        String rdate = c.getString("date");
                        String rlectname = c.getString("lectname");
                        HashMap<String,String> restlisthash = new HashMap<>();
                        //restlisthash.put("quesno",no);
                        restlisthash.put("name1",rname);
                        restlisthash.put("category1",rcategory);
                        restlisthash.put("date1",rdate);
                        restlisthash.put("lectname1",rlectname);
                        quizList.add(restlisthash);

                    }
                }catch (final JSONException e){
                    Log.e("JSONERROR",e.toString());
                }

                ListAdapter adapter = new CustomAdapter(
                        MyQuizActivity.this, quizList,
                        R.layout.cust_list_quiz, new String[]
                        {"name1","category1","date1","lectname1"}, new int[]
                        {R.id.textView,R.id.textView2,R.id.textView3,R.id.textView4});
                lvQuiz.setAdapter(adapter);
            }

        }
        LoadRestaurant loadRestaurant = new LoadRestaurant();
        loadRestaurant.execute();
    }
}
