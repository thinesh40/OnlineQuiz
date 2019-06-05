package com.xon.onlinequiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomAdapterStore extends SimpleAdapter{

        private Context mContext;
        public LayoutInflater inflater = null;

        public CustomAdapterStore(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mContext = context;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            try {
                if (convertView == null)
                    vi = inflater.inflate(R.layout.cust_list_store, null);
                HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
                TextView tvfeatures = vi.findViewById(R.id.textViewF);
                TextView tvdescription = vi.findViewById(R.id.textViewD);
                TextView tvcode = vi.findViewById(R.id.textView21);
                TextView tvprice = vi.findViewById(R.id.textViewP);
                String qfeature = (String) data.get("feature");
                String qdescription = (String) data.get("description");
                String qcode = (String) data.get("code");
                String qprice = (String) data.get("price");
                tvfeatures.setText(qfeature);
                tvdescription.setText(qdescription);
                tvcode.setText(qcode);
                tvprice.setText(qprice);

            } catch (IndexOutOfBoundsException e) {

            }

            return vi;
        }
    }

