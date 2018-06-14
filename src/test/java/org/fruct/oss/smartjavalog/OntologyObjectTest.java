package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;

import static org.junit.jupiter.api.Assertions.*;

class OntologyObjectTest {

    @Test
    void getName() {
        OntologyObject obj = new OntologyObject((IRI.create("test.iri")));
        assertEquals(obj.getName(), "test.iri");
    }

    @Test
    void getManyProperties() {
        int propCount = 10;
        OntologyObject obj = new OntologyObject(IRI.create("test.iri"));
        for (int i = 0; i < propCount; i++) {
            obj.addProperty(IRI.create("test.iri" + i));
        }

        assertEquals(obj.getProperties().size(), propCount);

        for (int i = 0; i < propCount; i++) {
            assertTrue(obj.getProperties().containsKey(IRI.create("test.iri" + i).getIRIString()));
            assertTrue(obj.getProperties().containsValue(IRI.create("test.iri" + i)));
        }
    }

    @Test
    void getIRI() {
        OntologyObject obj = new OntologyObject(IRI.create("test.iri"));
        assertEquals(obj.getIRI(), IRI.create("test.iri"));
    }
}