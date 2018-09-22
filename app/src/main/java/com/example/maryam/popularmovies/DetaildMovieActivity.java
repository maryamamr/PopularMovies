package com.example.maryam.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetaildMovieActivity extends AppCompatActivity {
    Integer id;
    TextView movieTitle, movieOverView, movieReleaseDate, rating;
    ImageView posterImage;
    String api_key = "26f93e16f5f1dadf6c0c3c17462efcc6&language=en-US&page=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaild_movie);
        movieTitle = (TextView) findViewById(R.id.movie_tile_tv);
        movieOverView = (TextView) findViewById(R.id.movie_overview_tv);
        posterImage = (ImageView) findViewById(R.id.poster_img);
        movieReleaseDate = (TextView) findViewById(R.id.release_date_tv);
        rating = (TextView) findViewById(R.id.rating);
        Intent i = getIntent();
        id = i.getIntExtra("ID", 0);
        GetDetails getDetails = new GetDetails();
        getDetails.execute();
    }

    public class GetDetails extends AsyncTask<Void, Void, Void> {
        String title, image, releaseDate, overview;
        Double ratings;
        String base_path = "http://image.tmdb.org/t/p/w185";

        @Override
        protected Void doInBackground(Void... voids) {
            String base_url = "https://api.themoviedb.org/3/movie/";
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(base_url + Integer.toString(id) + "?api_key=" + api_key);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                StringBuffer stringBuffer = new StringBuffer();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer == null) {
                    return null;
                }
                String JSONString = stringBuffer.toString();
                JSONObject main = new JSONObject(JSONString);
                title = main.getString("original_title");
                releaseDate = main.getString("release_date");
                image = base_path + main.getString("poster_path");
                overview = main.getString("overview");
                ratings = main.getDouble("vote_average");

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            movieTitle.setText(title);
            Picasso.with(DetaildMovieActivity.this).load(image).into(posterImage);
            movieOverView.setText(overview);
            movieReleaseDate.setText("Date of release : " + releaseDate);
            rating.setText("Rating : " + ratings.toString());


        }
    }
}
