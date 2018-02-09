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
    private Map<String, OntologyObject> objects = new HashMap<>();

    /**
     * список свойств
     */
    private Map<String, OntologyProperty> properties = new HashMap<>();

    /**
     * список сложных типов данных
     */
    private Map<String, OntologyComplexDataType> types = new HashMap<>();

    private Map<String, String> comments = new HashMap<>();


    public static OntologyFactory getInstance() {
        if (factory == null) {
            factory = new OntologyFactory();
        }

        return factory;
    }

    private OntologyFactory() {
    }

    public void addClassWithProperty(IRI classIri, IRI propertyIri) {
        if (!objects.containsKey(classIri.getIRIString())) {
            objects.put(classIri.getIRIString(), new OntologyObject(classIri));
            System.err.println("Add new class " + classIri.getFragment());
        }

        objects.get(classIri.getIRIString()).addProperty(propertyIri);

        if (!properties.containsKey(propertyIri.getIRIString())) {
            properties.put(propertyIri.getIRIString(), new OntologyProperty(propertyIri));
        }
    }

    public void addDataType(IRI dataProperties) {
        if (!types.containsKey(dataProperties.getIRIString()))
            types.put(dataProperties.getIRIString(), new OntologyComplexDataType(dataProperties));
    }

    public void addDataType(IRI dataType, OWLDatatype simpleType) {
        addDataType(dataType);
        types.get(dataType.getIRIString()).setType(simpleType);
        System.err.println("Set simple data type \"" + simpleType + "\" for value \"" +dataType.getFragment() + "\"");
    }

    public void addDataType(IRI dataType, OWLDataOneOf oneOfType) {
        addDataType(dataType);
        types.get(dataType.getIRIString()).setType(oneOfType);
        System.err.println("value2: " + oneOfType.values().collect(Collectors.toList()).get(1).getLiteral() +
                "; " + oneOfType.values().collect(Collectors.toList()).get(1).getDatatype());

    }

    public void addClass(IRI iri) {
        if (!objects.containsKey(iri.getIRIString())) {
            objects.put(iri.getIRIString(), new OntologyObject(iri));
            System.err.println("Add new class " + iri.getFragment());
        }
    }

    /**
     * Добавление простого типа значения для свойства
     * @param owlDataProperty свойство
     * @param type простой тип
     */
    public void addPropertyType(OWLObjectProperty owlDataProperty, OWL2Datatype type, Cardinality crd) {
        IRI iri = owlDataProperty.getIRI();
        if (!properties.containsKey(iri.getIRIString())) {
            properties.put(iri.getIRIString(), new OntologyProperty(iri));
        }
        properties.get(iri.getIRIString()).addDataType(type, crd);
    }

    /**
     * Добавление сложного типа значения или класса для свойства
     * @param owlDataProperty свойство
     * @param complexType URL типа
     */
    public void addPropertyType(OWLObjectProperty owlDataProperty, IRI complexType, Cardinality crd) {
        IRI iri = owlDataProperty.getIRI();

        if (!properties.containsKey(iri.getIRIString())) {
            properties.put(iri.getIRIString(), new OntologyProperty(iri));
        }

        properties.get(iri.getIRIString()).addDataType(complexType, crd);
    }

    public void addObjectDataProperty(IRI iri) {
        if (!properties.containsKey(iri.getIRIString())) {
            properties.put(iri.getIRIString(), new OntologyProperty(iri));
        }
    }

    public Map<String,OntologyObject> getObjects() {
        return objects;
    }

    Map<String, OntologyComplexDataType> getDataTypes() {
        return types;
    }

    /**
     * Проверка, является ли свойство класса свойством данных или ссылкой на другой класс
     * @param property URI свойства
     * @return true если это свойство данных, false если это свойство класса
     */
    boolean isSimpleDataProperty(IRI property) {
        if (!properties.containsKey(property.getIRIString()))
            throw new IllegalStateException("Unknown property \"" + property.getIRIString() + "\"");

        return properties.get(property.getIRIString()).isDataProperty();
    }

    /**
     * Проверка, является ли свойство класса свойством данных или ссылкой на другой класс
     * @param property URI свойства
     * @return true если это свойство данных, false если это свойство класса
     */
    boolean isComplexDataProperty(IRI property) {
        if (!properties.containsKey(property.getIRIString()))
            throw new IllegalStateException("Unknown property \"" + property.getIRIString() + "\"");

        return properties.get(property.getIRIString()).isComplexDataProperty();
    }

    /**
     * Получение свойства класса по URI
     * @param property URI свойства
     * @return объект свойства класса или null если такого нет
     */
    public OntologyProperty getProperty(IRI property) {
        return properties.get(property.getIRIString());
    }

    /**
     * Добавление комментария к элементу (классу, свойству и т.д.)
     * @param iri URL элемента
     * @param literal Текст комментария
     */
    public void addComment(IRI iri, String literal) {
        if (comments.containsKey(iri.getIRIString()))
            throw new IllegalStateException("Not support multiple comments");
        comments.put(iri.getIRIString(), literal);
    }

    /**
     * получение комментария к объекту
     * @param iri URL объекта
     * @return текст комментария или null
     */
    public String getComment(IRI iri) {
        return comments.get(iri.getIRIString());
    }

    public OntologyObject getObject(IRI type) {
        System.err.println("Found object " + objects.get(type.getIRIString()) + " for uri=" + type.getFragment());
        return objects.get(type.getIRIString());
    }

    public OntologyComplexDataType getDataType(IRI type) {
        return types.get(type.getIRIString());
    }
}
