package com.example.maryam.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {
    private ArrayList<String> mPosters;
    String posterPath[];
    private Context ctx;
    String baseURI = "http://image.tmdb.org/t/p/w185";

    public MovieAdapter(Context ctx, ArrayList<String> MoviePosters) {
        this.ctx = ctx;
        mPosters = MoviePosters;
        posterPath = new String[mPosters.size()];
        for (int i = 0; i < posterPath.length; i++) {
            posterPath[i] = baseURI + mPosters.get(i);
        }
    }

    @Override
    public int getCount() {
        return posterPath.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView img;
        if (convertView == null) {
            img = new ImageView(ctx);
            img.setLayoutParams(new GridView.LayoutParams(250, 350));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setPadding(10, 10, 10, 10);
        } else {

            img = (ImageView) convertView;
        }
        Picasso.with(ctx).load(posterPath[position]).into(img);
        return img;
    }
}
