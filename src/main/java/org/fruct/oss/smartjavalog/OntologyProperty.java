package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OntologyProperty {

    private Logger log = Logger.getLogger(OntologyProperty.class.getName());

    private IRI name;

    private List<OntologyComplexDataType.DataTypeWithValue> simpleDataTypes = new ArrayList<>();

    private List<IRI> complexDataTypes = new ArrayList<>();

    OntologyProperty(IRI propertyIri) {
        name = propertyIri;
    }

    public void addDataType(OWL2Datatype type) {
        simpleDataTypes.add(new OntologyComplexDataType.DataTypeWithValue(type, null));
        System.err.println("Add type \"" + type + "\" for data property \"" + name.getFragment() + "\"");
    }

    public void addDataType(IRI iri) {
        complexDataTypes.add(iri);
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
        for (IRI type: complexDataTypes) {
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
        for(IRI type : complexDataTypes) {
            if (OntologyFactory.getInstance().getObject(type) != null)
                return true;
        }
        return false;
    }

    /**
     * Получение имени класса-значения
     * @return название класса-значения
     */
    public String getClassValue() {
        for (IRI type: complexDataTypes) {
            if (OntologyFactory.getInstance().getObject(type) != null)
                return type.getFragment();
        }

        return null;
    }
}
