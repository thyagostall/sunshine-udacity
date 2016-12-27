/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private final Context mContext;
    private final ForecastAdapterOnClickHandler mClickHandler;
    private final View mEmptyView;

    private Cursor mCursor;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mIconView;
        public final TextView mDateView;
        public final TextView mDescriptionView;
        public final TextView mHighTempView;
        public final TextView mLowTempView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.list_item_icon);
            mDateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mDescriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            mHighTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            mLowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
        }
    }

    public interface ForecastAdapterOnClickHandler {
        void onClick(Long date, ForecastAdapterViewHolder viewHolder);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler clickHandler, View emptyView) {
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = clickHandler;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!(parent instanceof RecyclerView)) {
            throw new RuntimeException("Not bound to RecyclerView");
        }

        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layoutId, parent, false);

        view.setFocusable(false);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int fallbackId;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                fallbackId = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                fallbackId = Utility.getIconResourceForWeatherCondition(weatherId);

        }

        if (Utility.isUsingLocalGraphics(mContext)) {
            viewHolder.mIconView.setImageResource(fallbackId);
        } else {
            Glide.with(mContext)
                    .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(fallbackId)
                    .crossFade()
                    .into(viewHolder.mIconView);
        }

        // Read date from mCursor
        long dateInMillis = mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        viewHolder.mDateView.setText(Utility.getFriendlyDayString(mContext, dateInMillis));

        // Read weather forecast from mCursor
        String description = Utility.getStringForWeatherCondition(mContext, weatherId);
        // Find TextView and set weather forecast on it
        viewHolder.mDescriptionView.setText(description);
        viewHolder.mDescriptionView.setContentDescription(mContext.getString(R.string.a11y_forecast, description));

        // For accessibility, add a content description to the icon field
        viewHolder.mIconView.setContentDescription(description);

        // Read high temperature from mCursor
        String high = Utility.formatTemperature(mContext, mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP));
        viewHolder.mHighTempView.setText(high);
        viewHolder.mHighTempView.setContentDescription(mContext.getString(R.string.a11y_high_temp, high));

        // Read low temperature from mCursor
        String low = Utility.formatTemperature(mContext, mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));
        viewHolder.mLowTempView.setText(low);
        viewHolder.mLowTempView.setContentDescription(mContext.getString(R.string.a11y_low_temp, low));
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }
}