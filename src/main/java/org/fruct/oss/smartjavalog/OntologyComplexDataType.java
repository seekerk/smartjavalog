package org.fruct.oss.smartjavalog;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class OntologyComplexDataType {
    private static final Logger log = Logger.getLogger(OntologyComplexDataType.class.getName());

    private IRI name;

    List<DataTypeWithValue> types = null;

    /** если это один из многих */
    private boolean oneOfType = false;

    OntologyComplexDataType(IRI name) {
        this.name = name;
    }

    public void setType(OWLDatatype type) {
        if (this.types != null)
            throw new IllegalStateException("Multiple call of setType()");

        this.types = new ArrayList<>(1);
        this.types.add(new DataTypeWithValue(type.getBuiltInDatatype(), null, null));
    }

    public void setType(OWLDataOneOf oneOfType) {
        if (this.types != null)
            throw new IllegalStateException("Multiple call of setType()");

        List<OWLLiteral> items = oneOfType.values().collect(Collectors.toList());
        this.types = new ArrayList<>(items.size());
        for (OWLLiteral item : items) {
            log.info("Item = " + item.getDatatype() + ": " + item.getLiteral() + " (" + item + ")");

            boolean isKnownType = false;
            for (DataTypeWithValue oldType : this.types) {
                if (oldType.type.equals(item.getDatatype().getBuiltInDatatype())) {
                    oldType.values.add(item.getLiteral());
                    isKnownType = true;
                    break;
                }
            }

            if (!isKnownType)
                this.types.add(new DataTypeWithValue(item.getDatatype().getBuiltInDatatype(), item.getLiteral(), null));
        }

        this.oneOfType = true;
    }

    public boolean isOnOfType() {
        return oneOfType;
    }

    public List<DataTypeWithValue> getOWLDataTypes() {
        return this.types;
    }

    public List<DataTypeWithValue> getOWLDataTypes(List<DataTypeWithValue> otherTypes) {
        if (otherTypes == null)
            return this.types == null ? new ArrayList<>() : this.types;

        if (otherTypes.isEmpty()) {
            if (this.types != null)
                otherTypes.addAll(this.types);
        } else {
            for (DataTypeWithValue oldType : this.types) {
                boolean isKnownType = false;
                for (DataTypeWithValue newType : otherTypes) {
                    if (oldType.type.equals(newType.type)) {
                        oldType.values.addAll(newType.values);
                        isKnownType = true;
                        break;
                    }
                }
                if (!isKnownType)
                    otherTypes.add(oldType);
            }
        }

        return otherTypes;
    }

    public String getName() {
        return name.getFragment();
    }

    public IRI getIRI() {
        return name;
    }

    public static class DataTypeWithValue {
        private OWL2Datatype type = null;
        private List<String> values = null;
        private Cardinality cardinality = null;

        DataTypeWithValue(OWL2Datatype type, String value, Cardinality cardinality) {
            this.type = type;
            values = new ArrayList<>(1);
            this.values.add(value);
            if (cardinality == null)
                this.cardinality = new Cardinality();
            else
                this.cardinality = cardinality;
        }

        public OWL2Datatype getType() {
            return type;
        }

        public List<String> getValue() {
            return values;
        }

        public Cardinality getCardinality() {
            return cardinality;
        }
    }
}
