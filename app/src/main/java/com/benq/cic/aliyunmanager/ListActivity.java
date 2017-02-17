package com.benq.cic.aliyunmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ListActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListFragment fragment = ListFragment.newInstance("");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, "root")
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(String item) {
        ListFragment fragment = ListFragment.newInstance(item);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, item)
                .addToBackStack(null)
                .commit();
    }
}
