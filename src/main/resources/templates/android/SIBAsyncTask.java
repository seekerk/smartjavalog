package org.fruct.oss.smartjavalog.base;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import sofia_kp.SIBResponse;

/**
 * Android implementation for execution tasks.
 */
public abstract class SIBAsyncTask {
    private static String TAG = "SIBAsyncTask";

    protected KPIproxy proxy;

    protected Exception ex = null;

    protected SIBResponse response = null;

    public SIBAsyncTask(KPIproxy proxy) {
        this.proxy = proxy;
    }

    public void execute() {
        NativeAsyncTask task = new NativeAsyncTask(this);
        task.execute();
    }

    protected abstract void doInBackground();

    protected abstract void onPostExecute();

    public void setError(Exception ex) {
        this.ex = ex;
        onPostExecute();
    }

    /**
     * Native implementation of required multithread model for Android through AsyncTask
     */
    private static class NativeAsyncTask extends AsyncTask<Void, Void, Void> {
        SIBAsyncTask that;

        NativeAsyncTask(SIBAsyncTask that) {
            this.that = that;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            that.doInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            that.onPostExecute();
        }
    }
}