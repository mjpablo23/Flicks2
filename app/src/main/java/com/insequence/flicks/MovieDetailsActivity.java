package com.insequence.flicks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // https://developer.android.com/training/system-ui/status.html
//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//        // Remember that you should never show the action bar if the
//        // status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();

        String str = getIntent().getStringExtra("str");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String backdrop= getIntent().getStringExtra("backdrop");
        String poster = getIntent().getStringExtra("poster");
        String rating = getIntent().getStringExtra("rating");
        String releaseDate = getIntent().getStringExtra("releaseDate");
        pos = getIntent().getIntExtra("pos", 0);

        Log.d("i", "pos: " + pos);

        TextView titleView = (TextView) findViewById(R.id.infoTitleView);
        TextView descriptionView = (TextView) findViewById(R.id.infoDescriptionView);
        TextView ratingView = (TextView) findViewById(R.id.infoRatingView);
         TextView releaseDateView = (TextView) findViewById(R.id.infoReleaseDateView);

//        @BindView(R.id.infoTitleView) TextView titleView;
//        @BindView(R.id.infoDescriptionView) TextView descriptionView;
//        @BindView(R.id.infoRatingView) TextView ratingView;
//        @BindView(R.id.infoReleaseDateView) TextView releaseDateView;

        // don't know why they are reversed
        descriptionView.setText(title);
        titleView.setText(description);
        ratingView.setText("Average Rating: " + rating);
        releaseDateView.setText("Release Date: " + releaseDate);

        ButterKnife.setDebug(true);

        ButterKnife.bind(this, titleView);

    }

    public void save(View v) {
        System.out.println("save button");
        onSubmit(v);
    }

    // when the subactivity is complete then it can return the result to the parent
    public void onSubmit(View v) {
        System.out.println("onSubmit");

        Intent data = new Intent();
        data.putExtra("str", "itemText");
        data.putExtra("pos", pos);
        setResult(RESULT_OK, data);
        finish();
    }

    // download the image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
