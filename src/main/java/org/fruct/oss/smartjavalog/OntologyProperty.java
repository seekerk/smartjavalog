package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OntologyProperty {

    private Logger log = Logger.getLogger(OntologyProperty.class.getName());

    private IRI name;

    private List<OntologyComplexDataType.DataTypeWithValue> simpleDataTypes = new ArrayList<>();

    private Map<IRI, Cardinality> complexDataTypes = new HashMap<>();

    OntologyProperty(IRI propertyIri) {
        name = propertyIri;
    }

    public void addDataType(OWL2Datatype type, Cardinality crd) {
        simpleDataTypes.add(new OntologyComplexDataType.DataTypeWithValue(type, null, crd));
        System.err.println("Add type \"" + type + "\" for data property \"" + name.getFragment() + "\"");
    }

    public void addDataType(IRI iri, Cardinality crd) {
        complexDataTypes.put(iri, crd);
        System.err.println("Add complex type \"" + iri.getFragment() + "\" for data property \"" + name.getFragment() + "\"");
    }

    /**
     * Список простых типов данных собранных из всех вариантов
     * @return список типов
     */
    public List<OntologyComplexDataType.DataTypeWithValue> getOWLDataTypes() {
        List<OntologyComplexDataType.DataTypeWithValue> ret = new ArrayList<>(simpleDataTypes);
        //return simpleDataTypes;
        log.info("complexTypes: " + complexDataTypes);
        for (IRI type: complexDataTypes.keySet()) {
            OntologyComplexDataType dataType = OntologyFactory.getInstance().getDataType(type);
            ret = dataType.getOWLDataTypes();
        }

        // поиск среди известных типов
        OntologyComplexDataType dataType = OntologyFactory.getInstance().getDataType(name);
        if (dataType != null) {
            ret = dataType.getOWLDataTypes(ret);
        }

        return ret;
    }


    public boolean isObjectProperty() {
        log.info("Known types" + complexDataTypes);
        for(IRI type : complexDataTypes.keySet()) {
            if (OntologyFactory.getInstance().getObject(type) != null)
                return true;
        }
        return false;
    }

    boolean isComplexDataProperty() {
        for(IRI type: complexDataTypes.keySet()) {
            if (OntologyFactory.getInstance().getDataType(type) != null)
                return true;
        }

        return false;
    }

    /**
     * Получение имени класса-значения
     * @return название класса-значения
     */
    public String getClassValue() {
        for (IRI type: complexDataTypes.keySet()) {
            if (OntologyFactory.getInstance().getObject(type) != null)
                return type.getFragment();
        }

        return null;
    }

    /**
     * Получение имени класса-значения
     * @return название класса-значения
     */
    public String getComplexDataValue() {
        for (IRI type: complexDataTypes.keySet()) {
            if (OntologyFactory.getInstance().getDataType(type) != null)
                return type.getFragment();
        }

        return null;
    }

    Cardinality getClassCardinality() {
        for (IRI type: complexDataTypes.keySet()) {
            if (OntologyFactory.getInstance().getObject(type) != null)
                return complexDataTypes.get(type);
        }

        return new Cardinality();
    }

    Cardinality getComplexDataCardinality() {
        for (IRI type: complexDataTypes.keySet()) {
            if (OntologyFactory.getInstance().getDataType(type) != null)
                return complexDataTypes.get(type);
        }

        return new Cardinality();
    }

    public boolean isDataProperty() {
        if (isObjectProperty())
            return false;
        if (isComplexDataProperty()) {
            return false;
        }
        return true;
    }
}
