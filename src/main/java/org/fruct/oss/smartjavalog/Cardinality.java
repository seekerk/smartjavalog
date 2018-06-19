package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.*;

public class Cardinality {
    private int minCardinality = -1;

    private int exactCardinality = -1;

    private int maxCardinality = -1;

    void parse(OWLClassExpression range) {
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

    int getMinCardinality() {
        return minCardinality == -1 ? exactCardinality : minCardinality;
    }

    int getMaxCardinality() {
        return maxCardinality == -1 ? exactCardinality : maxCardinality;
    }

    int getExactCardinality() {
        if (exactCardinality == -1 && minCardinality == maxCardinality)
            return minCardinality;
        return exactCardinality;
    }

    @Override
    public String toString() {
        return "Cardinality {min=" + minCardinality + "; max=" + maxCardinality + "; exact=" + exactCardinality + "}";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        Cardinality other = (Cardinality)obj;
        return minCardinality == other.minCardinality &&
                exactCardinality == other.exactCardinality &&
                maxCardinality == other.maxCardinality;
    }
}
