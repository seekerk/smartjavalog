package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import static org.junit.jupiter.api.Assertions.*;

class OntologyPropertyTest {

    private OntologyProperty property;

    @BeforeEach
    void setUp() {
        property = new OntologyProperty(IRI.create("prop.iri"));
        OntologyFactory.getInstance().addClass(IRI.create("class.iri"));
        OntologyFactory.getInstance().addDataType(IRI.create("complex.type.iri"));
    }

    @Test
    void emptyObjectProperty() {
        assertFalse(property.isObjectProperty());
        assertTrue(property.isDataProperty());
        assertEquals(property.getOWLDataTypes().size(), 0);
    }

    @Test
    void isSimpleObjectProperty() {
        property.addDataType(IRI.create("class.iri"), new Cardinality());
        assertTrue(property.isObjectProperty());
        assertFalse(property.isDataProperty());
        assertEquals(property.getOWLDataTypes().size(), 0);
    }

    @Test
    void isObjectPropertyWithDataProperty() {
        property.addDataType(IRI.create("not.class.iri"), new Cardinality());
        property.addDataType(IRI.create("class.iri"), new Cardinality());
        assertTrue(property.isObjectProperty());
        assertFalse(property.isDataProperty());
        assertEquals(property.getOWLDataTypes().size(), 0);
    }

    @Test
    void isSimpleDataProperty() {
        property.addDataType(IRI.create("not.class.iri"), new Cardinality());
        assertFalse(property.isObjectProperty());
        assertTrue(property.isDataProperty());
        assertEquals(property.getOWLDataTypes().size(), 0);
    }

    @Test
    void dataTypeProperty() {
        property.addDataType(OWL2Datatype.XSD_BOOLEAN, new Cardinality());
        assertFalse(property.isObjectProperty());
        assertTrue(property.isDataProperty());
        assertEquals(property.getOWLDataTypes().size(), 1);
    }

    @Test
    void getClassValue() {
        property.addDataType(IRI.create("class.iri"), new Cardinality());
        assertEquals(property.getClassValue(), IRI.create("class.iri").getFragment());
        assertEquals(property.getClassCardinality(), new Cardinality());
        assertNull(property.getComplexDataValue());
        assertEquals(property.getComplexDataCardinality(), new Cardinality());
        assertEquals(property.getOWLDataTypes().size(), 0);
    }

    @Test
    void getClassValueForData() {
        property.addDataType(IRI.create("not.class.iri"), new Cardinality());
        assertNull(property.getClassValue());
        assertEquals(property.getClassCardinality(), new Cardinality());
        assertNull(property.getComplexDataValue());
        assertEquals(property.getComplexDataCardinality(), new Cardinality());
        assertEquals(property.getOWLDataTypes().size(), 0);
    }

    @Test
    void complexDataType() {
        property.addDataType(IRI.create("complex.type.iri"), new Cardinality());
        assertFalse(property.isDataProperty());
        assertFalse(property.isObjectProperty());
        assertTrue(property.isComplexDataProperty());
        assertEquals(property.getComplexDataValue(), IRI.create("complex.type.iri").getFragment());
        assertEquals(property.getComplexDataCardinality(), new Cardinality());
        assertNull(property.getOWLDataTypes());
    }
}