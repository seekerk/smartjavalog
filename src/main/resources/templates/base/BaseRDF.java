package org.fruct.oss.smartjavalog.base;

import sofia_kp.SIBResponse;

import java.util.*;

import static org.fruct.oss.smartjavalog.base.KPIproxy.SIB_ANY;

public abstract class BaseRDF {

    protected String _accessPointName;
    private final String _id;
    // датчик случайных чисел для генератора
    private static Random rand = null;

    private InteractionSIBTask loadTask = null;

    private boolean isDownloaded = false;

    private List<UpdateListener> listeners = new ArrayList<>();

    // загруженные триплеты
    private final ArrayList<ArrayList<String>> triples = new ArrayList<>();

    private static Map<String, BaseRDFChildInstance> registeredInstances = new HashMap<>();

    public static void registerInstance(String classUri, BaseRDFChildInstance handler) {
        registeredInstances.put(classUri, handler);
    }

    public static BaseRDF getInstance(String classUri, String objectId) {
        if (objectId != null)
            return registeredInstances.get(classUri).getInstance(objectId);
        return registeredInstances.get(classUri).getInstance();
    }

    public void addListener(UpdateListener listener) {
        this.listeners.add(listener);
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

    public InteractionSIBTask download() {
        if (loadTask != null)
            return loadTask;

        loadTask = new InteractionSIBTask();
        SIBFactory.getInstance().getAccessPoint(_accessPointName).queryRDF(_id, SIB_ANY, SIB_ANY, "uri", "uri").addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {
                triples.clear();
                triples.addAll(response.query_results);
                loadTask.setSuccess(response);
                BaseRDF.this.loadTask = null;
                isDownloaded = true;
                notifyListeners(null);
            }

            @Override
            public void onError(Exception ex) {
                loadTask.setError(ex);
                BaseRDF.this.loadTask = null;
                notifyListeners(ex);
            }
        });

        return loadTask;
    }

    public ArrayList<String> getInTriples(String searchURI) {
        ArrayList<String> ret = new ArrayList<>();
        if (this.triples.size() == 0) {
            download();
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
        return isDownloaded;
    }

    public static class InteractionSIBTask {

        protected Exception ex = null;

        protected SIBResponse response = null;

        protected List<TaskListener> listeners = new ArrayList<>();

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
