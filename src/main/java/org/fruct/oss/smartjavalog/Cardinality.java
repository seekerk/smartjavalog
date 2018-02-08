package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.*;

import java.util.logging.Logger;

public class Cardinality {
    private static Logger log = Logger.getLogger(Cardinality.class.getName());

    private int minCardinality = -1;

    private int exactCardinality = -1;

    private int maxCardinality = -1;

    public void parse(OWLClassExpression range) {
        range.accept(new OWLClassExpressionVisitor() {
            @Override
            public void visit(OWLObjectMinCardinality ce) {
                minCardinality = ce.getCardinality();
            }

            @Override
            public void visit(OWLDataMaxCardinality ce) {
                maxCardinality = ce.getCardinality();
            }

            @Override
            public void visit(OWLDataMinCardinality ce) {
                minCardinality = ce.getCardinality();
            }

            @Override
            public void visit(OWLDataExactCardinality ce) {
                exactCardinality = ce.getCardinality();
            }

            @Override
            public void visit(OWLObjectMaxCardinality ce) {
                maxCardinality = ce.getCardinality();
            }

            @Override
            public void visit(OWLObjectExactCardinality ce) {
                exactCardinality = ce.getCardinality();
            }
        });
    }

    public int getMinCardinality() {
        return minCardinality == -1 ? exactCardinality : minCardinality;
    }

    public int getMaxCardinality() {
        return maxCardinality == -1 ? exactCardinality : maxCardinality;
    }

    public int getExactCardinality() {
        if (exactCardinality == -1 && minCardinality == maxCardinality)
            return minCardinality;
        return exactCardinality;
    }

    @Override
    public String toString() {
        return "Cardinality {min=" + minCardinality + "; max=" + maxCardinality + "; exact=" + exactCardinality + "}";
    }
}
