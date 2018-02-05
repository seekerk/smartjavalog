package org.fruct.oss.smartjavalog.base;

import sofia_kp.KPICore;
import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import sofia_kp.iKPIC_subscribeHandler2;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class KPIproxy {
    private static Logger log = Logger.getLogger(KPIproxy.class.getName());

    // разные гадости
    //TODO: заменить на rdg4j
    public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String SIB_ANY = "http://www.nokia.com/NRC/M3/sib#any";

    public static final String NOTIFICATION_INDIVIDE = "http://oss.fruct.org/smartjavalog#notificationIndivide";
    public static final String NOTIFICATION_UPDATE_TIME = "http://oss.fruct.org/smartjavalog#notificationUpdateTime";

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

    public KPICore getCore() {
        return core;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public LeaveTask disconnect() {
        LeaveTask task = new LeaveTask(this);
        SubscribeQuery.getInstance().unsubscribe().addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {
                task.execute();
            }

            @Override
            public void onError(Exception ex) {
                //task.setError(ex);
                task.execute();
            }
        });

        return task;
    }

    public JoinTask connect() {
        JoinTask task = new JoinTask(this);
        task.execute();
        // after connection start subscription
        task.addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {
                SubscribeQuery.getInstance().subscribe().addListener(new TaskListener() {
                    @Override
                    public void onSuccess(SIBResponse response) {
                        log.info("Subscription connected");
                    }

                    @Override
                    public void onError(Exception ex) {
                        task.setError(ex);
                    }
                });

            }
        });

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

    /**
     * Load all known instances of selected class from SIB
     * @param classURI class URI
     * @return Async task with executed query
     */
    public QueryClassTask queryClass(String classURI) {
        QueryClassTask task = new QueryClassTask(this);
        task.setClassURI(classURI);
        if (!isConnected)
            task.setError(new IllegalStateException("Not connected to SIB"));
        else
            task.execute();
        return task;
    }

    public RemoveTask removeInstance(String instanceId) {
        ArrayList<ArrayList<String>> triples = new ArrayList<>(1);
        triples.add(BaseRDF.createTriple(instanceId, SIB_ANY, SIB_ANY));
        return remove(triples);
    }

    /**
     * Async task to download all known instances of selected class
     */
    public static class QueryClassTask extends SIBQueryTask {
        private String classURI;

        private List<QueryListener> classListeners = new ArrayList<>();

        void setClassURI(String classURI) {
            this.classURI = classURI;
        }

        public QueryClassTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            this.response = proxy.core.queryRDF(SIB_ANY, RDF_TYPE_URI, classURI, "uri", "uri");
            log.info("Class query result size=" + response.query_results.size());
            if (!this.response.isConfirmed()) {
                this.ex = new Exception("Query error: " + proxy.core.getErrMess());
            }
        }

        @Override
        protected void onPostExecute() {
            super.onPostExecute();
            if (ex != null) {
                for (QueryListener listener: classListeners) {
                    listener.onError(ex);
                }
                return;
            }

            for (ArrayList<String> triple : response.query_results) {
                //[0] - object [id]
                //[1] - predicate [typeof]
                //[2] - subject [type]
                if (!triple.get(2).equals(classURI)) {
                    log.warning("Different types: " + classURI + " vs. " + triple.get(2));
                    continue;
                }
                BaseRDF ret = BaseRDF.getInstance(triple.get(2), triple.get(0));

                for (QueryListener listener : classListeners) {
                    listener.addItem(ret);
                }
            }
        }

        public void addListener(QueryListener listener) {
            classListeners.add(listener);
            if (ex != null) {
                listener.onError(ex);
                return;
            }
            if (response != null && response.query_results.size() > 0) {
                for (ArrayList<String> triple : response.query_results) {
                    //[0] - object [id]
                    //[1] - predicate [typeof]
                    //[2] - subject [type]
                    if (!triple.get(2).equals(classURI)) {
                        log.warning("Different types: " + classURI + " vs. " + triple.get(2));
                        continue;
                    }
                    BaseRDF ret = BaseRDF.getInstance(triple.get(2), triple.get(0));
                    listener.addItem(ret);
                }
            }
        }
    }

    /**
     * Async task to download results of RDF query.
     */
    public static class QueryRDFTask extends SIBQueryTask {

        private String subject;
        private String predicate;
        private String object;
        private String subjectType;
        private String objectType;

        QueryRDFTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            this.response = proxy.core.queryRDF(subject, predicate, object, subjectType, objectType);
            log.info("Query result size=" + response.query_results.size());
            if (!this.response.isConfirmed())
                this.ex = new Exception("Query error: " + proxy.core.getErrMess());
        }

        void setQuery(String subject, String predicate, String object, String subjectType, String objectType) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.subjectType = subjectType;
            this.objectType = objectType;
        }
    }

    public static class InsertTask extends SIBQueryTask {
        private ArrayList<ArrayList<String>> triples;


        InsertTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            if (triples == null) {
                ex = new IllegalArgumentException("Triples not defined");
                return;
            }
            response = proxy.core.insert(triples);
            if (!response.isConfirmed())
                this.ex = new Exception("Insert error: " + proxy.core.getErrMess());
            log.info("Insert results: " + response);
        }

        void setTriples(ArrayList<ArrayList<String>> triples) {
            this.triples = triples;
        }
    }

    public static class RemoveTask extends SIBQueryTask {
        private ArrayList<ArrayList<String>> triples;


        RemoveTask(KPIproxy proxy) {
            super(proxy);
        }

        @Override
        protected void doInBackground() {
            if (triples == null) {
                ex = new IllegalArgumentException("Triples not defined");
                return;
            }
            response = proxy.core.remove(triples);
            if (!response.isConfirmed())
                ex = new Exception("Remove error: " + proxy.core.getErrMess());
            log.info("Remove result: " + response);
        }

        void setTriples(ArrayList<ArrayList<String>> triples) {
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
                log.info(ex.toString());
            }
            //TODO: проверить корректность подключения
            log.info("Leave result: " + this.response);
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
            log.info( "Joint result: " + this.response);
            if (response.isConfirmed())
                proxy.isConnected = true;
            else {
                this.ex = new ConnectException("Can't connect to SIB: " + proxy.core.getErrMess());
            }
        }
    }
}