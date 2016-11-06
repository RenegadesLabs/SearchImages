package com.renegades.labs.searchimages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Виталик on 04.11.2016.
 */

public class GridActivity extends AppCompatActivity {
    Parser parser;
    String searchTerm;
    GridView gridView;
    CircularProgressView progressView;
    Context context;
    ImageAdapter imageAdapter;
    List<Bitmap> bitmapsList;
    int numColumns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        Intent intent = getIntent();
        searchTerm = intent.getStringExtra("searchTerm");
        numColumns = intent.getIntExtra("columnsQuantity", 2);
        parser = new Parser();

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setVisibility(View.GONE);
        gridView.setNumColumns(numColumns);
        context = this;

        progressView = (CircularProgressView) findViewById(R.id.progressView);
        progressView.setVisibility(View.VISIBLE);

        bitmapsList = new ArrayList<>();
        imageAdapter = new ImageAdapter(context);
        gridView.setAdapter(imageAdapter);

        ParseImages parseImages = new ParseImages();
        parseImages.execute();
    }

    class ParseImages extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            return parser.getStrings(searchTerm);
        }

        @Override
        protected void onPostExecute(List<String> imgStrings) {
            progressView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            DownloadImageTask downloadImageTask = new DownloadImageTask();
            downloadImageTask.execute(imgStrings);
        }
    }

    private class DownloadImageTask extends AsyncTask<List<String>, Void, Void> {

        @Override
        protected Void doInBackground(List<String>... params) {

            try {
                for (String s : params[0]) {
                    InputStream in = new java.net.URL(s).openStream();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(in, null, options);

                    options.inSampleSize = calculateInSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;

                    in.close();

                    in = new java.net.URL(s).openStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                    if (bitmap != null) {
                        bitmapsList.add(bitmap);
                    }
                    in.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageAdapter.notifyDataSetChanged();
                            gridView.setAdapter(imageAdapter);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return bitmapsList.size();
        }

        @Override
        public Object getItem(int position) {
            ImageView imageView = new ImageView(mContext);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            width = (width - 32) / numColumns;

            imageView.setLayoutParams(new GridView.LayoutParams(width, width));
            imageView.setPadding(8, 8, 8, 8);

            imageView.setImageBitmap(bitmapsList.get(position));

            return imageView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {

                imageView = new ImageView(mContext);

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels;
                width = (width - 32) / numColumns;

                imageView.setLayoutParams(new GridView.LayoutParams(width, width));
                imageView.setPadding(8, 8, 8, 8);

                imageView.setImageBitmap(bitmapsList.get(position));
            } else {
                imageView = (ImageView) convertView;
            }

            return imageView;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
