package com.gelo.gelosample.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gelo.gelosdk.GeLoPlatformManager;
import com.gelo.gelosdk.Model.Infos.GeLoBeaconInfo;
import com.gelo.gelosdk.Model.Infos.GeLoTourInfo;

import java.util.ArrayList;


public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        GeLoPlatformManager manager = GeLoPlatformManager.sharedInstance(getApplicationContext());
        int beaconId = getIntent().getIntExtra("beaconId", -1);
        GeLoBeaconInfo beacon = null;

        if (beaconId != -1) {
            ArrayList<GeLoBeaconInfo> beacons = manager.currentTour().getBeaconInfos();
            for (GeLoBeaconInfo info : beacons) {
                if (info.getBeaconId() == beaconId) {
                    beacon = info;
                    break;
                }
            }
        }

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView textView = (TextView) findViewById(R.id.textView);

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.widthPixels * 9/16);
        imageView.setLayoutParams(lp);

        if (beacon != null ) {
            if (beacon instanceof GeLoTourInfo) {
                textView.setText(((GeLoTourInfo) beacon).getDescription());
                String imageUrl = ((GeLoTourInfo) beacon).getMedia().get(0).getUrl();
                if (imageUrl != null) {
                    Uri path = manager.getMediaUri(imageUrl);
                    Bitmap logoBmp = BitmapFactory.decodeFile(path.getPath());
                    imageView.setImageBitmap(logoBmp);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
