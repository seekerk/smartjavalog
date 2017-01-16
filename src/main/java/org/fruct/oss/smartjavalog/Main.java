package org.fruct.oss.smartjavalog;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core application class
 *
 * @author Kirill Kulakov
 */
public class Main {

    String owl_file = "samples/user.owl";

    String output_folder = "./output";

    OntModel model;

    /**
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Main javalog = new Main();

        // TODO: read command line arguments
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("Use file \"" + javalog.owl_file + "\"");
        System.out.println("Output folder: \"" + javalog.output_folder + "\"");

        //open file
        try {
            javalog.parse();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // пробегаемся по классам
        ExtendedIterator<OntClass> iter = javalog.model.listClasses();
        while(iter.hasNext()) {
            javalog.generateClass(iter.next());
                 System.out.println("====== CLASS ==========");
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
        }
    }

    /**
     * Parse owl/rdf file
     */
    void parse() throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(owl_file);
        model = ModelFactory.createOntologyModel();
        model.read(in, null);
        in.close();
    }

    private void generateClass(OntClass next) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
