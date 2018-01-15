package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OntologyFactory {

    private static OntologyFactory factory = null;

    /**
     * список классов
     */
    Map<String, OntologyClass> classes;

    /**
     * список свойств
     */
    Map<String, OntologyDataProperty> properties;

    Map<String, OntologyDataType> types;


    public static OntologyFactory getInstance() {
        if (factory == null) {
            factory = new OntologyFactory();
        }

        return factory;
    }

    private OntologyFactory() {
        classes = new HashMap<>();
        properties = new HashMap<>();
        types = new HashMap<>();
    }

    public void addClassWithProperty(IRI classIri, IRI propertyIri) {
        if (!classes.containsKey(classIri.getIRIString())) {
            classes.put(classIri.getIRIString(), new OntologyClass(classIri));
            System.err.println("Add new class " + classIri.getFragment());
        }

        classes.get(classIri.getIRIString()).addProperty(propertyIri);

        if (!properties.containsKey(propertyIri.getIRIString())) {
            properties.put(propertyIri.getIRIString(), new OntologyDataProperty(propertyIri));
        }
    }

    public void addDataType(IRI dataProperties) {
        if (!types.containsKey(dataProperties.getIRIString()))
            types.put(dataProperties.getIRIString(), new OntologyDataType(dataProperties));
    }

    public void addDataType(IRI dataType, OWLDatatype simpleType) {
        addDataType(dataType);
        types.get(dataType.getIRIString()).setSimpleType(simpleType);
        System.err.println("Set type \"" + simpleType + "\" for data type \"" +dataType.getFragment() + "\"");
    }

    public void addDataType(IRI dataType, OWLDataOneOf oneOfType) {
        addDataType(dataType);
        types.get(dataType.getIRIString()).setOneOfType(oneOfType);
        System.err.println("value2: " + oneOfType.values().collect(Collectors.toList()).get(1).getLiteral() +
                "; " + oneOfType.values().collect(Collectors.toList()).get(1).getDatatype());

    }

    public void addClass(IRI iri) {
        if (!classes.containsKey(iri.getIRIString())) {
            classes.put(iri.getIRIString(), new OntologyClass(iri));
            System.err.println("Add new class " + iri.getFragment());
        }
    }

    public void addDataPropertyType(OWLObjectProperty owlDataProperty, OWL2Datatype type) {
        IRI iri = owlDataProperty.getIRI();
        if (!properties.containsKey(iri.getIRIString())) {
            properties.put(iri.getIRIString(), new OntologyDataProperty(iri));
        }
        properties.get(iri.getIRIString()).addDataType(type);
    }

    public void addDataPropertyType(OWLObjectProperty owlDataProperty, IRI complexType) {
        IRI iri = owlDataProperty.getIRI();

        if (!properties.containsKey(iri.getIRIString())) {
            properties.put(iri.getIRIString(), new OntologyDataProperty(iri));
        }

        properties.get(iri.getIRIString()).addDataType(complexType);
    }

    public void addObjectDataProperty(IRI iri) {
        if (!properties.containsKey(iri.getIRIString())) {
            properties.put(iri.getIRIString(), new OntologyDataProperty(iri));
        }
    }

    public Map<String,OntologyClass> getClasses() {
        return classes;
    }
}
