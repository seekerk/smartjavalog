package org.fruct.oss.smartjavalog.base;

import android.os.AsyncTask;
import android.util.Log;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.iKPIC_subscribeHandler2;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class KPIproxy {
    private static String TAG = "KPIproxy";

    // разные гадости
    //TODO: заменить на rdg4j
    public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String SIB_ANY = "http://www.nokia.com/NRC/M3/sib#any";

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

    QueryRDFTask queryRDF(String subject, String predicate, String object, String subjectType, String objectType) {
        QueryRDFTask task = new QueryRDFTask(this);
        task.setQuery(subject, predicate, object, subjectType, objectType);
        if (!isConnected) {
            task.setError(new IllegalStateException("Not connected to SIB"));
        } else {
            task.execute();
        }
        return task;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public LeaveTask disconnect() {
        LeaveTask task = new LeaveTask(this);
        task.execute();

        return task;
    }

    public JoinTask connect() {
        JoinTask task = new JoinTask(this);
        task.execute();

        return task;
    }

    public InsertTask insert(final ArrayList<ArrayList<String>> newTriples) {
        final InsertTask task = new InsertTask(this);
        if (!isConnected)
            task.setError(new IllegalStateException("Not connected to SIB"));
        else {
            task.setTriples(newTriples);
            task.execute();
        }

        return task;
    }

    public RemoveTask remove(final ArrayList<ArrayList<String>> removeTriples) {
        final RemoveTask task = new RemoveTask(this);
        if (!isConnected)
            task.setError(new IllegalStateException("Not connected to SIB"));
        else {
            task.setTriples(removeTriples);
            task.execute();
        }

        return task;
    }

    public SubscribeTask subscribe(String classURI) {
        SubscribeTask task = new SubscribeTask(this);
        if (!isConnected)
            task.setError(new IllegalStateException("Not connected to SIB"));
        else {
            task.setClassUri(classURI);
            task.execute();
        }

        return task;
    }

    public static class SubscribeTask extends SIBSubscribeTask {
        private String classURI = null;

        private iKPIC_subscribeHandler2 handler = null;
        public SubscribeTask(KPIproxy kpIproxy) { super(kpIproxy);}

        public void setClassUri(String classURI) { this.classURI = classURI; }

        @Override
        protected void doInBackground() {
            if (handler == null) {
                this.ex = new IllegalStateException("Subscription handler not defined");
                return;
            }

            String query = "select ?subject ?predicate ?object where {?subject ?predicate ?object}";
            if (classURI != null && !classURI.isEmpty()) {
                query = "select ?subject ?predicate ?object where {?subject ?predicate ?object . ?subject <" + RDF_TYPE_URI + "> <" + classURI + ">}";
            }
            this.response = proxy.core.subscribeSPARQL(query, handler);
            Log.d(TAG, "Subscribe result:" + response);
        }
    }

    public static class QueryRDFTask extends SIBQueryTask {

        private String subject;
        private String predicate;
        private String object;
        private String subjectType;
        private String objectType;

        public QueryRDFTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            this.response = proxy.core.queryRDF(subject, predicate, object, subjectType, objectType);
            Log.d(TAG, "Query result: " + response);
        }

        public void setQuery(String subject, String predicate, String object, String subjectType, String objectType) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.subjectType = subjectType;
            this.objectType = objectType;
        }
    }

    public static class InsertTask extends SIBQueryTask {
        private ArrayList<ArrayList<String>> triples;


        public InsertTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            if (triples == null) {
                ex = new IllegalArgumentException("Triples not defined");
                return;
            }
            response = proxy.core.insert(triples);
            Log.d(TAG, "Insert results: " + response);
        }

        public void setTriples(ArrayList<ArrayList<String>> triples) {
            this.triples = triples;
        }
    }

    public static class RemoveTask extends SIBQueryTask {
        private ArrayList<ArrayList<String>> triples;


        public RemoveTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            if (triples == null) {
                ex = new IllegalArgumentException("Triples not defined");
                return;
            }
            response = proxy.core.remove(triples);
            Log.d(TAG, "Remove result: " + response);
        }

        public void setTriples(ArrayList<ArrayList<String>> triples) {
            this.triples = triples;
        }
    }

    /**
     * Async leave
     */
    public static class LeaveTask extends SIBQueryTask {

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

    public static class JoinTask extends SIBQueryTask {

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
            if (response != null)
                proxy.isConnected = true;
            else
                ex = new ConnectException("Can't connect to SIB");
        }
    }
}