package org.fruct.oss.smartjavalog;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class JavaLogBuilderTest {
    private final TestAppender appender = new TestAppender();
    private final Logger logger = Logger.getRootLogger();

    @BeforeEach
    void setUp() {
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.removeAppender(appender);
    }

    @Test
    void simpleTest() {
        JavaLogBuilder builder = new JavaLogBuilder();
        assertNull(builder.getOutputFolder());
        assertNull(builder.getOwlFile());
        assertNull(builder.getPackageName());
        assertEquals("default", builder.getPlatform());
    }

    @Test
    void initObjectTest() {
        JavaLogBuilder builder = new JavaLogBuilder();
        builder.setOwlFile("wdfgfnmhjk,jmhgnbfvf cxsvbnhjgfdvfb nbg");
        builder.setOutputFolder("1234567890");
        builder.setPackageName("qwertyppoiuytre");
        builder.setPlatform("android");

        assertEquals("wdfgfnmhjk,jmhgnbfvf cxsvbnhjgfdvfb nbg", builder.getOwlFile());
        assertEquals("1234567890", builder.getOutputFolder());
        assertEquals("qwertyppoiuytre", builder.getPackageName());
        assertEquals("android", builder.getPlatform());
    }

    @Test
    void failTemplateLoading() {
        JavaLogBuilder builder = new JavaLogBuilder();
        Method loadTemplate = null;
        try {
            loadTemplate = builder.getClass().getDeclaredMethod("loadTemplate", String.class);
            loadTemplate.setAccessible(true);
        } catch (NoSuchMethodException e) {
            fail(e);
        }

        try {
            loadTemplate.invoke(builder, "123");
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }

        final List<LoggingEvent> log = appender.getLog();
        final LoggingEvent firstLogEntry = log.get(0);
        assertEquals(firstLogEntry.getLevel(), Level.ERROR);
        assertTrue(firstLogEntry.getRenderedMessage().contains("Can't load template"));
        assertEquals(firstLogEntry.getLoggerName(), JavaLogBuilder.class.getName());
    }

    @Test
    void parseTest() {
        JavaLogBuilder builder = new JavaLogBuilder();
        builder.setOwlFile(Objects.requireNonNull(getClass().getClassLoader().getResource("JavaLogBuilder/point.owl")).getFile());
        try {
            builder.parse();
        } catch (OWLOntologyCreationException e) {
            fail(e);
        }
        try {
            Field field = builder.getClass().getDeclaredField("ontology");
            field.setAccessible(true);
            OWLOntology ontology = (OWLOntology) field.get(builder);
            assertEquals(ontology.getAxiomCount(), 5);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }

    }

    @Test
    void parseUnknownItem() {
        JavaLogBuilder builder = new JavaLogBuilder();
        builder.setOwlFile(Objects.requireNonNull(getClass().getClassLoader().getResource("JavaLogBuilder/bad1.owl")).getFile());
        assertThrows(NullPointerException.class, builder::parse);
    }

    @Test
    void getJavaTypeTest() {
        JavaLogBuilder builder = new JavaLogBuilder();
        Method getJavaType = null;
        try {
            getJavaType = JavaLogBuilder.class.getDeclaredMethod("getJavaType", OWL2Datatype.class);
            getJavaType.setAccessible(true);
            assertEquals("Integer", getJavaType.invoke(builder, OWL2Datatype.XSD_INTEGER));
            assertEquals("String", getJavaType.invoke(builder, OWL2Datatype.XSD_STRING));
            assertEquals("Double", getJavaType.invoke(builder, OWL2Datatype.XSD_DOUBLE));
            assertEquals("Double", getJavaType.invoke(builder, OWL2Datatype.XSD_DECIMAL));
            assertEquals("Boolean", getJavaType.invoke(builder, OWL2Datatype.XSD_BOOLEAN));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }

        try {
            getJavaType.invoke(builder, OWL2Datatype.XSD_INT);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            assertEquals(e.getCause().getClass(), IllegalStateException.class);
        }
    }

    class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        List<LoggingEvent> getLog() {
            return new ArrayList<LoggingEvent>(log);
        }
    }
}