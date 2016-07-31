package com.thyago.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by thyago on 7/27/16.
 */
public class DetailFragment extends Fragment {

    private TextView mDetailTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        String temperature = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mDetailTextView = (TextView) view.findViewById(R.id.detail_textview);
        mDetailTextView.setText(temperature);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        actionProvider.setShareIntent(buildShareIntent());
    }

    private Intent buildShareIntent() {
        final String SHARE_TYPE = "text/plain";
        String shareText = mDetailTextView.getText() + " #SunshineApp";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(SHARE_TYPE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }


}
