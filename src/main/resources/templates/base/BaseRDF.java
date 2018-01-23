package org.fruct.oss.smartjavalog.base;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import sofia_kp.KPICore;
import sofia_kp.SIBResponse;

public abstract class BaseRDF {

    protected String _accessPointName;
    private final String _id;
    // датчик случайных чисел для генератора
    private static Random rand = null;

    // загруженные триплеты
    private final ArrayList<ArrayList<String>> triples = new ArrayList<>();

    // разные гадости
    //TODO: заменить на rdg4j
    public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String SIB_ANY = "http://www.nokia.com/NRC/M3/sib#any";

    public BaseRDF(String objectID, String accessPointName) {
        _accessPointName = accessPointName;
        _id = objectID;
    }

    public String getID() {
        return _id;
    }

    public abstract InteractionSIBTask update();

    public InteractionSIBTask load() {
        InteractionSIBTask task = new InteractionSIBTask();
        SIBFactory.getInstance().getAccessPoint(_accessPointName).queryRDF(_id, SIB_ANY, SIB_ANY, "uri", "uri").addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {
                triples.clear();
                triples.addAll(response.query_results);
                task.setSuccess(response);
            }

            @Override
            public void onError(Exception ex) {
                task.setError(ex);
            }
        });

        return task;
    }

    public ArrayList<String> getInTriples(String searchURI) {
        ArrayList<String> ret = new ArrayList<>();
        if (this.triples.size() == 0) {
            load();
        }
        for (ArrayList<String> t : this.triples) {
            if (t.contains(searchURI)) {
                ret.add(t.get(2));
            }
        }
        return ret;
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
}
