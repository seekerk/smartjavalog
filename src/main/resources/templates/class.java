package $PACKAGE_NAME$;

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

    public void update() {
        // update triple store
        load();

        // триплеты для добавления
        ArrayList<ArrayList<String>> newTriples = new ArrayList();
        
        // триплеты для удаления
        ArrayList<ArrayList<String>> removeTriples = new ArrayList();
        
        // 1. проверяем, новый ли индивид. Если новый, то у него нет триплетов с сиба
        if (getStringInTriples(RDF_TYPE_URI).isEmpty()) {
            // Добавляем триплет для класса индивида
            newTriples.add(createTriple(getID(), RDF_TYPE_URI, getURI()));
        }

        $PROPERTIES_UPDATE$

        SIBResponse ret;
        ret = SIBFactory.getInstance().getAccessPoint(_accessPointName).insert(newTriples);
        if (!ret.isConfirmed()) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        ret = SIBFactory.getInstance().getAccessPoint(_accessPointName).remove(removeTriples);
        if (!ret.isConfirmed()) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
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