package org.fitnesse.testsystems.jbehave;

import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class JBehaveTestSystemFactoryTest {

    @Test
    public void factoryReturnsRunningTestSystemInstance() throws IOException {
        JBehaveTestSystemFactory factory = new JBehaveTestSystemFactory();
        Descriptor descriptor = mock(Descriptor.class);

        TestSystem testSystem = factory.create(descriptor);

        assertThat(testSystem.isSuccessfullyStarted(), is(true));
    }
}
