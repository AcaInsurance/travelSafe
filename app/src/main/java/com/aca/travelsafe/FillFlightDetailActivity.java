package com.aca.travelsafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aca.travelsafe.Fragment.FillCustomerDetailFragment;
import com.aca.travelsafe.Fragment.FillFlightDetailFragment;
import com.aca.travelsafe.Util.var;

public class FillFlightDetailActivity extends BaseActivity implements FillFlightDetailFragment.FillFlightDetailFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_flight_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFragment();
//        getIntentData();

    }




    private void initFragment() {
        if (getIntent().getExtras() != null) {
            String id = getIntent().getExtras().get(var.id).toString();
            setFragmentNoBackStack(R.id.fragment, FillFlightDetailFragment.newInstance(id));
        }
        else
            setFragmentNoBackStack(R.id.fragment, FillFlightDetailFragment.newInstance());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goBack() {
        setResult(RESULT_OK);
        this.finish();
    }
}
