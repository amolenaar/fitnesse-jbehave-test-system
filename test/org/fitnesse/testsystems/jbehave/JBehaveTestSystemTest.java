package org.fitnesse.testsystems.jbehave;

import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.*;
import fitnesse.wiki.ReadOnlyPageData;
import fitnesse.wikitext.parser.VariableSource;
import org.jbehave.core.embedder.Embedder;
import org.junit.Test;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class JBehaveTestSystemTest {

    static class TestJBehaveTestSystem extends JBehaveTestSystem {

        public TestJBehaveTestSystem(String name, ClassLoader classLoader, VariableSource variableSource) {
            super(name, classLoader, variableSource);
        }

        @Override
        protected void resolveCandidateSteps(WikiTestPage pageToTest, Embedder embedder) {
            embedder.candidateSteps().add(new ExampleSteps());
        }
    }

    @Test
    public void shouldHaveAName() {
        TestSystem testSystem = new JBehaveTestSystem("name", getClassLoader(), null);

        assertThat(testSystem.getName(), is("name"));
    }

    @Test
    public void canPerformAPassingTest() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader(), null);
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        ReadOnlyPageData pageData = mock(ReadOnlyPageData.class);
        when(pageToTest.getDecoratedData()).thenReturn(pageData);
        when(pageData.getContent()).thenReturn(FileUtil.getFileContent(new File("FitNesseRoot/JbehaveTestSystem/PassingJbehaveTest/content.txt")));
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(eq(testSystem), eq((Throwable) null));
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));

    }

    @Test
    public void canPerformAFailingTest() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader(), null);
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        ReadOnlyPageData pageData = mock(ReadOnlyPageData.class);
        when(pageToTest.getDecoratedData()).thenReturn(pageData);
        when(pageData.getContent()).thenReturn(FileUtil.getFileContent(new File("FitNesseRoot/JbehaveTestSystem/FailingJbehaveTest/content.txt")));
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(eq(testSystem), eq((Throwable) null));
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
        verify(listener, never()).testExceptionOccurred(eq((Assertion) null), any(ExceptionResult.class));
    }

    @Test
    public void canHandlePendingSteps() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader(), null);
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        ReadOnlyPageData pageData = mock(ReadOnlyPageData.class);
        when(pageToTest.getDecoratedData()).thenReturn(pageData);
        when(pageData.getContent()).thenReturn(FileUtil.getFileContent(new File("FitNesseRoot/JbehaveTestSystem/FailingJbehaveTest/content.txt")));
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(eq(testSystem), eq((Throwable) null));
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
        verify(listener, never()).testExceptionOccurred(eq((Assertion) null), any(ExceptionResult.class));
    }

    protected ClassLoader getClassLoader() {
        return new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
    }

}