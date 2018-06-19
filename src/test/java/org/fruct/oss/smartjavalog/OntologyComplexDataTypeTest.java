package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OntologyComplexDataTypeTest {

    private static IRI IRI_TYPE = IRI.create("complex.type");
    private OntologyComplexDataType type;
    private OWLOntology ontology = null;
    private static OWLOntologyManager manager;

    @BeforeAll
    static void init() {
        manager = OWLManager.createOWLOntologyManager();

    }

    @BeforeEach
    void setUp() {
        type = new OntologyComplexDataType(IRI_TYPE);
    }

    @Test
    void simpleTest() {
        assertEquals(IRI_TYPE, type.getIRI());
        assertEquals(IRI_TYPE.getFragment(), type.getName());
        assertNull(type.getOWLDataTypes());
    }
}