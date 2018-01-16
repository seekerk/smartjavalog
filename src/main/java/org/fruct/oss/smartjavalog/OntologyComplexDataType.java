package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class OntologyComplexDataType {

    private IRI name;

    OWLDatatype type = null;

    OWLDataOneOf oneOfType = null;

    OntologyComplexDataType(IRI name) {
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

    public Collection<? extends OWL2Datatype> getOWLDataTypes() {
        List<OWL2Datatype> ret = new ArrayList<>();
        if (type != null) {
            ret.add(type.getBuiltInDatatype());
        }
        System.err.println(ret);
        return ret;
    }
}
