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

import static org.fruct.oss.smartjavalog.base.KPIproxy.SIB_ANY;

/**
 * Manage subscriptions by filtering incoming triplets.
 */
public class SubscribeQuery implements iKPIC_subscribeHandler2 {
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

    private List<SoftReference<BaseRDF>> knownObjects = new ArrayList<>();

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
            task.setHandler(this);
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
    void registerObject(BaseRDF object) {
        this.knownObjects.add(new SoftReference<>(object));
    }

    @Override
    public void kpic_RDFEventHandler(ArrayList<ArrayList<String>> newTriples, ArrayList<ArrayList<String>> oldTriples, String indSequence, String subID) {
        log.info("kpic_RDFEventHandler():" + newTriples + "; " + oldTriples);
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
