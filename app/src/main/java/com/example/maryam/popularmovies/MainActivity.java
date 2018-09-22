package com.example.maryam.popularmovies;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GridView posterGridView;
    ArrayList posters, ids;
    GetMovies popularMovies = new GetMovies();
    MovieAdapter movieAdapter;
    Dialog sortDialog;
    Button save, cancel;
    SharedPreferences sharedPreferences;
    String sort_type;
    RadioGroup radioGroup;
    String api_key = "26f93e16f5f1dadf6c0c3c17462efcc6&language=en-US&page=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        posterGridView = (GridView) findViewById(R.id.posters_gv);
        sharedPreferences = getSharedPreferences("movies", MODE_PRIVATE);
        sort_type = sharedPreferences.getString("sort_type", "popular");
        popularMovies.execute(sort_type);
        posterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detialedIntent = new Intent(MainActivity.this, DetaildMovieActivity.class);
                detialedIntent.putExtra("ID", (Integer) ids.get(position));
                startActivity(detialedIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.action_sort) {
            showPop();
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetMovies extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            posters = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + params[0] + "?api_key=" + api_key);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();
                JSONObject main = new JSONObject(moviesJsonStr);
                JSONArray arr = main.getJSONArray("results");
                JSONObject movie;
                for (int i = 0; i < arr.length(); i++) {
                    movie = arr.getJSONObject(i);
                    ids.add(movie.getInt("id"));
                    posters.add(movie.getString("poster_path"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            movieAdapter = new MovieAdapter(MainActivity.this, posters);
            try {
                posterGridView.setAdapter(movieAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public void showPop() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        sort_type = sharedPreferences.getString("sort_type", "popular");
        sortDialog = new Dialog(this);
        sortDialog.setContentView(R.layout.settings_popup);
        save = (Button) sortDialog.findViewById(R.id.save_btn);
        radioGroup = (RadioGroup) sortDialog.findViewById(R.id.radio_group);
        cancel = (Button) sortDialog.findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButton == R.id.top_rated_rb) {
                    editor.putString("sort_type", "top_rated");
                }
                if (checkedRadioButton == R.id.most_popular_rb) {
                    editor.putString("sort_type", "popular");
                }
                editor.commit();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        sortDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sortDialog.show();
    }
}
