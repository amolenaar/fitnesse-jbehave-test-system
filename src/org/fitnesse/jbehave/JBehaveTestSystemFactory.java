package org.fitnesse.jbehave;

import fitnesse.testsystems.ClassPath;
import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class JBehaveTestSystemFactory implements TestSystemFactory {

    @Override
    public TestSystem create(Descriptor descriptor) throws IOException {
        URLClassLoader classLoader = new URLClassLoader(getUrlsFromClassPath(descriptor), getClass().getClassLoader());
        return new JBehaveTestSystem(descriptor.getTestSystem(), classLoader);
    }

    private URL[] getUrlsFromClassPath(Descriptor descriptor) throws MalformedURLException {
        ClassPath classPath = descriptor.getClassPath();
        List<String> pathElements = classPath.getElements();
        URL[] urls = new URL[pathElements.size()];
        int i = 0;
        for (String path : pathElements) {
            urls[i++] = new File(path).toURI().toURL();
        }
        return urls;
    }
}
