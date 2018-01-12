package org.fruct.oss.smartjavalog;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLogBuilder {

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

    private String owlFile;// = "samples/user.owl";

    private String packageName;// = "org.fruct.oss";

    private String outputFolder;

    OntModel model;

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
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
    void parse() throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(owlFile);
        model = ModelFactory.createOntologyModel();
        model.read(in, null);
        in.close();
    }

    private void generateFactory() {
        ST factoryContent;
        factoryContent = new ST(factoryTemplate, '$', '$');
        factoryContent.add("PACKAGE_NAME", packageName);

        saveFile("BaseRDF.java", factoryContent.render());
    }

    private void saveFile(String fileName, String value) {
        try {
            System.out.println("Create file \"" + this.outputFolder +  "/" + this.packageName.replace(".","/") + "/" + fileName + "\"");
            PrintWriter writer = new PrintWriter(this.outputFolder +  "/" + this.packageName.replace(".","/") + "/" + fileName);
            writer.print(value);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generate() {
        generateFactory();


        // пробегаемся по классам
        ExtendedIterator<OntClass> iter = model.listClasses();
        while(iter.hasNext()) {
            OntClass ontclss = iter.next();
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

    private String generateClass(OntClass classValue) {
        StringBuilder classProperties = new StringBuilder();
        StringBuilder updateProperties = new StringBuilder();

        // generate methods for class properties
        ExtendedIterator<OntProperty> propIter = classValue.listDeclaredProperties();
        while (propIter.hasNext()) {
            OntProperty propVal = propIter.next();
            ST classProperty;
            ST updateProperty;
            String propType = "String";
            if (propVal.isDatatypeProperty()) {
                classProperty = new ST(dataPropertyTemplate, '$', '$');
                updateProperty = new ST(updateDataPropertyTemplate, '$', '$');
                OntResource res = propVal.getRange(); // null для Datatype и ontclass для objectproperty
                if (res != null && res.getLocalName() != null && !res.getLocalName().equals("string")) {
                    if (res.getLocalName().equals("double")) {
                        propType = "Double";
                    } else {
                        System.out.println("Value type: " + res.getLocalName()); // Location
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
