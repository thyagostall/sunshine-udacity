package com.thyago.sunshine;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    public static class MainFragment extends Fragment {
        public MainFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ArrayList<String> fakeData = new ArrayList<>(7);
            fakeData.add("Today - Sunny - 88/63");
            fakeData.add("Tomorrow - Foggy - 70/46");
            fakeData.add("Wed - Cloudy - 72/63");
            fakeData.add("Thurs - Rainy - 64/51");
            fakeData.add("Fri - Foggy - 70/46");
            fakeData.add("Sat - Sunny - 76/68");
            fakeData.add("Sun - Sunny - 80/66");

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, fakeData);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(adapter);

            return rootView;
        }
    }
}
