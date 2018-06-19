package org.fruct.oss.smartjavalog;

import org.junit.jupiter.api.Test;
import org.slf4j.event.LoggingEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class JavaLogBuilderTest {

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

        //@TODO: добавить проверку вывода в лог сообщения
    }
}