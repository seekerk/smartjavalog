package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;

public class OntologyDataProperty {

    private IRI name;

    private List<OWL2Datatype> simpleTypes;

    private List<IRI> complexTypes;

    OntologyDataProperty(IRI propertyIri) {
        name = propertyIri;
        simpleTypes = new ArrayList<>();
        complexTypes = new ArrayList<>();
    }

    public void addDataType(OWL2Datatype type) {
        simpleTypes.add(type);
        System.err.println("Add type \"" + type + "\" for data property \"" + name.getFragment() + "\"");
    }

    public void addDataType(IRI iri) {
        complexTypes.add(iri);
        System.err.println("Add complex type \"" + iri.getFragment() + "\" for data property \"" + name.getFragment() + "\"");
    }
}
