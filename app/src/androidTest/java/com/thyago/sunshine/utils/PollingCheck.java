package com.thyago.sunshine.utils;

/**
 * Created by thyago on 8/1/16.
 */
public abstract class PollingCheck {
    private static final long TIME_SLICE = 50;
    private long mTimeout = 3000;

    public PollingCheck() {
    }

    public PollingCheck(long timeout) {
        mTimeout = timeout;
    }

    protected abstract boolean check();

    public void run() {
        if (check()) {
            return;
        }

        long timeout = mTimeout;
        while (timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE);
            } catch (InterruptedException e) {
            }

            if (check()) {
                return;
            }

            timeout -= TIME_SLICE;
        }
    }
}
