package org.fitnesse.testsystems.jbehave;

import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemFactory;

import java.io.IOException;

public class JBehaveTestSystemFactory implements TestSystemFactory {

    @Override
    public TestSystem create(Descriptor descriptor) throws IOException {
        // TODO: get classpath
        // TODO: create class loader
        // TODO: determine steps same way as we do for classpath
        return new JBehaveTestSystem(descriptor.getTestSystem());
    }
}
