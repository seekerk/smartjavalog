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

    protected List<TaskListener> listeners = new ArrayList<>();

    public SIBAsyncTask(KPIproxy proxy) {
        this.proxy = proxy;
    }

    public void addListener(TaskListener taskListener) {
        listeners.add(taskListener);

        if (ex != null)
            taskListener.onError(ex);

        if (response != null)
            taskListener.onSuccess(response);
    }

    public void execute() {
        NativeAsyncTask task = new NativeAsyncTask();
        task.execute();
    }

    protected abstract void doInBackground();

    public void setError(Exception ex) {
        this.ex = ex;
        onPostExecute();
    }

    protected void onPostExecute() {
        if (ex != null) {
            for (TaskListener listener : listeners) {
                listener.onError(ex);
            }
            return;
        }
        for (TaskListener listener : listeners) {
            listener.onSuccess(response);
        }
    }


    private class NativeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SIBAsyncTask.this.doInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SIBAsyncTask.this.onPostExecute();
        }
    }
}