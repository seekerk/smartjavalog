package $PACKAGE_NAME$.base;

import android.os.AsyncTask;
import android.util.Log;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;

import java.util.ArrayList;
import java.util.List;

public class KPIproxy {
    private static String TAG = "KPIproxy";

    private KPICore core;
    private boolean isConnected = false;

    public KPIproxy(String host, int port, String name) {
        core = new KPICore(host, port, name);
    }

    public void setAddr(String host, int port) {
        if (isConnected)
            disconnect();

        core.HOST = host;
        core.PORT = port;
    }

    SIBResponse queryRDF(String subject, String predicate, String object, String subjectType, String objectType) {
        if (!isConnected)
            connect();

        return core.queryRDF(subject, predicate, object, subjectType, objectType);
    }

    public void disconnect() {
        LeaveTask task = new LeaveTask(this);
        task.execute();
    }

    public JoinTask connect() {
        JoinTask task = new JoinTask(this);
        task.execute();
        return task;
    }

    public SIBResponse insert(ArrayList<ArrayList<String>> newTriples) {
        if (!isConnected)
            connect();

        return core.insert(newTriples);
    }

    public SIBResponse remove(ArrayList<ArrayList<String>> removeTriples) {
        if (!isConnected)
            connect();

        return core.remove(removeTriples);
    }

    /**
     * Async leave
     */
    private static class LeaveTask extends SIBAsyncTask {

        LeaveTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            try {
                this.response = proxy.core.leave();
            } catch (SecurityException ex) {
                this.ex = ex;
                Log.w(TAG, ex);
            }
            //TODO: проверить корректность подключения
            Log.d(TAG, "Leave result: " + this.response);
            proxy.isConnected = false;
        }
    }

    public static class JoinTask extends SIBAsyncTask {

        JoinTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            try {
                this.response = proxy.core.join();
            } catch (SecurityException ex) {
                this.ex = ex;
                return;
            }
            //TODO: проверить корректность подключения
            Log.d(TAG, "Joint result: " + this.response);
            proxy.isConnected = true;
            return;
        }
    }
}