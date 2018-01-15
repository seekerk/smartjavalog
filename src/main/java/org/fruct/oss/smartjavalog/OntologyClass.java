package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OntologyClass {

    private IRI name;

    private Map<String, IRI> properties;

    OntologyClass(IRI classIri) {
        name = classIri;
        properties = new HashMap<>();
    }

    public void addProperty(IRI propertyIri) {
        if (!properties.containsKey(propertyIri.getIRIString())) {
            properties.put(propertyIri.getIRIString(), propertyIri);
            System.err.println("Add property \"" + propertyIri.getFragment() + "\" to class \"" + name.getFragment() + "\"");
        }
    }
}
