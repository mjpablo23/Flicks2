package com.insequence.flicks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    ArrayList<MovieItem> movieItems;
    MovieItemAdapter itemsAdapter;
    ListView lvItems;
    // @BindView(R.id.lvItems) ListView lvItems;

    ArrayList<HashMap<String, String>> movieList;

    private ProgressDialog pDialog;
    // URL to get contacts JSON
    // json parsing tutorial:  http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
    // private static String url = "http://api.androidhive.info/contacts/";

    // example of api call for a single movie
    private static String url_singleMovie = "https://api.themoviedb.org/3/movie/550?api_key=b6bddb4059caa8d601981525229a9d46";

    // popular movies get request api call
    private static String url_popular = "https://api.themoviedb.org/3/movie/popular?api_key=b6bddb4059caa8d601981525229a9d46&language=en-US";

    // now playing movies get request api call.  use this one.
    private static String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=b6bddb4059caa8d601981525229a9d46";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        movieList = new ArrayList<>();

        // tried replace with butterknife, but didn't work
        lvItems = (ListView) findViewById(R.id.lvItems);

        movieItems = new ArrayList<MovieItem>();

        System.out.println("setting items in itemsAdapter");
        itemsAdapter = new MovieItemAdapter(this, movieItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        new GetMovieInfo().execute();
    }

    private void setupListViewListener() {
        // http://stackoverflow.com/questions/9097723/adding-an-onclicklistener-to-listview-android
        // http://stackoverflow.com/questions/36917725/error-setonclicklistener-from-an-android-app
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                MovieItem movieItem = movieItems.get(pos);
                String str = movieItem.getKey();
                System.out.println("str: " + str);
                launchMovieDetailsItem(str, pos, movieItem);
            }
        });
    }

    private final int REQUEST_CODE  = 20;
    // http://guides.codepath.com/android/Using-Intents-to-Create-Flows
    public void launchMovieDetailsItem(String str, int pos, MovieItem movieItem) {
        Intent i = new Intent(MainActivity.this, MovieDetailsActivity.class);
        i.putExtra("str", str);
        i.putExtra("pos", pos);
        i.putExtra("title", movieItem.getName());
        i.putExtra("description", movieItem.getText());
        i.putExtra("rating", movieItem.rating + "");
        i.putExtra("releaseDate", movieItem.releaseDate);
        i.putExtra("backdrop", movieItem.getBackdropUrl());
        i.putExtra("poster", movieItem.getPosterUrl());
        startActivityForResult(i, REQUEST_CODE);
    }

    // Once the sub-activity finishes, the onActivityResult() method in the calling activity is be invoked:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String str = data.getExtras().getString("str");
            int pos = data.getExtras().getInt("pos");
            System.out.println("str: " + str + ", Pos: " + pos);

            MovieItem orig = movieItems.get(pos);
            orig.setText(str);
            movieItems.set(pos, orig);

            itemsAdapter.notifyDataSetChanged();
        }
    }

    // this is called when orientation changes from portrait to landscape, and vice versa
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("i", "changed to landscape");

            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

            for (int i = 0; i< movieItems.size(); i++) {
                String backdrop = movieItems.get(i).getBackdropUrl();
                movieItems.get(i).setPhotoUrl(backdrop);
            }
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("i", "changed to portrait");

            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();

            for (int i = 0; i< movieItems.size(); i++) {
                String poster = movieItems.get(i).getPosterUrl();
                movieItems.get(i).setPhotoUrl(poster);
            }
        }

        itemsAdapter.notifyDataSetChanged();
    }

    /**
     * Async task class to get json by making HTTP call
     * http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
     */
    private class GetMovieInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    // "results" is the JSON array that has individual movie objects
                    JSONArray movies = jsonObj.getJSONArray("results");

                    // looping through All Movies
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject c = movies.getJSONObject(i);

                        String id = c.getString("id");
                        String title = c.getString("title");
                        String overview = c.getString("overview");
                        String poster_path = c.getString("poster_path");
                        String backdrop_path = c.getString("backdrop_path");
                        String release_date = c.getString("release_date");
                        String vote_average = c.getString("vote_average");

                        // tmp hash map for single contact
                        HashMap<String, String> movie = new HashMap<>();

                        // adding each child node to HashMap key => value
                        movie.put("id", id);
                        movie.put("title", title);
                        movie.put("overview", overview);
                        movie.put("poster_path", poster_path);
                        movie.put("backdrop_path", backdrop_path);
                        movie.put("vote_average", vote_average);
                        movie.put("release_date", release_date);


                        // adding contact to contact list
                        movieList.add(movie);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */


            // populate the to do items with stuff from contact list

            for (int i = 0; i< movieList.size(); i++) {
                // tmp hash map for single contact
                HashMap<String, String> contact = movieList.get(i);
                String title = contact.get("title");
                String overview = contact.get("overview");
                String id = contact.get("id");
                String backdrop = contact.get("backdrop_path");
                String poster = contact.get("poster_path");
                String rating = contact.get("vote_average") + "";
                String releaseDate = contact.get("release_date");

                // just using one image size
                String backdrop_url = "https://image.tmdb.org/t/p/w500" + backdrop;
                String poster_url = "https://image.tmdb.org/t/p/w185" + poster;
                String url_for_display = "";

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.d("i", "display poster image");
                    url_for_display = poster_url;
                    // ...
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.d("i", "display backdrop image");
                    url_for_display = backdrop_url;
                    // ...
                }

                MovieItem t = new MovieItem(title, overview, url_for_display, id, poster_url, backdrop_url);
                t.rating = rating;
                t.releaseDate = releaseDate;
                movieItems.add(t);
            }
            MovieItemAdapter adapter = new MovieItemAdapter(MainActivity.this, movieItems);
            lvItems.setAdapter(adapter);

        }

    }

}
