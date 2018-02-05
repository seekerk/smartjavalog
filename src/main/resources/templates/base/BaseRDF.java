package org.fruct.oss.smartjavalog.base;

import sofia_kp.SIBResponse;

import java.util.*;
import java.util.logging.Logger;

import static org.fruct.oss.smartjavalog.base.KPIproxy.SIB_ANY;

public abstract class BaseRDF implements UpdateListener {
    private static Logger log = Logger.getLogger(BaseRDF.class.getName());

    protected static long NOTIFICATION_TIMEOUT = 10 * 60 * 1000L;

    protected String _accessPointName;
    private final String _id;
    // датчик случайных чисел для генератора
    private static Random rand = null;

    private InteractionSIBTask loadTask = null;

    protected boolean isDownloaded = false;

    protected boolean isNew = false;

    private List<UpdateListener> listeners = new ArrayList<>();

    // загруженные триплеты
    private final ArrayList<ArrayList<String>> triples = new ArrayList<>();

    private static Map<String, BaseRDFChildInstance> registeredInstances = new HashMap<>();

    private static Map<String, BaseRDFChildInstance> registeredNotifications = new HashMap<>();

    public static void registerInstance(String classUri, String notificationUri, BaseRDFChildInstance handler) {
        registeredInstances.put(classUri, handler);
        registeredNotifications.put(notificationUri, handler);
    }

    public static BaseRDF getInstance(String classUri, String objectId) {
        if (objectId != null)
            return registeredInstances.get(classUri).getInstance(objectId);
        return registeredInstances.get(classUri).getInstance();
    }

    public static BaseRDFChildInstance getNotificatedInstance(String notificationUri) {
        log.info("Search uri=" + notificationUri + " in notifications");
        if (registeredNotifications.containsKey(notificationUri)) {
            return registeredNotifications.get(notificationUri);
        }
        log.info("Notification not found; known keys: " + registeredNotifications.keySet());
        return null;
    }

    public void addListener(UpdateListener listener) {
        if (!this.listeners.contains(listener))
            this.listeners.add(listener);
        else
            return;
        log.warning("Total count of listeners: " + listeners);
        if (isDownloaded)
            listener.onUpdate();
    }

    public boolean removeListener(UpdateListener listener) {
        return this.listeners.remove(listener);
    }

    protected void notifyListeners(Exception ex) {
        for (UpdateListener listener : listeners) {
            if (ex != null)
                listener.onError(ex);
            else
                listener.onUpdate();
        }
    }

    protected BaseRDF(String objectID, String accessPointName) {
        _accessPointName = accessPointName;
        _id = objectID;
    }

    public String getID() {
        return _id;
    }

    public abstract InteractionSIBTask update();

    public InteractionSIBTask download(boolean notifyOther) {
        log.warning("CALL DOWNLOAD for " + getID() + " from " + Thread.currentThread().getStackTrace()[2]);
        if (loadTask != null && !loadTask.isDone())
            return loadTask;

        loadTask = new InteractionSIBTask();
        SIBFactory.getInstance().getAccessPoint(_accessPointName).queryRDF(_id, SIB_ANY, SIB_ANY, "uri", "uri").addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {
                triples.clear();
                triples.addAll(response.query_results);
                loadTask.setSuccess(response);
                isDownloaded = true;
                if (notifyOther)
                    notifyListeners(null);
            }

            @Override
            public void onError(Exception ex) {
                loadTask.setError(ex);
                if (notifyOther)
                    notifyListeners(ex);
            }
        });

        return loadTask;
    }

    public ArrayList<String> getInTriples(String searchURI) {
        ArrayList<String> ret = new ArrayList<>();
        if (this.triples.size() == 0 && !isNew && !isDownloaded) {
            log.warning("CALL DOWNLOAD FOR INSTANCE " + getID() + ": isNew=" + isNew + "; isDownloaded=" + isDownloaded);
            download(true);
        }
        for (ArrayList<String> t : this.triples) {
            if (t.contains(searchURI)) {
                ret.add(t.get(2));
            }
        }
        return ret;
    }

    void addTriple(List<String> triple) {
        this.triples.add(new ArrayList<>(triple));
        isNew = false;
        notifyListeners(null);
    }

    void removeTriple(List<String> triple) {
        for (ArrayList<String> oldTriple : this.triples) {
            if (oldTriple.containsAll(triple)) {
                this.triples.remove(oldTriple);
                notifyListeners(null);
                return;
            }
        }
    }

    /**
     *  функция для удаления свойства объекта из SIB
     * @param URI адрес свойства
     */
    public abstract void removeProperty(String URI);

    public static String generateID(String prefix) {
        if (rand == null) {
            rand = new Random();
        }
        return String.format("%s%s", prefix, Long.toString(rand.nextLong()));
    }

    /**
     * Создание триплета с объектом и субъектом типа "uri"
     * @param object объект (uri)
     * @param predicate связь (uri)
     * @param subject субъект (uri)
     * @return триплет в виде массива
     */
    public static ArrayList<String> createTriple(String object, String predicate, String subject) {
        return createTriple(object, predicate, subject, "uri", "uri");
    }

    public static ArrayList<String> createTriple(String object, String predicate, Long subject) {
        return createTriple(object, predicate, String.valueOf(subject), "uri", "literal");
    }

    /**
     * Создание триплета (объект->предикат->субъект)
     * @param object объект (uri)
     * @param predicate связь (uri)
     * @param subject субъект (uri)
     * @param objectType тип объекта ("uri" or "literal")
     * @param subjectType тип субъекта ("uri" or "literal")
     * @return триплет в виде массива
     */
    public static ArrayList<String> createTriple(String object, String predicate, String subject, String objectType, String subjectType) {
            ArrayList<String> ret = new ArrayList<>(5);
            ret.add(object);
            ret.add(predicate);
            ret.add(subject);
            ret.add(objectType);
            ret.add(subjectType);
            return ret;
    }

    public boolean isDownloaded() {
        return isDownloaded || isNew;
    }

    @Override
    public void onUpdate() {
        notifyListeners(null);
    }

    @Override
    public void onError(Exception ex) {
        notifyListeners(ex);
    }


    public static class InteractionSIBTask {

        protected Exception ex = null;

        protected SIBResponse response = null;

        protected List<TaskListener> listeners = new ArrayList<>();

        private boolean isDone = false;

        public boolean isDone() {
            synchronized (this) {
                return isDone;
            }
        }

        private void setDone(boolean isDone) {
            synchronized (this) {
                this.isDone = isDone;
            }
        }

        public void addListener(TaskListener taskListener) {
            listeners.add(taskListener);

            if (ex != null)
                taskListener.onError(ex);

            if (response != null)
                taskListener.onSuccess(response);
        }

        public void setError(Exception ex) {
            this.ex = ex;
            onPostExecute();
        }

        public void setSuccess(SIBResponse response) {
            this.response = response;
            this.ex = null;
            onPostExecute();
        }

        protected void onPostExecute() {
            setDone(true);
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
    }

    /**
     * Provides interface for instance creation of child classes.
     */
    public interface BaseRDFChildInstance {
        /**
         * Get instance of child class with defined ID.
         * @param objectId id
         */
        BaseRDF getInstance(String objectId);

        /**
         * Get new instance of child class.
         */
        BaseRDF getInstance();
    }
}
