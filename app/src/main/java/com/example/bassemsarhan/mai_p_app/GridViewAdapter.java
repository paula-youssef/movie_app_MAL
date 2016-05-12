package com.example.bassemsarhan.mai_p_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<String> {
    private static final String LOG_TAG = GridViewAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<String>imageUrls;

    public GridViewAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.list_movies_posters, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_movies_posters, parent, false);
        }
        Log.v(LOG_TAG, "urls :   " + imageUrls.get(position));
        Picasso.with(context)
                .load(imageUrls.get(position))
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }

}