package org.fruct.oss.smartjavalog;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

/**
 * Core application class
 *
 * @author Kirill Kulakov
 */
public class Main {
    
    static final String CLASS_TEMPLATE = "templates/class.template";
    String classTemplate;

    String owlFile = "samples/user.owl";

    String outputFolder = "./output";
    
    String packageName = "org.fruct.oss";
    

    OntModel model;

    Main() {
        classTemplate = loadTemplate(CLASS_TEMPLATE);
    }
    
    /**
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Main javalog = new Main();

        // TODO: read command line arguments
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("Use file \"" + javalog.owlFile + "\"");
        System.out.println("Output folder: \"" + javalog.outputFolder + "\"");

        //open file
        try {
            javalog.parse();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // создаем результирующий каталог
        File outputDir = new File(javalog.outputFolder);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        javalog.generateFactory();
        
        // пробегаемся по классам
        ExtendedIterator<OntClass> iter = javalog.model.listClasses();
        while(iter.hasNext()) {
            OntClass ontclss = iter.next();
            String classContent = javalog.generateClass(ontclss);
            try {
                System.out.println("Create file \"" + javalog.outputFolder + "/" + ontclss.getLocalName() + ".java\"");
                PrintWriter writer = new PrintWriter(javalog.outputFolder + "/" + ontclss.getLocalName() + ".java");
                writer.print(classContent);
                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }


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
     * Parse owl/rdf file
     */
    void parse() throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(owlFile);
        model = ModelFactory.createOntologyModel();
        model.read(in, null);
        in.close();
    }

    private String generateClass(OntClass classValue) {
        
        // generate output file
        ST classContent;
        classContent = new ST(classTemplate, '$', '$');
        classContent.add("PACKAGE_NAME", packageName);
        classContent.add("CLASS_NAME", classValue.getLocalName());
        classContent.add("CLASS_URI", classValue.toString());
        //TODO: CLASS_PROPERTIES
        //TODO: PROPERTIES_UPDATE
        
        return classContent.render();
    }
    
    private String loadTemplate(String fileName) {
        String result = "";

	ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName), "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

	return result;
    }

    private void generateFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
