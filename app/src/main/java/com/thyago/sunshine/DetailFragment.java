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
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        String temperature = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        TextView detailTextView = (TextView) view.findViewById(R.id.detail_textview);
        detailTextView.setText(temperature);

        return view;
    }
}
