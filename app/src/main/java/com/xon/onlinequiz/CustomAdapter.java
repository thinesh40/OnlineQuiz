package com.xon.onlinequiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class CustomAdapter extends SimpleAdapter {

        private Context mContext;
        public LayoutInflater inflater = null;

        public CustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mContext = context;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            try {
                if (convertView == null)
                    vi = inflater.inflate(R.layout.cust_list_quiz, null);
                HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
                TextView tvquizame = vi.findViewById(R.id.textViewg);
                TextView quizID = vi.findViewById(R.id.textView14g);
                TextView tvcategory = vi.findViewById(R.id.textView2g);
                TextView tvdate = vi.findViewById(R.id.textView3g);
                TextView tvlectname = vi.findViewById(R.id.textView4g);
                String qname = (String) data.get("name1");
                String qid1 = (String)data.get("quesid1");
                String qcategory = (String) data.get("category1");
                String qdate = (String) data.get("date1");
                String qlectname = (String) data.get("lectname1");
                tvquizame.setText(qname);
                quizID.setText(qid1);
               // Toast.makeText(mContext, qid1, Toast.LENGTH_SHORT).show();
                tvcategory.setText(qcategory);
                tvdate.setText(qdate);
                tvlectname.setText(qlectname);


            } catch (IndexOutOfBoundsException e) {

            }

            return vi;
        }
    }


