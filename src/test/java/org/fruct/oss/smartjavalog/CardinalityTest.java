package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.Test;

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
}