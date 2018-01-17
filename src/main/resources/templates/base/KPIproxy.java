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
    private static class LeaveTask extends AsyncTask<Void, Void, Void> {

        KPIproxy that;

        List<TaskListener> listeners = new ArrayList<>();
        Exception ex = null;

        LeaveTask(KPIproxy proxy) {
            this.that = proxy;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SIBResponse resp = null;
            try {
                resp = that.core.leave();
            } catch (SecurityException ex) {
                this.ex = ex;
                Log.w(TAG, ex);
            }
            //TODO: проверить корректность подключения
            Log.d(TAG, "Leave result: " + resp);
            that.isConnected = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ex != null) {
                for (TaskListener listener : listeners) {
                    listener.onError(ex);
                }
            } else {
                for (TaskListener listener : listeners) {
                    listener.onSuccess();
                }
            }
        }
    }

    public static class JoinTask extends AsyncTask<Void, Void, Void> {
        KPIproxy that;

        List<TaskListener> listeners = new ArrayList<>();
        Exception ex = null;

        JoinTask(KPIproxy proxy) {
            this.that = proxy;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SIBResponse resp = null;
            try {
                resp = that.core.join();
            } catch (SecurityException ex) {
                this.ex = ex;
                return null;
            }
            //TODO: проверить корректность подключения
            Log.d(TAG, "Joint result: " + resp);
            that.isConnected = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ex != null) {
                for (TaskListener listener : listeners) {
                    listener.onError(ex);
                }
            } else {
                for (TaskListener listener : listeners) {
                    listener.onSuccess();
                }
            }
        }

        public void addListener(TaskListener taskListener) {
            listeners.add(taskListener);
        }
    }
}