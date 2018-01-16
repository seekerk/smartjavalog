package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OntologyProperty {

    private Logger log = Logger.getLogger(OntologyProperty.class.getName());

    private IRI name;

    private List<OWL2Datatype> simpleDataTypes = new ArrayList<>();

    private List<IRI> complexDataTypes = new ArrayList<>();

    OntologyProperty(IRI propertyIri) {
        name = propertyIri;
    }

    public void addDataType(OWL2Datatype type) {
        simpleDataTypes.add(type);
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
    public List<OWL2Datatype> getOWLDataTypes() {
        List<OWL2Datatype> ret = new ArrayList<>(simpleDataTypes);
        //return simpleDataTypes;
        log.info("complexTypes: " + complexDataTypes);
        for (IRI type: complexDataTypes) {
            OntologyComplexDataType dataType = OntologyFactory.getInstance().getDataType(type);
            ret.addAll(dataType.getOWLDataTypes());
        }

        // поиск среди известных типов
        OntologyComplexDataType dataType = OntologyFactory.getInstance().getDataType(name);
        if (dataType != null)
            ret.addAll(dataType.getOWLDataTypes());

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
