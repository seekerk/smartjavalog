package org.fruct.oss.smartjavalog;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        builder.setOwlFile(Objects.requireNonNull(getClass().getClassLoader().getResource("JavaLogBuilder/point.owl")).getFile());
        assertNull(builder.getOutputFolder());
        assertEquals(Objects.requireNonNull(getClass().getClassLoader().getResource("JavaLogBuilder/point.owl")).getFile(), builder.getOwlFile());
        assertNull(builder.getPackageName());
        assertEquals("default", builder.getPlatform());
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

        public List<LoggingEvent> getLog() {
            return new ArrayList<LoggingEvent>(log);
        }
    }
}