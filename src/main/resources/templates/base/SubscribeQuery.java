package org.fruct.oss.smartjavalog.base;

import sofia_kp.SIBResponse;
import sofia_kp.SSAP_sparql_response;
import sofia_kp.iKPIC_subscribeHandler2;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.fruct.oss.smartjavalog.base.KPIproxy.RDF_TYPE_URI;
import static org.fruct.oss.smartjavalog.base.KPIproxy.SIB_ANY;

/**
 * Manage subscriptions by filtering incoming triplets.
 */
public class SubscribeQuery {
    private static Logger log = Logger.getLogger(SubscribeQuery.class.getName());

    /**
     * Flag shows subscription availability.
     */
    private boolean subscribed = false;

    /**
     * Implement singleton template.
     */
    private static SubscribeQuery instance = null;

    private Map<String, List<SubscribeListener>> classSubscriptions = new HashMap<>();

    private Map<String, SoftReference<BaseRDF>> knownItems = new HashMap<>();

    private iKPIC_subscribeHandler2 handler = new iKPIC_subscribeHandler2() {
        /**
         * Process subscription event
         * @param newTriples list of new triples
         * @param oldTriples list of old triples
         * @param indSequence sequence number
         * @param subID subsequence number
         */
        @Override
        public void kpic_RDFEventHandler(ArrayList<ArrayList<String>> newTriples, ArrayList<ArrayList<String>> oldTriples, String indSequence, String subID) {
            log.info("kpic_RDFEventHandler(): seq=" + indSequence  + "(" + subID + ")");
            for (ArrayList<String> triple: oldTriples) {
                log.info("Remove triple " + triple);
                if (triple.get(1).equals(RDF_TYPE_URI)) {
                    process_remove_item(triple);
                }else{
                    process_remove_value(triple);
                }
            }

            for(ArrayList<String> triple : newTriples) {
                //[0] - subject (id)
                //[1] - predicate (property | type)
                //[2] - object (value | class)
                //[3] - object type
                log.info("Process triple " + triple);
                if (triple.get(1).equals(RDF_TYPE_URI)) {
                    process_insert_item(triple);
                }
                process_insert_value(triple);
            }

        }

        @Override
        public void kpic_SPARQLEventHandler(SSAP_sparql_response newResults, SSAP_sparql_response oldResults, String indSequence, String subID) {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void kpic_UnsubscribeEventHandler(String sub_ID) {
            log.info("kpic_UnsubscribeEventHandler(): " + subID);
        }

        @Override
        public void kpic_ExceptionEventHandler(Throwable SocketException) {
            log.severe("kpic_ExceptionEventHandler(): " + SocketException);
        }
    };

    private void process_remove_value(ArrayList<String> triple) {
        if (knownItems.containsKey(triple.get(0)) && knownItems.get(triple.get(0)).get() != null) {
            BaseRDF obj = knownItems.get(triple.get(0)).get();
            obj.removeTriple(triple);
        }
    }

    private void process_insert_value(ArrayList<String> triple) {
        //[0] - subject (id)
        //[1] - predicate (property | type)
        //[2] - object (value | class)
        //[3] - object type
        if (knownItems.containsKey(triple.get(0)) && knownItems.get(triple.get(0)).get() != null) {
            BaseRDF obj = knownItems.get(triple.get(0)).get();
            obj.addTriple(triple);
        }
    }

    private void process_insert_item(ArrayList<String> triple) {
        log.info("process_insert_item for class: " + triple.get(2));
        if (classSubscriptions.containsKey(triple.get(2))) {
            BaseRDF newItem = BaseRDF.getInstance(triple.get(2), triple.get(0));
            log.info("notify listeners: " + triple.get(0));
            for (SubscribeListener listener : classSubscriptions.get(triple.get(2))) {
                listener.addItem(newItem);
            }
        }
    }

    /**
     * process removing item. If we have corresponding subscription, then notify about it
     * @param triple
     */
    private void process_remove_item(ArrayList<String> triple) {
        //[0] - subject (id)
        //[1] - predicate (property | type)
        //[2] - object (value | class)
        //[3] - object type
        if (classSubscriptions.containsKey(triple.get(2))) {
            BaseRDF newItem = BaseRDF.getInstance(triple.get(2), triple.get(0));
            for (SubscribeListener listener : classSubscriptions.get(triple.get(2))) {
                listener.removeItem(newItem);
            }
        }
    }

    /**
     * Hide constructor. Use {@link #getInstance()} to get class instance.
     */
    private SubscribeQuery() {}

    /**
     * Implement singleton template.
     * @return class instance.
     */
    public static SubscribeQuery getInstance() {
        if (instance == null)
            instance = new SubscribeQuery();

        return instance;
    }

    /**
     * Enable subscription to all triples.
     * Check subscription status by using {@link #isSubscribed()} before call this method.
     * @return Subscription handler. If Subscription already enabled then handler returns Exception.
     */
    public SubscribeTask subscribe() {
        //@TODO: если точек несколько, то подписаться ко всем!
        SubscribeTask task = new SubscribeTask(SIBFactory.getInstance().getAccessPoint());
        if (!SIBFactory.getInstance().getAccessPoint().isConnected())
            task.setError(new IllegalStateException("Not connected to SIB"));
        else {
            task.setHandler(this.handler);
            task.execute();
            task.addListener(new TaskListener() {
                @Override
                public void onSuccess(SIBResponse response) {
                    subscribed = true;
                }
            });
        }

        return task;
    }

    /**
     * Disable subscription to all triples
     * @return Unsubscription handler. If subscription is disabled, then handler returns Exception.
     */
    UnsubscribeTask unsubscribe() {
        UnsubscribeTask task = new UnsubscribeTask(SIBFactory.getInstance().getAccessPoint());
        if (!SIBFactory.getInstance().getAccessPoint().isConnected())
            task.setError(new IllegalStateException("Not connected to SIB"));
        else {
            task.execute();
            task.addListener(new TaskListener() {
                @Override
                public void onSuccess(SIBResponse response) {
                    subscribed = false;
                }
            });
        }

        return task;
    }

    /**
     * Check subscription status.
     * @return true if subscription is enabled, otherwise - false.
     */
    public boolean isSubscribed() { return subscribed; }

    /**
     * Add subscription to the appearance and deletion of instances of a class.
     * @param classURI the URI of class. Use getClassUri() to obtain class URI.
     * @param listener Implementation of {@link SubscribeListener} interface.
     */
    public void addSubscription(String classURI, SubscribeListener listener) {
        if (!this.classSubscriptions.containsKey(classURI))
            this.classSubscriptions.put(classURI, new ArrayList<>());
        this.classSubscriptions.get(classURI).add(listener);
    }

    /**
     * Remove subscription to the appearance and deletion of instances of a class.
     * Be sure that you call {@link #addSubscription(String, SubscribeListener)} before subscription removing.
     * @param classURI the URI of class. Use getClassUri() to obtain class URI.
     * @param listener Implementation of {@link SubscribeListener} interface.
     * @return true if subsription removed correctly.
     */
    public boolean removeSubscription(String classURI, SubscribeListener listener) {
        return this.classSubscriptions.get(classURI).remove(listener);
    }

    /**
     * Register object for updating during subsription.
     * @param object Object instance.
     */
    public void registerObject(BaseRDF object) {
        this.knownItems.put(object.getID(), new SoftReference<>(object));
    }

    public BaseRDF getKnownObject(String objectId) {
        if (knownItems.containsKey(objectId))
            return knownItems.get(objectId).get();

        return null;
    }

    public static class SubscribeTask extends SIBQueryTask {

        private iKPIC_subscribeHandler2 handler;

        SubscribeTask(KPIproxy kpIproxy) { super(kpIproxy);}

        void setHandler(iKPIC_subscribeHandler2 handler) { this.handler = handler; }

        @Override
        protected void doInBackground() {
            this.response = proxy.getCore().subscribeRDF(SIB_ANY, SIB_ANY, SIB_ANY, "uri", handler);
            if (!this.response.isConfirmed()) {
                this.ex = new Exception("Subscription error: " + proxy.getCore().getErrMess());
            }
            log.info("Subscribe result:" + response);
        }
    }

    public static class UnsubscribeTask extends SIBQueryTask {

        UnsubscribeTask(KPIproxy kpIproxy) { super(kpIproxy);}

        @Override
        protected void doInBackground() {
            this.response = proxy.getCore().unsubscribe();
            if (!this.response.isConfirmed()) {
                this.ex = new Exception("Unsubscription error: " + proxy.getCore().getErrMess());
            }
            log.info("Subscribe result:" + response);
        }
    }

}
