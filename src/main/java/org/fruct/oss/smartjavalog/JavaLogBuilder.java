package org.fruct.oss.smartjavalog;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.System.exit;

public class JavaLogBuilder {

    private static Logger log = Logger.getLogger(JavaLogBuilder.class.getName());

    private static final String CLASS_TEMPLATE = "templates/class.java";
    private String classTemplate;

    private static final String COMPLEX_DATA_TEMPLATE = "templates/complex-data.java";
    private String complexDataTemplate;

    private static final String DATAPROPERTY_TEMPLATE = "templates/data-property.java";
    private String dataPropertyTemplate;

    private static final String SETDATAPROPERTY_TEMPLATE = "templates/set-data-property.java";
    private String setDataPropertyTemplate;

    private static final String OBJECTPROPERTY_TEMPLATE = "templates/object-property.java";
    private String objectPropertyTemplate;

    private static final String UPDATE_DATAPROPERTY_TEMPLATE = "templates/update-data-property.java";
    private String updateDataPropertyTemplate;

    private static final String UPDATE_OBJECTPROPERTY_TEMPLATE = "templates/update-object-property.java";
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

    /**
     * используемая платформа
     */
    private String platform = "default";


    private OWLOntology ontology = null;

    private static String smartJavalogPackageName = "org.fruct.oss.smartjavalog.base";

    JavaLogBuilder() {
        classTemplate = loadTemplate(CLASS_TEMPLATE);
        complexDataTemplate = loadTemplate(COMPLEX_DATA_TEMPLATE);
        dataPropertyTemplate = loadTemplate(DATAPROPERTY_TEMPLATE);
        setDataPropertyTemplate = loadTemplate(SETDATAPROPERTY_TEMPLATE);
        objectPropertyTemplate = loadTemplate(OBJECTPROPERTY_TEMPLATE);
        updateDataPropertyTemplate = loadTemplate(UPDATE_DATAPROPERTY_TEMPLATE);
        updateObjectPropertyTemplate = loadTemplate(UPDATE_OBJECTPROPERTY_TEMPLATE);
    }

