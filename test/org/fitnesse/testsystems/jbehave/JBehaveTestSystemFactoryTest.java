package org.fitnesse.testsystems.jbehave;

import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JBehaveTestSystemFactoryTest {

    @Test
    public void factoryReturnsRunningTestSystemInstance() throws IOException {
        JBehaveTestSystemFactory factory = new JBehaveTestSystemFactory(new Properties());
        Descriptor descriptor = mock(Descriptor.class);
        when(descriptor.getClassPath()).thenReturn("classes");

        TestSystem testSystem = factory.create(descriptor);

        assertThat(testSystem.isSuccessfullyStarted(), is(false));
    }
}
