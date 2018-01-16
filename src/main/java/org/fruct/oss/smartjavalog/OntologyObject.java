package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;

import java.util.HashMap;
import java.util.Map;

public class OntologyObject {

    /**
     * имя класса
     */
    private IRI name;

    /**
     * Свойство класса (данные или другой класс)
     */
    private Map<String, IRI> properties;

    OntologyObject(IRI classIri) {
        name = classIri;
        properties = new HashMap<>();
    }

    /**
     * Добавление свойства класса (данные или другой класс)
     * @param propertyIri URI свойства
     */
    public void addProperty(IRI propertyIri) {
        if (!properties.containsKey(propertyIri.getIRIString())) {
            properties.put(propertyIri.getIRIString(), propertyIri);
            System.err.println("Add property \"" + propertyIri.getFragment() + "\" to class \"" + name.getFragment() + "\"");
        }
    }

    /**
     * Имя класса
     * @return имя класса
     */
    public String getName() {
        return name.getFragment();
    }

    /**
     * URL класса
     * @return url класса
     */
    public String getURI() {
        return name.getIRIString();
    }

    public Map<String, IRI> getProperties() {
        return properties;
    }

    public IRI getIRI() {
        return name;
    }
}
