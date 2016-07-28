package com.thyago.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by thyago on 7/27/16.
 */
public class DetailFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView detailTextView = (TextView) result.findViewById(R.id.forecast_detail_textview);

        Intent intent = getActivity().getIntent();
        detailTextView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));

        return result;
    }
}
