package com.insequence.flicks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by paulyang on 9/20/16.
 */

// adapter to convert arraylist items into listview items

// http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView

public class MovieItemAdapter extends ArrayAdapter<MovieItem> {
    // View lookup cache


    // private static class ViewHolder {
    static class ViewHolder {

        // commented to use butterknife instead, leave old code here
//         TextView itemPersonName;
//         TextView itemText;
//         ImageView imageView;

        @BindView(R.id.itemPersonName) TextView itemPersonName;
        @BindView(R.id.itemText) TextView itemText;
        @BindView(R.id.imageView3) ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieItemAdapter(Context context, ArrayList<MovieItem> users) {
        super(context, R.layout.movie_item, users);

    }


    // view holder pattern with recycling view items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final MovieItem movieItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        // ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row

            // commented to use butterknife instead, leave old code here
            // viewHolder = new ViewHolder();  // replace with butter

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movie_item, parent, false);
            viewHolder = new ViewHolder(convertView); // butter

            // commented to use butterknife instead, leave old code here
            // replace with butter
//             viewHolder.itemPersonName = (TextView) convertView.findViewById(R.id.itemPersonName);
//             viewHolder.itemText = (TextView) convertView.findViewById(R.id.itemText);
//             viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView3);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }



        // Populate the data into the template view using the data object
        viewHolder.itemPersonName.setText(movieItem.getName());
        viewHolder.itemText.setText(movieItem.getText());

        // get the profile picture from url

        // use 2nd solution:
        //http://stackoverflow.com/questions/5776851/load-image-from-url

        // download the image
        new DownloadImageTask(viewHolder.imageView)
                .execute(movieItem.getPhotoUrl());
//      Return the completed view to render on screen
        return convertView;
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