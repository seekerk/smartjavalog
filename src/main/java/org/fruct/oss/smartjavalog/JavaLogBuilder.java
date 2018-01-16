package org.fruct.oss.smartjavalog;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JavaLogBuilder {

    private static Logger log = Logger.getLogger(JavaLogBuilder.class.getName());

    private static final String CLASS_TEMPLATE = "templates/class.template";
    private String classTemplate;

    private static final String FACTORY_TEMPLATE = "templates/factory.template";
    private String factoryTemplate;

    private static final String DATAPROPERTY_TEMPLATE = "templates/data-property.template";
    private String dataPropertyTemplate;

    private static final String OBJECTPROPERTY_TEMPLATE = "templates/object-property.template";
    private String objectPropertyTemplate;

    private static final String UPDATE_DATAPROPERTY_TEMPLATE = "templates/update-data-property.template";
    private String updateDataPropertyTemplate;

    private static final String UPDATE_OBJECTPROPERTY_TEMPLATE = "templates/update-object-property.template";
    private String updateObjectPropertyTemplate;

    /**
     * источник данных
     */
    private String owlFile;// = "samples/user.owl";

    /**
     * имя пакета
     */
    private String packageName;// = "org.fruct.oss";

    /**
     * директория для записи данных
     */
    private String outputFolder;


    private OWLOntology ontology = null;

    JavaLogBuilder() {
        classTemplate = loadTemplate(CLASS_TEMPLATE);
        factoryTemplate = loadTemplate(FACTORY_TEMPLATE);
        dataPropertyTemplate = loadTemplate(DATAPROPERTY_TEMPLATE);
        objectPropertyTemplate = loadTemplate(OBJECTPROPERTY_TEMPLATE);
        updateDataPropertyTemplate = loadTemplate(UPDATE_DATAPROPERTY_TEMPLATE);
        updateObjectPropertyTemplate = loadTemplate(UPDATE_OBJECTPROPERTY_TEMPLATE);
    }

    /**
     * Load template file into string object
     * @param fileName path to file
     * @return file content or null
     */
    private String loadTemplate(String fileName) {
        String result = null;

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName), "UTF-8");
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public void setOwlFile(String owlFile) {
        this.owlFile = owlFile;
    }

    public String getOwlFile() {
        return owlFile;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * Parse owl/rdf file
     */
    void parse() throws IOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(owlFile));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return;
        }
        System.err.println("Loaded ontology: " + ontology.getAxiomCount() + " axioms");
    }

    private void generateFactory() {
        ST factoryContent;
        factoryContent = new ST(factoryTemplate, '$', '$');
        factoryContent.add("PACKAGE_NAME", packageName);

        saveFile("BaseRDF.java", factoryContent.render());
    }

    private void saveFile(String fileName, String value) {
        try {
            log.log(Level.INFO, "Create file \"" + this.outputFolder +  "/" + this.packageName.replace(".","/") + "/" + fileName + "\"");
            PrintWriter writer = new PrintWriter(this.outputFolder +  "/" + this.packageName.replace(".","/") + "/" + fileName);
            writer.print(value);
            writer.close();
        } catch (FileNotFoundException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    public void generate() {
        // генерируем базовый класс
        generateFactory();

        // пробегаемся по элементам
        List<OWLAxiom> axiomList = ontology.axioms().collect(Collectors.toList());
        for (OWLAxiom axiom : axiomList) {
            axiom.accept(new OntologyVisitor());
        }

        Map<String, OntologyObject> classes = OntologyFactory.getInstance().getObjects();

        for (OntologyObject ontologyObject : classes.values()) {
            printClass(ontologyObject);
        }
    }

    /**
     * Печать структуры класса в шаблоне с сохранением в файл
     * @param cls содержимое класса
     */
    private void printClass(OntologyObject cls) {
        //TODO: доделать

        ST classContent;
        classContent = new ST(classTemplate, '$', '$');
        classContent.add("PACKAGE_NAME", packageName);
        classContent.add("CLASS_NAME", cls.getName());
        classContent.add("CLASS_URI", cls.getURI());
        classContent.add("CLASS_DESCRIPTION", " * " + OntologyFactory.getInstance().getComment(cls.getIRI()));

        StringBuilder propertyCollector = new StringBuilder();
        StringBuilder updatePropertyCollector = new StringBuilder();
        //пробегаемся по свойствам
        Map<String, IRI> classProperties = cls.getProperties();
        for (IRI property : classProperties.values()) {
            if (OntologyFactory.getInstance().isDataProperty(property)) {
                // если это не класс, а данные, то выбираем тип
                log.info("Found data property \"" + property.getFragment() + "\" for class \"" + cls.getName() + "\"");
                List<OWL2Datatype> types = OntologyFactory.getInstance().getProperty(property).getOWLDataTypes();
                System.err.println("Types count=" + types.size());
                for (OWL2Datatype type : types) {
                    ST propertyTemplate = new ST(dataPropertyTemplate, '$', '$');
                    ST updatePropertyTemplate = new ST(updateDataPropertyTemplate, '$', '$');
                    propertyTemplate.add("PROPERTY_NAME", property.getFragment());
                    propertyTemplate.add("PROPERTY_URI", property.getIRIString());
                    propertyTemplate.add("PROPERTY_TYPE", getJavaType(type));
                    propertyCollector.append(propertyTemplate.render());

                    updatePropertyTemplate.add("PROPERTY_NAME", property.getFragment());
                    updatePropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                    updatePropertyTemplate.add("PROPERTY_TYPE", getJavaType(type));
                    updatePropertyCollector.append(updatePropertyTemplate.render());

                }

                if (types.size() == 0) {
                    // если тип данных не указан, то по умолчанию это строка
                    ST propertyTemplate = new ST(dataPropertyTemplate, '$', '$');
                    ST updatePropertyTemplate = new ST(updateDataPropertyTemplate, '$', '$');
                    propertyTemplate.add("PROPERTY_NAME", property.getFragment());
                    propertyTemplate.add("PROPERTY_URI", property.getIRIString());
                    propertyTemplate.add("PROPERTY_TYPE", "String");
                    propertyCollector.append(propertyTemplate.render());

                    updatePropertyTemplate.add("PROPERTY_NAME", property.getFragment());
                    updatePropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                    updatePropertyTemplate.add("PROPERTY_TYPE", "String");
                    updatePropertyCollector.append(updatePropertyTemplate.render());
                }

            } else {
                // это класс, просто добавяем к списку
                log.info("Found class property\"" + property.getFragment() + "\" for class \"" + cls.getName() + "\"");
                ST propertyTemplate = new ST(objectPropertyTemplate, '$', '$');
                ST updatePropertyTemplate = new ST(updateObjectPropertyTemplate, '$', '$');
                String propType = OntologyFactory.getInstance().getProperty(property).getClassValue();
                propertyTemplate.add("PROPERTY_NAME", property.getFragment());
                propertyTemplate.add("PROPERTY_URI", property.getIRIString());
                propertyTemplate.add("PROPERTY_TYPE", propType);
                propertyCollector.append(propertyTemplate.render());

                updatePropertyTemplate.add("PROPERTY_NAME", property.getFragment());
                updatePropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                updatePropertyTemplate.add("PROPERTY_TYPE", propType);
                updatePropertyCollector.append(updatePropertyTemplate.render());

            }
        }

        classContent.add("CLASS_PROPERTIES", propertyCollector.toString());
        classContent.add("PROPERTIES_UPDATE", updatePropertyCollector.toString());


        saveFile(cls.getName() + ".java", classContent.render());
    }

    private String getJavaType(OWL2Datatype type) {

        switch (type.name()) {
            case "XSD_DOUBLE": {
                return "double";
            }
            case "XSD_STRING": {
                return "String";
            }
            default:
                throw new IllegalStateException("Not implemented for " + type.name());
        }
    }

    /**
     * Основной метод запускающий генерацию кода
     */
    public void generate2() {
        // генерируем базовый класс
        generateFactory();

/*
        // пробегаемся по классам
        ExtendedIterator<OntClass> iter = model.listClasses();
        while(iter.hasNext()) {
            OntClass ontclss = iter.next();
            if (ontclss.getLocalName() == null) {
                log.log(Level.WARNING, "Parsed class doesn't have a name " + ontclss.getId());
                continue;
            }
            log.log(Level.INFO, "Parse class " + ontclss.getLocalName());
            String classContent = generateClass(ontclss);
            saveFile(ontclss.getLocalName() + ".java", classContent);


                /*            System.out.println("====== CLASS ==========");
                OntClass ontclss = iter.next();
                System.out.println("localname: " + ontclss.getLocalName()); // User
                System.out.println("namespace: " + ontclss.getNameSpace()); // http://oss.fruct.org/etourism#
                System.out.println("comment: " + ontclss.getComment(null)); // rdf:comment Описание пользователя (может быть несколько?)
                ExtendedIterator<OntProperty> propIter = ontclss.listDeclaredProperties();
                while(propIter.hasNext()) {
                OntProperty propval = propIter.next();
                System.out.println("====== PROPERTY ==========");
                System.out.println("comment: " + propval.getComment(null)); // rdf:comment
                System.out.println("localname:" + propval.getLocalName()); // name
                System.out.println("RDFType: " + propval.getRDFType()); // http://www.w3.org/2002/07/owl#DatatypeProperty
                System.out.println("cardinality: " + propval.getCardinality(propval)); // 0 ???
                System.out.println("isDatatypeProperty: " + propval.isDatatypeProperty()); // true
                if (propval.isObjectProperty()) {
                OntResource res = propval.getRange(); // null для Datatype и ontclass для objectproperty
                System.out.println("Value type: " + res.getLocalName()); // Location
                }
                }
                */
        }
    }

    /**
     * Генерация класса
     * @param classValue Точка класса
     * @return код на Java
     */
/*    private String generateClass(OntClass classValue) {
        StringBuilder classProperties = new StringBuilder();
        StringBuilder updateProperties = new StringBuilder();

        // generate methods for class properties
        ExtendedIterator<OntProperty> propIter = classValue.listDeclaredProperties();
        while (propIter.hasNext()) {
            OntProperty propVal = propIter.next();
            log.log(Level.INFO, "Parse property \"" + propVal.getLocalName() + "\"");
            ST classProperty;
            ST updateProperty;
            String propType = "String";
            if (propVal.isDatatypeProperty()) {
                classProperty = new ST(dataPropertyTemplate, '$', '$');
                updateProperty = new ST(updateDataPropertyTemplate, '$', '$');
                OntResource res = propVal.getRange(); // null для Datatype и ontclass для objectproperty
                ExtendedIterator iter = propVal.listDeclaringClasses(true);
                log.log(Level.INFO, "Property \"" + propVal.getLocalName() + "\" has type = \"" + res.getLocalName() + "\"");
                if (res.getLocalName() == null) {
                    NodeIterator i = res.getModel().listObjects();
                    //StmtIterator i = res.listProperties();
                    while (i.hasNext()) {
                        RDFNode s = i.next();
                        log.log(Level.INFO, "node: isLiteral=" + s.isLiteral() + "; isResource=" + s.isResource() + "; isAnon=" + s.isAnon());
                        //s.getModel().listObjects()
                    }
                    // сложный тип???
                    log.log(Level.INFO, "Found complex property: " + res.getRDFType());

                    continue;
                }
                if (res != null && !res.getLocalName().equals("string")) {
                    switch (res.getLocalName()) {
                        case "Double": {
                            propType = "Double";
                            break;
                        }
                        case "decimal": {
                            propType = "Integer";
                            break;
                        }

                        default:
                            System.out.println("Unknown property value type: " + res.getLocalName()); // Location
                    }
                }

                //TODO: обработка различных типов данных
            } else if (propVal.isObjectProperty()) {
                classProperty = new ST(objectPropertyTemplate, '$', '$');
                updateProperty = new ST(updateObjectPropertyTemplate, '$', '$');
                propType = propVal.getRange().getLocalName();
            } else {
                //TODO: implement for other property Types
                throw new UnsupportedOperationException("Not supported yet.");
            }

            classProperty.add("PROPERTY_NAME", propVal.getLocalName());
            classProperty.add("PROPERTY_URI", propVal.toString());
            classProperty.add("PROPERTY_TYPE", propType);
            classProperties.append(classProperty.render());

            updateProperty.add("PROPERTY_NAME", propVal.getLocalName());
            updateProperty.add("PROPERTY_URI", propVal.toString());
            updateProperty.add("PROPERTY_TYPE", propType);
            updateProperties.append(updateProperty.render());
        }

        // generate output file
        ST classContent;
        classContent = new ST(classTemplate, '$', '$');
        classContent.add("PACKAGE_NAME", packageName);
        classContent.add("CLASS_NAME", classValue.getLocalName());
        classContent.add("CLASS_URI", classValue.toString());
        classContent.add("CLASS_PROPERTIES", classProperties.toString());
        classContent.add("PROPERTIES_UPDATE", updateProperties.toString());

        return classContent.render();
    }

}
*/