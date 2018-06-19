package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CardinalityTest {

    @Test
    void equals() {
        Cardinality cardinality = new Cardinality();
        assertNotEquals(cardinality, null);
        assertNotEquals(cardinality, new Object());
        assertEquals(cardinality, new Cardinality());
    }

    @Test
    void hashCodeTest() {
        Cardinality cardinality = new Cardinality();
        assertEquals(cardinality.hashCode(), new Cardinality().hashCode());
    }

    @Test
    void minCardinalityTest() {
        Cardinality cardinality = new Cardinality();
        assertEquals(-1, cardinality.getMinCardinality());

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("Cardinality/minCardinality3.owl")).getFile()));
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        List<OWLAxiom> axiomList = ontology.axioms().collect(Collectors.toList());
        for (OWLAxiom axiom : axiomList) {
            if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
                cardinality.parse(((OWLObjectPropertyRangeAxiom) axiom).getRange());
            }
        }
        assertEquals(3, cardinality.getMinCardinality());
        assertEquals(-1, cardinality.getMaxCardinality());
        assertEquals(-1, cardinality.getExactCardinality());
    }

    @Test
    void exactCardinalityTest() {
        Cardinality cardinality = new Cardinality();
        assertEquals(-1, cardinality.getExactCardinality());

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("Cardinality/exactCardinality4.owl")).getFile()));
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        List<OWLAxiom> axiomList = ontology.axioms().collect(Collectors.toList());
        for (OWLAxiom axiom : axiomList) {
            if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
                cardinality.parse(((OWLObjectPropertyRangeAxiom) axiom).getRange());
            }
        }
        assertEquals(4, cardinality.getMinCardinality());
        assertEquals(4, cardinality.getMaxCardinality());
        assertEquals(4, cardinality.getExactCardinality());
    }

    @Test
    void maxCardinalityTest() {
        Cardinality cardinality = new Cardinality();
        assertEquals(-1, cardinality.getMaxCardinality());

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("Cardinality/maxCardinality2.owl")).getFile()));
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        List<OWLAxiom> axiomList = ontology.axioms().collect(Collectors.toList());
        for (OWLAxiom axiom : axiomList) {
            if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
                cardinality.parse(((OWLObjectPropertyRangeAxiom) axiom).getRange());
            }
        }
        assertEquals(-1, cardinality.getMinCardinality());
        assertEquals(2, cardinality.getMaxCardinality());
        assertEquals(-1, cardinality.getExactCardinality());
    }

    @Test
    void objectMinMaxCardinalityTest() {
        Cardinality cardinality = new Cardinality();
        assertEquals(-1, cardinality.getMaxCardinality());
        assertEquals(-1, cardinality.getMinCardinality());

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("Cardinality/objectCardinalityMinMax.owl")).getFile()));
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        List<OWLAxiom> axiomList = ontology.axioms().collect(Collectors.toList());
        for (OWLAxiom axiom : axiomList) {
            if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
                cardinality.parse(((OWLObjectPropertyRangeAxiom) axiom).getRange());
            }
        }
        assertEquals(2, cardinality.getMinCardinality());
        assertEquals(5, cardinality.getMaxCardinality());
        assertEquals(-1, cardinality.getExactCardinality());
    }

    @Test
    void objectExactCardinalityTest() {
        Cardinality cardinality = new Cardinality();
        assertEquals(-1, cardinality.getExactCardinality());

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("Cardinality/objectCardinalityExact.owl")).getFile()));
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        List<OWLAxiom> axiomList = ontology.axioms().collect(Collectors.toList());
        for (OWLAxiom axiom : axiomList) {
            if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
                cardinality.parse(((OWLObjectPropertyRangeAxiom) axiom).getRange());
            }
        }
        assertEquals(8, cardinality.getExactCardinality());
    }
}