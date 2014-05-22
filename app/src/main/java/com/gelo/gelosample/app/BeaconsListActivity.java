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

import com.gelo.gelosdk.GeLoBeaconManager;
import com.gelo.gelosdk.Model.Beacons.GeLoBeacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.gelo.gelosdk.GeLoConstants.GELO_BEACON_FOUND;

public class BeaconsListActivity extends Activity {
    GeLoBeaconManager manager;
    BeaconListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_list);
        manager = GeLoBeaconManager.sharedInstance(getApplicationContext());
        manager.startScanningForBeacons();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(GELO_BEACON_FOUND));

        ListView list = (ListView) findViewById(R.id.listView);
        adapter = new BeaconListAdapter(getApplicationContext(), R.layout.single_list_item, manager.getKnownTourBeacons());
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<GeLoBeacon> knownBeacons = manager.getKnownTourBeacons();
                GeLoBeacon beacon = knownBeacons.get(position);

                Intent detailIntent = new Intent(getBaseContext(), DetailActivity.class);
                detailIntent.putExtra("beaconId", beacon.getBeaconId());
                startActivity(detailIntent);
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateTask(), 0, 1 * 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beacons_list, menu);
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
            if (intent.getAction().equals(GELO_BEACON_FOUND)) {
               //We don't actually use the messages transmitted by the beacon manager
                //This is only included for illustrative purposes on how to listen for intents.
            }
        }
    };

    private static class BeaconListAdapter extends ArrayAdapter<GeLoBeacon> {
        Context context;
        List<GeLoBeacon> beacons;

        public BeaconListAdapter(Context context, int textViewResourceId, List<GeLoBeacon> beacons) {
            super(context, textViewResourceId, beacons);
            this.context = context;
            this.beacons = beacons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(R.layout.single_list_item, parent, false);
            TextView nameView = (TextView) rootView.findViewById(R.id.idView);

            if (position < beacons.size()) {
                GeLoBeacon beacon = beacons.get(position);
                nameView.setText(Integer.toString(beacon.getBeaconId()));
            }

            return rootView;
        }
    }

    class UpdateTask extends TimerTask {
        @Override
        public  void run() {
            BeaconsListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    ArrayList<GeLoBeacon> knownBeacons = manager.getKnownTourBeacons();
                    adapter.addAll(knownBeacons);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
