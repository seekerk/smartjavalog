package org.fruct.oss.smartjavalog.base;

import java.util.ArrayList;
import java.util.List;

import sofia_kp.SIBResponse;

public abstract class SIBAsyncTask {
    private static String TAG = "SIBAsyncTask";

    protected KPIproxy proxy;

    protected Exception ex = null;

    protected SIBResponse response = null;

    public SIBAsyncTask(KPIproxy proxy) {
        this.proxy = proxy;
    }

    public void execute() {
        doInBackground();
        onPostExecute();
    }

    protected abstract void doInBackground();

    protected abstract void onPostExecute();

    public void setError(Exception ex) {
        this.ex = ex;
        onPostExecute();
    }
}