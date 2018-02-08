package org.fruct.oss.smartjavalog;

import org.semanticweb.owlapi.model.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OntologyVisitor implements OWLObjectVisitor {

    private Logger log = Logger.getLogger(OntologyVisitor.class.getName());

    @Override
    @ParametersAreNonnullByDefault
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        log.info("Parse " + axiom.toString());

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
    @ParametersAreNonnullByDefault
    public void visit(OWLDeclarationAxiom axiom) {
        log.info("Parse " + axiom);

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

    /**
     * свойство класса (данные)
     * @param axiom контейнер свойства
     */
    @Override
    @ParametersAreNonnullByDefault
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        log.info("Parse " + axiom.toString());

        //System.err.println(axiom);
        IRI property = axiom.getProperty().dataPropertiesInSignature().collect(Collectors.toList()).get(0).getIRI(); // свойство класса
        IRI cls = axiom.classesInSignature().collect(Collectors.toList()).get(0).getIRI();
        OntologyFactory.getInstance().addClassWithProperty(cls, property);
    }

    /**
     * Комментарии к объектам
     * @param axiom контейнер комментария
     */
    @Override
    @ParametersAreNonnullByDefault
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        log.info("Parse " + axiom.toString());

        //System.err.println(axiom.getSubject().asIRI().get().getFragment()); // аннотируемый объект

        //System.err.println(axiom.getValue());
        axiom.getValue().accept(new OWLAnnotationValueVisitor() {
            @Override
            public void visit(OWLLiteral node) {
                //System.err.println(node.getLiteral()); // текст сообщения
                if (axiom.getSubject().asIRI().isPresent())
                    OntologyFactory.getInstance().addComment(axiom.getSubject().asIRI().get(), node.getLiteral());
                else
                    throw new IllegalStateException("Not implemented");
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        log.info("Parse " + axiom.toString());

        boolean notParsed = true;

        OWLObjectProperty property = axiom.objectPropertiesInSignature().collect(Collectors.toList()).get(0);

        Cardinality crd = new Cardinality();
        crd.parse(axiom.getRange());
        //search cardinality

        List<OWLDatatype> datatypes = axiom.datatypesInSignature().collect(Collectors.toList());
        if (datatypes.size() > 0) {
            notParsed = false;
            if (datatypes.size() > 1) {
                System.err.println(axiom);
                throw new IllegalStateException("Not implemented for datatypes size = " + datatypes.size());
            }

            OntologyFactory.getInstance().addPropertyType(property,
                    datatypes.get(0).getBuiltInDatatype(), crd);
            log.info("Add data property");
        }

        List<OWLDataProperty> properties = axiom.dataPropertiesInSignature().collect(Collectors.toList());
        if (properties.size() > 0) {
            notParsed = false;
            if (properties.size() > 1) {
                System.err.println(axiom);
                throw new IllegalStateException("Not implemented for data properties size = " + datatypes.size());
            }
            OntologyFactory.getInstance().addPropertyType(property,
                    properties.get(0).getIRI(), crd);
            log.info("Add complex data property: " + properties.get(0).getIRI());
        }

        List<OWLClass> classes = axiom.classesInSignature().collect(Collectors.toList());
        if (classes.size() > 0) {
            notParsed = false;
            if (classes.size() > 1) {
                System.err.println(axiom);
                throw  new IllegalStateException("Not implemented for multiple classes size=" + classes.size());
            }
            OntologyFactory.getInstance().addPropertyType(property,
                    classes.get(0).getIRI(), crd);
            log.info("Add class value property: " + classes.get(0).getIRI());
        }

        if (notParsed)
            throw new IllegalStateException("Not implemented");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        log.info("Parse " + axiom.toString());

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
    public void visit(OWLDatatypeDefinitionAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLObjectProperty property) { throw new IllegalStateException("Not implemented for: " + property); }

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
    public void visit(OWLObjectInverseOf property) { throw new IllegalStateException("Not implemented for: " + property); }

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
    public void visit(OWLClassAssertionAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLObjectExactCardinality ce) {
        throw new IllegalStateException("Not implemented for: " + ce);
    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLNamedIndividual individual) { throw new IllegalStateException("Not implemented for: " + individual); }

    @Override
    public void visit(OWLAnnotationProperty property) { throw new IllegalStateException("Not implemented for: " + property); }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLAnonymousIndividual individual) { throw new IllegalStateException("Not implemented for: " + individual); }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(SWRLDifferentIndividualsAtom node) { throw new IllegalStateException("Not implemented for: " + node); }

    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLAnnotationPropertyDomainAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }

    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) { throw new IllegalStateException("Not implemented for: " + axiom); }
}
