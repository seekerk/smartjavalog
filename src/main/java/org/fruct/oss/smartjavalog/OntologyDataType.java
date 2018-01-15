package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;

import java.util.ArrayList;
import java.util.List;

class OntologyDataType {

    private IRI name;

    OWLDatatype type = null;

    OWLDataOneOf oneOfType = null;

    OntologyDataType(IRI name) {
        this.name = name;
    }

    void setSimpleType(OWLDatatype type) {
        if (this.type != null)
            throw new IllegalStateException("Multiple call of setSimpleType()");

        this.type = type;
    }

    public void setOneOfType(OWLDataOneOf oneOfType) {
        if (this.oneOfType != null)
            throw new IllegalStateException("Multiple call of setOneOfType()");
        this.oneOfType = oneOfType;
    }
}
