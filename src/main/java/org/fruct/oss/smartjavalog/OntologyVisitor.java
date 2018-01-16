package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class OntologyVisitor implements OWLObjectVisitor {

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        axiom.getRange().accept(new OWLDataRangeVisitor() {
            @Override
            public void visit(OWLDataOneOf node) {
                OntologyFactory.getInstance().addDataType(axiom.dataPropertiesInSignature().collect(Collectors.toList()).get(0).getIRI(),
                        node);
            }

            @Override
            public void visit(OWLDatatype node) {
                OntologyFactory.getInstance().addDataType(axiom.dataPropertiesInSignature().collect(Collectors.toList()).get(0).getIRI(),
                        axiom.getRange().datatypesInSignature().collect(Collectors.toList()).get(0));
            }

            @Override
            public void visit(OWLDataUnionOf node) {
                throw new IllegalStateException("Not implemented");
            }

            @Override
            public void visit(OWLDataComplementOf node) {
                throw new IllegalStateException("Not implemented");
            }

            @Override
            public void visit(OWLDataIntersectionOf node) {
                throw new IllegalStateException("Not implemented");
            }

            @Override
            public void visit(OWLDatatypeRestriction node) {
                throw new IllegalStateException("Not implemented");
            }
        });
    }

    @Override
    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);
    }

    @Override
    public void visit(OWLObjectProperty property) {
        throw new IllegalStateException("Not implemented for: " + property);
    }

    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        switch (axiom.getEntity().getEntityType().getName()) {
            case "DataProperty": {
                OntologyFactory.getInstance().addDataType(axiom.getEntity().getIRI());
                break;
            }
            case "ObjectProperty": {
                OntologyFactory.getInstance().addObjectDataProperty(axiom.getEntity().getIRI());
                break;
            }
            case "Class": {
                OntologyFactory.getInstance().addClass(axiom.getEntity().getIRI());
                break;
            }
            default: {
                System.err.println(axiom);
                throw new IllegalStateException("Not implemented for type \"" +
                        axiom.getEntity().getEntityType().getName() + "\"");
            }
        }
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);
    }

    @Override
    public void visit(IRI iri) {
        throw new IllegalStateException("Not implemented for: " + iri);
    }

    @Override
    public void visit(OWLClass ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(SWRLRule node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLLiteral node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLDatatype node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLDataOneOf node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLObjectOneOf ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(SWRLVariable node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLAnnotation node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLDataHasValue ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(SWRLClassAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLDataUnionOf node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(OWLObjectHasSelf ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(OWLObjectUnionOf ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(OWLHasKeyAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);
    }

    @Override
    public void visit(OWLObjectHasValue ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(OWLOntology ontology) {
        throw new IllegalStateException("Not implemented for: " + ontology);
    }

    @Override
    public void visit(SWRLBuiltInAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);
    }

    @Override
    public void visit(SWRLDataRangeAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLDataAllValuesFrom ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLDataComplementOf node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLDataMaxCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLDataMinCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLDataProperty property) {
        throw new IllegalStateException("Not implemented for: " + property);

    }

    @Override
    public void visit(OWLDataSomeValuesFrom ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLFacetRestriction node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLObjectComplementOf ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(SWRLLiteralArgument node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLObjectAllValuesFrom ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(SWRLDataPropertyAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLDataExactCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLDataIntersectionOf node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLObjectIntersectionOf ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLObjectMaxCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLObjectMinCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLObjectSomeValuesFrom ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLDatatypeRestriction node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLObjectInverseOf property) {
        throw new IllegalStateException("Not implemented for: " + property);

    }

    @Override
    public void visit(SWRLIndividualArgument node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(SWRLObjectPropertyAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(SWRLSameIndividualAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLObjectExactCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);

    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLNamedIndividual individual) {
        throw new IllegalStateException("Not implemented for: " + individual);

    }

    @Override
    public void visit(OWLAnnotationProperty property) {
        throw new IllegalStateException("Not implemented for: " + property);

    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    /**
     * свойство класса
     * @param axiom
     */
    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        //System.err.println(axiom);
        IRI property = axiom.getProperty().dataPropertiesInSignature().collect(Collectors.toList()).get(0).getIRI(); // свойство класса
        IRI cls = axiom.classesInSignature().collect(Collectors.toList()).get(0).getIRI();
        OntologyFactory.getInstance().addClassWithProperty(cls, property);
    }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    /**
     * Комментарии к объектам
     * @param axiom
     */
    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        //System.err.println(axiom);

        //System.err.println(axiom.getSubject().asIRI().get().getFragment()); // аннотируемый объект

        //System.err.println(axiom.getValue());
        axiom.getValue().accept(new OWLAnnotationValueVisitor() {
            @Override
            public void visit(OWLLiteral node) {
                //System.err.println(node.getLiteral()); // текст сообщения
                OntologyFactory.getInstance().addComment(axiom.getSubject().asIRI().get(), node.getLiteral());
            }
        });
    }

    @Override
    public void visit(OWLAnonymousIndividual individual) {
        throw new IllegalStateException("Not implemented for: " + individual);

    }

    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        System.err.println(axiom);
        List<OWLDatatype> datatypes = axiom.datatypesInSignature().collect(Collectors.toList());
        if (datatypes.size() > 0) {
            if (datatypes.size() > 1) {
                System.err.println(axiom);
                throw new IllegalStateException("Not implemented for datatypes size = " + datatypes.size());
            }

            OntologyFactory.getInstance().addPropertyType(axiom.objectPropertiesInSignature().collect(Collectors.toList()).get(0),
                    datatypes.get(0).getBuiltInDatatype());
        }

        List<OWLDataProperty> properties = axiom.dataPropertiesInSignature().collect(Collectors.toList());
        if (properties.size() > 0) {
            if (properties.size() > 1) {
                System.err.println(axiom);
                throw new IllegalStateException("Not implemented for data properties size = " + datatypes.size());
            }
            OntologyFactory.getInstance().addPropertyType(axiom.objectPropertiesInSignature().collect(Collectors.toList()).get(0),
                    properties.get(0).getIRI());
        }
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(SWRLDifferentIndividualsAtom node) {
        throw new IllegalStateException("Not implemented for: " + node);

    }

    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        List<OWLClass> classes = axiom.classesInSignature().collect(Collectors.toList());
        if (classes.size() != 1) {
            System.err.println(axiom);
            throw new IllegalStateException("Not implemented for classes size=" + classes.size());
        }

        List<OWLObjectProperty> properties = axiom.objectPropertiesInSignature().collect(Collectors.toList());
        if (properties.size() != 1) {
            System.err.println(axiom);
            throw new IllegalStateException("Not implemented for properties size=" + properties.size());
        }

        OntologyFactory.getInstance().addClassWithProperty(classes.get(0).getIRI(), properties.get(0).getIRI());
    }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }

    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        throw new IllegalStateException("Not implemented for: " + axiom);

    }
}