    /**
     * Load template file into string object
     *
     * @param fileName path to file
     * @return file content or null
     */
    private String loadTemplate(String fileName) {
        String result = null;

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName), "UTF-8");
        } catch (IOException | NullPointerException ex) {
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
    void parse() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(owlFile));
        } catch (OWLOntologyCreationException e) {
            log.log(Level.SEVERE, "Parse error", e);
            return;
        }
        System.err.println("Loaded ontology: " + ontology.getAxiomCount() + " axioms");
    }

    private void generateFactory() {
        Pattern pattern = Pattern.compile("templates/base/.*java");
        Collection<String> files;
            files = getResources(pattern);

        for (String patternFile : files) {
            ST factoryContent;

            String templateContent = loadTemplate(patternFile);

            factoryContent = new ST(templateContent, '$', '$');
            factoryContent.add("PACKAGE_NAME", packageName);

            saveFile(getFileName(patternFile), factoryContent.render(), smartJavalogPackageName);
        }

        // платформо-специфичные шаблоны
        pattern = Pattern.compile("templates/" + platform + "/.*java");
        files = getResources(pattern);

        for (String patternFile : files) {
            ST factoryContent;

            String templateContent = loadTemplate(patternFile);

            factoryContent = new ST(templateContent, '$', '$');
            factoryContent.add("PACKAGE_NAME", packageName);

            saveFile(getFileName(patternFile), factoryContent.render(), smartJavalogPackageName);
        }
//
//        // baserdf.java
//
//        //sibFactory.java
//        factoryContent = new ST(sibFactoryTemplate, '$', '$');
//        factoryContent.add("PACKAGE_NAME", packageName);
//
//        saveFile("SIBFactory.java", factoryContent.render());
//
//        //sibFactory.java
//        factoryContent = new ST(kpiproxyTemplate, '$', '$');
//        factoryContent.add("PACKAGE_NAME", packageName);
//
//        saveFile("KPIproxy.java", factoryContent.render());
    }

    private String getFileName(String path) {
        Path p = Paths.get(path);
        return p.getFileName().toString();
    }

    private void saveFile(String fileName, String value) {
        saveFile(fileName, value, this.packageName);
    }

    private void saveFile(String fileName, String value, String packageName) {
        String path = this.outputFolder + "/" + packageName.replace(".", "/");

        try {
            //log.log(Level.INFO, "Create file \"" + path + "/" + fileName + "\"");
            File outputDir = new File(path);
            if (!outputDir.exists()) {
                //log.log(Level.INFO, "Create path \"" + path + "\"");
                if (!outputDir.mkdirs()) {
                    System.err.println("Can't create folder \"" + outputDir.getAbsolutePath() + "\"");
                    exit(2);
                }
            }

            PrintWriter writer = new PrintWriter(path + "/" + fileName);
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

        Map<String, OntologyComplexDataType> dataTypes = OntologyFactory.getInstance().getDataTypes();

        for (OntologyComplexDataType type: dataTypes.values()) {
            if (type.isOnOfType())
                printComplexDataType(type);
        }
    }

    /**
     * Печать структуры класса в шаблоне с сохранением в файл
     *
     * @param cls содержимое класса
     */
    private void printClass(OntologyObject cls) {
        ST classContent;
        classContent = new ST(classTemplate, '$', '$');
        classContent.add("PACKAGE_NAME", packageName);
        classContent.add("CLASS_NAME", cls.getName());
        classContent.add("CLASS_URI", cls.getIRI().getIRIString());
        classContent.add("CLASS_DESCRIPTION", " * " + OntologyFactory.getInstance().getComment(cls.getIRI()));

        StringBuilder propertyCollector = new StringBuilder();
        StringBuilder updatePropertyCollector = new StringBuilder();
        //пробегаемся по свойствам
        Map<String, IRI> classProperties = cls.getProperties();
        for (IRI property : classProperties.values()) {
            String propertyName = property.getFragment().substring(0,1).toUpperCase() + property.getFragment().substring(1);
            if (OntologyFactory.getInstance().isSimpleDataProperty(property)) {
                // если это не класс, а данные, то выбираем тип
                log.info("Found data property \"" + property.getFragment() + "\" for class \"" + cls.getName() + "\"");
                List<OntologyComplexDataType.DataTypeWithValue> types = OntologyFactory.getInstance().getProperty(property).getOWLDataTypes();
                System.err.println("Property " + property.getFragment() + " has types count=" + types.size());

                StringBuilder setPropertyCollector = new StringBuilder();
                // пробегаемся по типам
                for (OntologyComplexDataType.DataTypeWithValue type : types) {
                    ST setPropertyTemplate = new ST(setDataPropertyTemplate, '$', '$');
                    setPropertyTemplate.add("PROPERTY_NAME", propertyName);
                    setPropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                    setPropertyTemplate.add("PROPERTY_TYPE", getJavaType(type.getType()));
                    setPropertyTemplate.add("MIN_CARDINALITY", type.getCardinality().getMinCardinality());
                    setPropertyTemplate.add("MAX_CARDINALITY", type.getCardinality().getMaxCardinality());
                    setPropertyTemplate.add("EXACT_CARDINALITY", type.getCardinality().getExactCardinality());
                    setPropertyCollector.append(setPropertyTemplate.render());
                }

                if (types.size() == 0) {
                    ST setPropertyTemplate = new ST(setDataPropertyTemplate, '$', '$');
                    setPropertyTemplate.add("PROPERTY_NAME", propertyName);
                    setPropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                    setPropertyTemplate.add("PROPERTY_TYPE", "String");
                    setPropertyTemplate.add("MIN_CARDINALITY", -1);
                    setPropertyTemplate.add("MAX_CARDINALITY", -1);
                    setPropertyTemplate.add("EXACT_CARDINALITY", -1);
                    setPropertyCollector.append(setPropertyTemplate.render());
                }

                ST propertyTemplate = new ST(dataPropertyTemplate, '$', '$');
                ST updatePropertyTemplate = new ST(updateDataPropertyTemplate, '$', '$');
                propertyTemplate.add("PROPERTY_NAME", propertyName);
                propertyTemplate.add("PROPERTY_URI", property.getIRIString());
                propertyTemplate.add("SET_DATA_PROPERTY", setPropertyCollector.toString());
                propertyCollector.append(propertyTemplate.render());

                updatePropertyTemplate.add("PROPERTY_NAME", propertyName);
                updatePropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                updatePropertyCollector.append(updatePropertyTemplate.render());

            } else if (OntologyFactory.getInstance().isComplexDataProperty(property)) {
                log.info("Found complex data property\"" + property.getFragment() + "\" for class \"" + cls.getName() + "\"");

                ST propertyTemplate = new ST(objectPropertyTemplate, '$', '$');
                ST updatePropertyTemplate = new ST(updateObjectPropertyTemplate, '$', '$');
                String propType = OntologyFactory.getInstance().getProperty(property).getComplexDataValue();
                Cardinality crd = OntologyFactory.getInstance().getProperty(property).getComplexDataCardinality();
                propertyTemplate.add("PROPERTY_NAME", propertyName);
                propertyTemplate.add("PROPERTY_URI", property.getIRIString());
                propertyTemplate.add("PROPERTY_TYPE", propType);
                propertyTemplate.add("MIN_CARDINALITY", crd.getMinCardinality());
                propertyTemplate.add("MAX_CARDINALITY", crd.getMaxCardinality());
                propertyTemplate.add("EXACT_CARDINALITY", crd.getExactCardinality());
                propertyCollector.append(propertyTemplate.render());

                updatePropertyTemplate.add("PROPERTY_NAME", propertyName);
                updatePropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                updatePropertyTemplate.add("PROPERTY_TYPE", propType);
                updatePropertyCollector.append(updatePropertyTemplate.render());

            } else {
                // это класс, просто добавяем к списку
                log.info("Found class property\"" + property.getFragment() + "\" for class \"" + cls.getName() + "\"");
                ST propertyTemplate = new ST(objectPropertyTemplate, '$', '$');
                ST updatePropertyTemplate = new ST(updateObjectPropertyTemplate, '$', '$');
                String propType = OntologyFactory.getInstance().getProperty(property).getClassValue();
                Cardinality crd = OntologyFactory.getInstance().getProperty(property).getClassCardinality();
                propertyTemplate.add("PROPERTY_NAME", propertyName);
                propertyTemplate.add("PROPERTY_URI", property.getIRIString());
                propertyTemplate.add("PROPERTY_TYPE", propType);
                propertyTemplate.add("MIN_CARDINALITY", crd.getMinCardinality());
                propertyTemplate.add("MAX_CARDINALITY", crd.getMaxCardinality());
                propertyTemplate.add("EXACT_CARDINALITY", crd.getExactCardinality());
                propertyCollector.append(propertyTemplate.render());

                updatePropertyTemplate.add("PROPERTY_NAME", propertyName);
                updatePropertyTemplate.add("PROPERTY_URI", property.getIRIString());
                updatePropertyTemplate.add("PROPERTY_TYPE", propType);
                updatePropertyCollector.append(updatePropertyTemplate.render());

            }
        }

        classContent.add("CLASS_PROPERTIES", propertyCollector.toString());
        classContent.add("PROPERTIES_UPDATE", updatePropertyCollector.toString());


        saveFile(cls.getName() + ".java", classContent.render());
    }

    private void printComplexDataType(OntologyComplexDataType dataType) {
        ST dataTypeContent;
        dataTypeContent = new ST(complexDataTemplate, '$', '$');
        dataTypeContent.add("PACKAGE_NAME", packageName);
        dataTypeContent.add("TYPE_NAME", dataType.getName());
        dataTypeContent.add("TYPE_URI", dataType.getIRI().getIRIString());
        dataTypeContent.add("TYPE_DESCRIPTION", " * " + OntologyFactory.getInstance().getComment(dataType.getIRI()));

        saveFile(dataType.getName() + ".java", dataTypeContent.render());
    }

    private String getJavaType(OWL2Datatype type) {

        switch (type.name()) {
            case "XSD_DOUBLE": {
                return "Double";
            }
            case "XSD_STRING": {
                return "String";
            }
            case "XSD_BOOLEAN": {
                return "Boolean";
            }
            case "XSD_INTEGER": {
                return "Integer";
            }
            case "XSD_DECIMAL": {
                return "Double";
            }
            default:
                throw new IllegalStateException("Not implemented for " + type.name());
        }
    }

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> getResources(
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for(final String element : classPathElements){
            retval.addAll(getResources(element, pattern));
        }
        return retval;
    }

    private static Collection<String> getResources(
            final String element,
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if(file.isDirectory()){
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else{
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> getResourcesFromJarFile(
            final File file,
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try{
            zf = new ZipFile(file);
        } catch(final IOException e){
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while(e.hasMoreElements()){
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if(accept){
                retval.add(fileName);
            }
        }
        try{
            zf.close();
        } catch(final IOException e1){
            throw new Error(e1);
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(
            final File directory,
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for(final File file : fileList){
            if(file.isDirectory()){
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else{
                try{
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if(accept){
                        retval.add(fileName);
                    }
                } catch(final IOException e){
                    throw new Error(e);
                }
            }
        }
        return retval;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }
}