package org.fitnesse.testsystems.jbehave;

import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.*;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class JBehaveTestSystemTest {

    @Test
    public void shouldHaveAName() {
        TestSystem testSystem = new JBehaveTestSystem("name");

        assertThat(testSystem.getName(), is("name"));
    }

    @Test
    public void canPerformAPassingTest() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new JBehaveTestSystem();
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getPath()).thenReturn("JbehaveTestSystem.PassingJbehaveTest");
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.configureStep(new ExampleSteps());
        testSystem.runTests(pageToTest);
        testSystem.bye();

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(testSystem, null, null);
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));

    }

    @Test
    public void canPerformAFailingTest() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new JBehaveTestSystem();
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getPath()).thenReturn("JbehaveTestSystem.FailingJbehaveTest");
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.configureStep(new ExampleSteps());
        testSystem.runTests(pageToTest);
        testSystem.bye();

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(testSystem, null, null);
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
        verify(listener, never()).testExceptionOccurred(eq((Assertion) null), any(ExceptionResult.class));
    }

    @Test
    public void canHandlePendingSteps() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new JBehaveTestSystem();
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getPath()).thenReturn("JbehaveTestSystem.FailingJbehaveTest");
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.configureStep(new ExampleSteps());
        testSystem.runTests(pageToTest);
        testSystem.bye();

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(testSystem, null, null);
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
        verify(listener, never()).testExceptionOccurred(eq((Assertion) null), any(ExceptionResult.class));
    }

}