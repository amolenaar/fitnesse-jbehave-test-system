package org.fitnesse.testsystems.jbehave;

import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemFactory;
import fitnesse.wiki.PageData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class JBehaveTestSystemFactory implements TestSystemFactory {

    @Override
    public TestSystem create(Descriptor descriptor) throws IOException {
        // TODO: get classpath
        // TODO: create class loader
        String[] paths = descriptor.getClassPath().split(System.getProperty("path.separator"));
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            urls[i] = new File(path).toURI().toURL();
            System.out.println("URL: " + urls[i]);
        }
        URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());

        try {
            return new JBehaveTestSystem(descriptor.getTestSystem(), classLoader);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create JBehaveTestSystem", e);
        }
    }

    private PageData getPageData(Descriptor descriptor) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Fields: " + Arrays.asList(descriptor.getClass().getDeclaredFields()));
        Field field = descriptor.getClass().getField("data");
        field.setAccessible(true);
        return (PageData) field.get(descriptor);
    }
}
