package org.fitnesse.jbehave;

import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.*;
import fitnesse.wiki.PageData;
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

        public TestJBehaveTestSystem(String name, ClassLoader classLoader) {
            super(name, classLoader);
        }

        @Override
        protected void resolveCandidateSteps(TestPage pageToTest, Embedder embedder) {
            embedder.candidateSteps().add(new ExampleSteps());
        }
    }

    @Test
    public void shouldHaveAName() {
        TestSystem testSystem = new JBehaveTestSystem("name", getClassLoader());

        assertThat(testSystem.getName(), is("name"));
    }

    @Test
    public void canPerformAPassingTest() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader());
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getContent()).thenReturn(FileUtil.getFileContent(new File("FitNesseRoot/JbehaveTestSystem/PassingJbehaveTest/content.txt")));
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        assertThat(testSystem.getScenarioCnt(), is(2));

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(eq(testSystem), eq((Throwable) null));
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
    }

    @Test
    public void canPerformAPassingTestDE() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader());
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getVariable("language")).thenReturn("de");
        when(pageToTest.getContent()).thenReturn("Szenario: 2 squared\n" +
                "\n" +
                "Gegeben a variable x with value 2\n" +
                "Wenn I multiply x by 2 \n" +
                "Dann x should equal 4\n" +
                "\n" +
                "Szenario: 3 squared\n" +
                "\n" +
                "Gegeben a variable x with value 3\n" +
                "Wenn I multiply x by 3 \n" +
                "Dann x should equal 9");
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        assertThat(testSystem.getScenarioCnt(), is(2));

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(eq(testSystem), eq((Throwable) null));
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
    }

    @Test
    public void canPerformAPassingTestInvalidLanguageGiven() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader());
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getContent()).thenReturn("#language: invlid_should_default_to_english \r\n"+
                "Scenario: 2 squared\n" +
                "\n" +
                "Given a variable x with value 2\n" +
                "When I multiply x by 2 \n" +
                "Then x should equal 4\n" +
                "\n" +
                "Scenario: 3 squared\n" +
                "\n" +
                "Given a variable x with value 3\n" +
                "When I multiply x by 3 \n" +
                "Then x should equal 9");
        TestSystemListener listener = mock(TestSystemListener.class);
        testSystem.addTestSystemListener(listener);

        testSystem.start();
        testSystem.runTests(pageToTest);
        testSystem.bye();

        assertThat(testSystem.getScenarioCnt(), is(2));

        verify(listener).testSystemStarted(testSystem);
        verify(listener).testSystemStopped(eq(testSystem), eq((Throwable) null));
        verify(listener).testStarted(pageToTest);
        verify(listener).testComplete(eq(pageToTest), any(TestSummary.class));
    }

    @Test
    public void canPerformAFailingTest() throws IOException, InterruptedException {
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader());
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getContent()).thenReturn(FileUtil.getFileContent(new File("FitNesseRoot/JbehaveTestSystem/FailingJbehaveTest/content.txt")));
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
        JBehaveTestSystem testSystem = new TestJBehaveTestSystem("", getClassLoader());
        WikiTestPage pageToTest = mock(WikiTestPage.class);
        when(pageToTest.getContent()).thenReturn(FileUtil.getFileContent(new File("FitNesseRoot/JbehaveTestSystem/FailingJbehaveTest/content.txt")));
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