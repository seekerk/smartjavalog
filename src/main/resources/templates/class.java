package $PACKAGE_NAME$;

import org.fruct.oss.smartjavalog.base.BaseRDF;
import org.fruct.oss.smartjavalog.base.SIBFactory;
import org.fruct.oss.smartjavalog.base.SubscribeQuery;
import org.fruct.oss.smartjavalog.base.TaskListener;

import java.util.*;

import sofia_kp.SIBResponse;

import static org.fruct.oss.smartjavalog.base.KPIproxy.NOTIFICATION_INDIVIDE;
import static org.fruct.oss.smartjavalog.base.KPIproxy.NOTIFICATION_UPDATE_TIME;
import static org.fruct.oss.smartjavalog.base.KPIproxy.RDF_TYPE_URI;

/**
$CLASS_DESCRIPTION$
 */
public class $CLASS_NAME$ extends BaseRDF {

    private static final String CLASS_URI = "$CLASS_URI$";
    private static final String NOTIFICATION_URI = "http://oss.fruct.org/smartjavalog#$PACKAGE_NAME$/$CLASS_NAME$/Notification";

    private static boolean classLoader = classLoader();

    private static boolean classLoader() {
        BaseRDF.registerInstance(CLASS_URI, NOTIFICATION_URI, new BaseRDFChildInstance() {
            @Override
            public BaseRDF getInstance(String objectId) {
                return $CLASS_NAME$.getInstance(objectId, SIBFactory.getInstance().getDefaultAccessPointName());
            }

            @Override
            public BaseRDF getInstance() {
                return $CLASS_NAME$.getInstance();
            }
        });

        return true;
    }

    public static $CLASS_NAME$ getInstance(String objectId, String accessPointName) {
        $CLASS_NAME$ ret = ($CLASS_NAME$) SubscribeQuery.getInstance().getKnownObject(objectId);

        if (ret == null) {
            ret = new $CLASS_NAME$(objectId, accessPointName);
            SubscribeQuery.getInstance().registerObject(ret);
        }

        return ret;
    }

    public static $CLASS_NAME$ getInstance() {
        $CLASS_NAME$ ret = new $CLASS_NAME$();
        SubscribeQuery.getInstance().registerObject(ret);

        return ret;
    }

    /**
     * Creates new class entity
     * @param objectID class entity id
     * @param accessPointName the name of used access point
     */
    protected $CLASS_NAME$(String objectID, String accessPointName) {
        super(objectID, accessPointName);
    }

    /**
     * Creates new class entity
     * @param accessPointName  the name of used access point
     */
    protected $CLASS_NAME$(String accessPointName) {
        super(generateID("$CLASS_NAME$"), accessPointName);
        isNew = true;
    }

    protected $CLASS_NAME$() {
        super(generateID("$CLASS_NAME$"), SIBFactory.getInstance().getDefaultAccessPointName());
        isNew = true;
    }

    /**
     * Return class URI from ontology model
     * @return String with class URI
     */
    public static String getClassUri() { return CLASS_URI; }

    $CLASS_PROPERTIES$

    public InteractionSIBTask update() {
        final InteractionSIBTask task = new InteractionSIBTask();
        // update triple store
        download(false).addListener(new TaskListener() {
            @Override
            public void onSuccess(SIBResponse response) {

                // триплеты для добавления
                ArrayList<ArrayList<String>> newTriples = new ArrayList<>();

                // триплеты для удаления
                ArrayList<ArrayList<String>> removeTriples = new ArrayList<>();

                String notificationId = null;

                // 1. проверяем, новый ли индивид. Если новый, то у него нет триплетов с сиба
                if (isNew) {
                    // Добавляем триплет для класса индивида
                    newTriples.add(createTriple(getID(), RDF_TYPE_URI, getClassUri()));
                }

                $PROPERTIES_UPDATE$

                if (!isNew && (newTriples.size() > 0 || removeTriples.size() > 0)) {
                    notificationId = generateID("notification");
                    newTriples.add(BaseRDF.createTriple(notificationId, RDF_TYPE_URI, NOTIFICATION_URI));
                    newTriples.add(BaseRDF.createTriple(notificationId, NOTIFICATION_INDIVIDE, getID()));
                    newTriples.add(BaseRDF.createTriple(notificationId, NOTIFICATION_UPDATE_TIME, System.currentTimeMillis() / 1000L));
                    SubscribeQuery.getInstance().registerNotification(notificationId);
                }

                String finalNotificationId = notificationId;

                SIBFactory.getInstance().getAccessPoint(_accessPointName).insert(newTriples).addListener(new TaskListener() {
                    @Override
                    public void onSuccess(SIBResponse response) {
                        if (!response.isConfirmed()) {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        if (removeTriples.size() > 0) {
                            SIBFactory.getInstance().getAccessPoint(_accessPointName).remove(removeTriples).addListener(new TaskListener() {
                                @Override
                                public void onSuccess(SIBResponse response) {
                                    if (!response.isConfirmed()) {
                                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                    }
                                    task.setSuccess(response);
                                    isNew = false;
                                    if (finalNotificationId != null) {
                                        new Timer().schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                SIBFactory.getInstance().getAccessPoint(_accessPointName).removeInstance(finalNotificationId);
                                                SubscribeQuery.getInstance().removeNotificationRegistration(finalNotificationId);
                                            }
                                        }, NOTIFICATION_TIMEOUT);
                                    }
                                }

                                @Override
                                public void onError(Exception ex) {
                                    task.setError(ex);
                                }
                            });
                        } else {
                            task.setSuccess(response);
                            isNew = false;
                            if (finalNotificationId != null) {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        SIBFactory.getInstance().getAccessPoint(_accessPointName).removeInstance(finalNotificationId);
                                        SubscribeQuery.getInstance().removeNotificationRegistration(finalNotificationId);
                                    }
                                }, NOTIFICATION_TIMEOUT);
                            }
                        }
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
    public void removeProperty(String URI) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}