package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SamplesTest {

    private JavaLogBuilder builder = null;

    private Path tempFolder = null;

    @BeforeEach
    void setUp() {
        // clean factory
        Field field = null;
        try {
            field = OntologyFactory.class.getDeclaredField("factory");
        } catch (NoSuchFieldException e) {
            fail(e);
        }
        field.setAccessible(true);
        try {
            field.set(OntologyFactory.getInstance(), null);
        } catch (IllegalAccessException e) {
            fail(e);
        }

        // create builder
        builder = new JavaLogBuilder();

        // create temp folder
        try {
            tempFolder = Files.createTempDirectory("test");
        } catch (IOException e) {
            fail(e);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            deleteFileOrFolder(tempFolder);
        } catch (IOException e) {
            fail(e);
        }
        builder = null;
        tempFolder = null;
    }

    @Test
    void testPointOwl() {
        builder.setOwlFile(Objects.requireNonNull(getClass().getClassLoader().getResource("Samples/geopoint.owl")).getFile());
        builder.setPlatform("default");
        builder.setPackageName("org.fruct.oss.test");
        builder.setOutputFolder(tempFolder.toString());

        //parse file
        try {
            builder.parse();
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }

        try {
            Collection<Path> all = new ArrayList<>();
            Enumeration<URL> templates = getClass().getClassLoader().getResources("templates/base");
            addTree(new File(templates.nextElement().getFile()).toPath(), all);
            assertEquals(10, all.size());
        } catch (IOException e) {
            fail(e);
        }
        builder.generate();

        // check result
        compileFolder(tempFolder, 3);
    }


    @Test
    void testUserOwl() {
        builder.setOwlFile(Objects.requireNonNull(getClass().getClassLoader().getResource("Samples/user.owl")).getFile());
        builder.setPlatform("default");
        builder.setPackageName("org.fruct.oss.test");
        builder.setOutputFolder(tempFolder.toString());

        //parse file
        try {
            builder.parse();
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        builder.generate();

        // check result
        compileFolder(tempFolder, 2);
    }


    /**
     * Compile generated sourses
     * @param folder path to sources folder
     */
    private void compileFolder(Path folder, int ontologyClassCount) {
        Collection<Path> all = new ArrayList<>();
        try {
            addTree(folder, all);
        } catch (IOException e) {
            fail(e);
        }
        System.err.println(((ArrayList<Path>) all).get(0).toString());
        // ontology classes + platform specific classes + base classes
        assertEquals(ontologyClassCount + 1 + 10, all.size());

        //TODO: дописать компиляцию
    }

    /**
     * Scan folder to find sources
     * @param directory path to initial folder
     * @param all result storage
     * @throws IOException From SimpleFileVisitor class
     */
    private static void addTree(Path directory, final Collection<Path> all)
            throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                all.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                e.printStackTrace(); // replace with more robust error handling
                return FileVisitResult.TERMINATE;
            }

            @Override public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if(e!=null)return handleException(e);
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    };
}