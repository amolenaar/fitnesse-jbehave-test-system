package org.fitnesse.testsystems.jbehave;

import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemFactory;
import fitnesse.wiki.PageData;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class JBehaveTestSystemFactory implements TestSystemFactory {

    @Override
    public TestSystem create(Descriptor descriptor) throws IOException {
        // TODO: get classpath
        // TODO: create class loader
        try {
            return new JBehaveTestSystem(descriptor.getTestSystem());
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
