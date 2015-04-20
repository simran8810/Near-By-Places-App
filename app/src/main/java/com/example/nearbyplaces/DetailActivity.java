package com.example.nearbyplaces;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.example.nearbyplaces";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment()).commit();
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #NearByApp";
        private String mForecastStr;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.activity_detail,
                    container, false);
            if (intent!=null && intent.hasExtra(EXTRA_MESSAGE)){
                String mForecastStr = intent.getStringExtra(EXTRA_MESSAGE);
                ((TextView) rootView.findViewById(R.id.detail_text)).setText(mForecastStr);
            }
            return rootView;
        }

    }
}