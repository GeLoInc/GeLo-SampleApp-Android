package com.gelo.gelosample.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gelo.gelosdk.GeLoPlatformManager;
import com.gelo.gelosdk.Model.GeLoSite;
import com.gelo.gelosdk.Model.GeLoTour;

import java.util.List;

import static com.gelo.gelosdk.GeLoConstants.GELO_NETWORK_REQUEST_ERRORED;
import static com.gelo.gelosdk.GeLoConstants.GELO_SITE_LOADED;


public class MainActivity extends Activity {

    GeLoPlatformManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = GeLoPlatformManager.sharedInstance(getApplicationContext());
        //OAuth credentials can be retrieved from the GeLo platform.
        manager.setOAuth2(clientId, username, password);
        GeLoSite site = manager.loadSiteById(siteId);
        if (site != null) {
            manager.setCurrentSite(site);
            setListView();
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(GELO_SITE_LOADED));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(GELO_NETWORK_REQUEST_ERRORED));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GELO_SITE_LOADED)) {
                GeLoSite site = (GeLoSite) intent.getSerializableExtra("site");
                manager.setCurrentSite(site);
                setListView();
            }
            if (intent.getAction().equals(GELO_NETWORK_REQUEST_ERRORED)) {
                Toast.makeText(getApplicationContext(), "Failed to contact platform", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void setListView() {
        ListView list = (ListView) findViewById(R.id.listView);
        GeLoSite site = manager.currentSite();
        TourListAdapter adapter = new TourListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, site.getTours());

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GeLoSite site = manager.currentSite();
                GeLoTour tour = site.getTours().get(position);
                manager.setCurrentTour(tour);

                Intent beaconIntent = new Intent(getBaseContext(), BeaconsListActivity.class);
                startActivity(beaconIntent);
            }
        });
    }

    private static class TourListAdapter extends ArrayAdapter<GeLoTour> {
        Context context;
        List<GeLoTour> tours;

        public TourListAdapter(Context context, int textViewResourceId, List<GeLoTour> tours) {
            super(context, textViewResourceId, tours);
            this.context = context;
            this.tours = tours;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(R.layout.single_list_item, parent, false);
            TextView nameView = (TextView) rootView.findViewById(R.id.idView);

            if (position < tours.size()) {
                GeLoTour tour = tours.get(position);
                nameView.setText(tour.getName());
            }

            return rootView;
        }

    }
}
