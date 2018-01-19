package $PACKAGE_NAME$;

import $PACKAGE_NAME$.base.BaseRDF;
import $PACKAGE_NAME$.base.SIBFactory;
import $PACKAGE_NAME$.base.TaskListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import sofia_kp.KPICore;
import sofia_kp.SIBResponse;

/**
$CLASS_DESCRIPTION$
 */
public class $CLASS_NAME$ extends BaseRDF {

    private static final String CLASS_URI = "$CLASS_URI$";

    /**
     * Creates new class entity
     * @param objectID class entity id
     * @param accessPointName the name of used access point
     */
    public $CLASS_NAME$(String objectID, String accessPointName) {
        super(objectID, accessPointName);
    }

    /**
     * Creates new class entity
     * @param accessPointName  the name of used access point
     */
    public $CLASS_NAME$(String accessPointName) {
        super(generateID("$CLASS_NAME$"), accessPointName);
    }

    public $CLASS_NAME$() {
        super(generateID("$CLASS_NAME$"), SIBFactory.getInstance().getDefaultAccessPointName());
    }

    $CLASS_PROPERTIES$

    public InteractionSIBTask update() {
        final InteractionSIBTask task = new InteractionSIBTask();
        // update triple store
        load().addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {

                // триплеты для добавления
                ArrayList<ArrayList<String>> newTriples = new ArrayList();

                // триплеты для удаления
                ArrayList<ArrayList<String>> removeTriples = new ArrayList();

                // 1. проверяем, новый ли индивид. Если новый, то у него нет триплетов с сиба
                if (getInTriples(RDF_TYPE_URI).isEmpty()) {
                    // Добавляем триплет для класса индивида
                    newTriples.add(createTriple(getID(), RDF_TYPE_URI, getURI()));
                }

                $PROPERTIES_UPDATE$

                SIBFactory.getInstance().getAccessPoint(_accessPointName).insert(newTriples).addListener(new TaskListener() {
                    @Override
                    public void onSuccess(SIBResponse response) {
                        if (!response.isConfirmed()) {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        SIBFactory.getInstance().getAccessPoint(_accessPointName).remove(removeTriples).addListener(new TaskListener() {
                            @Override
                            public void onSuccess(SIBResponse response) {
                                if (!response.isConfirmed()) {
                                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                }
                                task.setSuccess(response);
                            }

                            @Override
                            public void onError(Exception ex) {
                                task.setError(ex);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception ex) {
                        task.setError(ex);
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                task.setError(ex);
            }
        });

        return task;
    }

    @Override
    public String getURI() {
        return CLASS_URI;
    }

    @Override
    public void removeProperty(String URI) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}