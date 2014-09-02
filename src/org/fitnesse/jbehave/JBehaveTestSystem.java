package org.fitnesse.jbehave;

import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.*;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.model.*;
import org.jbehave.core.reporters.*;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import static fitnesse.wikitext.Utils.escapeHTML;
import static java.lang.String.format;

/**
 * <p>JBehave test system.</p>
 *
 * <p>NB. It requires TestPage's of the type WikiTestPage.</p>
 */
public class JBehaveTestSystem implements TestSystem {

    private final String name;
    private final ClassLoader classLoader;
    private final CompositeTestSystemListener testSystemListener;

    private boolean started = false;
    private TestSummary testSummary;

    public JBehaveTestSystem(String name, ClassLoader classLoader) {
        super();
        this.name = name;
        this.classLoader = classLoader;
        this.testSystemListener = new CompositeTestSystemListener();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() throws IOException {

        started = true;

        testSystemListener.testSystemStarted(this);
    }

    @Override
    public void bye() throws IOException, InterruptedException {
        kill();
    }

    @Override
    public void kill() throws IOException {
        testSystemListener.testSystemStopped(this, null);

        if (classLoader instanceof Closeable) {
            ((Closeable) classLoader).close();
        }
    }

    @Override
    public void runTests(TestPage pageToTest) throws IOException, InterruptedException {
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        testSummary = new TestSummary();

        testSystemListener.testStarted(pageToTest);

        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            Embedder embedder = newEmbedder();

            resolveCandidateSteps((WikiTestPage) pageToTest, embedder);

            embedder.runStoriesAsPaths(Arrays.asList(((WikiTestPage) pageToTest).getData().getContent()));
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            testSystemListener.testComplete(pageToTest, testSummary);
        }
    }

    @Override
    public boolean isSuccessfullyStarted() {
        return started;
    }

    @Override
    public void addTestSystemListener(TestSystemListener listener) {
        testSystemListener.addTestSystemListener(listener);
    }

    private Embedder newEmbedder() {
        Embedder embedder = new Embedder();
        embedder.configuration()
                .useStoryLoader(new StoryLoader() {
                    @Override
                    public String loadStoryAsText(String storyPath) {
                        // We pass in the story text, so just pass it through
                        return storyPath;
                    }
                })
                .useStoryReporterBuilder(new StoryReporterBuilder().withFormats(new Format("FITNESSE") {
                    @Override
                    public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
                        return new FitNesseStoryReporter();
                    }
                }));

        embedder.useEmbedderFailureStrategy(new FitNesseFailureStrategy());

        embedder.embedderControls().doGenerateViewAfterStories(false);

        return embedder;
    }

    protected void resolveCandidateSteps(WikiTestPage pageToTest, Embedder embedder) {
        Collection<String> stepNames = new StepsBuilder().getSteps(pageToTest);

        for (Object step : resolveClassInstances(stepNames)) {
            if (step instanceof CandidateSteps) {
                embedder.candidateSteps().add((CandidateSteps) step);
            } else {
                embedder.candidateSteps().addAll(new InstanceStepsFactory(embedder.configuration(), step).createCandidateSteps());
            }
        }
    }

    private Collection<Object> resolveClassInstances(Collection<String> stepNames) {
        List<Object> steps = new LinkedList();
        for (String stepName : stepNames) {
            try {
                steps.add(classLoader.loadClass(stepName).newInstance());
            } catch (Exception e) {
                processStep(format("Unable to load steps from %s: %s", stepName, e.toString()), ExecutionResult.ERROR);
            }
        }
        return steps;
    }


    private void println(String message) {
        output(format("%s<br/>", message));
    }

    private void output(String message) {
        try {
            testSystemListener.testOutputChunk(message);
        } catch (IOException e) {
            throw new RuntimeException("Unable to send ", e);
        }
    }

    private void processStep(String message, ExecutionResult result) {
        testSummary.add(result);
        output(format("<span class='%s'>%s</span><br/>", result.name().toLowerCase(), escapeHTML(message)));
    }

    public class FitNesseStoryReporter implements StoryReporter {

        @Override
        public void storyNotAllowed(Story story, String filter) {
            println("storyNotAllowed");
        }

        @Override
        public void storyCancelled(Story story, StoryDuration storyDuration) {
            println("storyCancelled");
        }

        @Override
        public void beforeStory(Story story, boolean givenStory) {
//            println("beforeStory");
        }

        @Override
        public void afterStory(boolean givenStory) {
//            println("afterStory " + givenStory);
        }

        @Override
        public void narrative(Narrative narrative) {
            if (!"".equals(narrative.inOrderTo()))
                println("<em>In order to</em> " + escapeHTML(narrative.inOrderTo()));
            if (!"".equals(narrative.asA()))
                println("<em>As a</em> " + escapeHTML(narrative.asA()));
            if (!"".equals(narrative.iWantTo()))
                println("<em>I want to</em> " + escapeHTML(narrative.iWantTo()));
            if (!"".equals(narrative.soThat()))
                println("<em>So that</em> " + escapeHTML(narrative.soThat()));
        }

        @Override
        public void lifecyle(Lifecycle lifecycle) {
//            println("lifecyle " + lifecycle);
        }

        @Override
        public void scenarioNotAllowed(Scenario scenario, String filter) {
            println("scenarioNotAllowed " + scenario + " " + filter);
        }

        @Override
        public void beforeScenario(String scenarioTitle) {
            println(format("<h3>Scenario: %s</h3><div class='jbehave-scenario'>", escapeHTML(scenarioTitle)));
        }

        @Override
        public void scenarioMeta(Meta meta) {
            println("scenarioMeta: " + meta);
        }

        @Override
        public void afterScenario() {
            println("</div>");
        }

        @Override
        public void givenStories(GivenStories givenStories) {
            println("givenStories " + givenStories);
        }

        @Override
        public void givenStories(List<String> storyPaths) {
            println("givenStories " + storyPaths);
        }

        @Override
        public void beforeExamples(List<String> steps, ExamplesTable table) {
            output("<div class='jbehave-scenario'>");
        }

        @Override
        public void example(Map<String, String> tableRow) {
            println(format("<h4>Example: <small>%s</small></h4>", escapeHTML(tableRow.toString())));
        }

        @Override
        public void afterExamples() {
            println("</div>");
        }

        @Override
        public void beforeStep(String step) {
        }

        @Override
        public void successful(String step) {
            processStep(step, ExecutionResult.PASS);
        }

        @Override
        public void failed(String step, Throwable cause) {
            processStep(step, ExecutionResult.FAIL);
        }

        @Override
        public void ignorable(String step) {
            processStep(step, ExecutionResult.IGNORE);
        }

        @Override
        public void pending(String step) {
            processStep(format("Missing step: '%s'", step), ExecutionResult.ERROR);
        }

        @Override
        public void notPerformed(String step) {
            println("notPerformed '" + step + "'");
        }

        @Override
        public void failedOutcomes(String step, OutcomesTable table) {
            println("failedOutcomes " + step + " " + table);
        }

        @Override
        public void restarted(String step, Throwable cause) {
            println("restarted " + step + " " + cause);
        }

        @Override
        public void dryRun() {
            println("dryRun");
        }

        @Override
        public void pendingMethods(List<String> methods) {
            println("<h4>Pending methods</h4>");
            for (String method : methods) {
                println(format("<pre>%s</pre>", method));
            }
        }
    }

    public class FitNesseFailureStrategy implements Embedder.EmbedderFailureStrategy {
        @Override
        public void handleFailures(BatchFailures failures) {
            for (Map.Entry<String, Throwable> failure : failures.entrySet()) {
                processStep(failure.getValue().getMessage(), ExecutionResult.ERROR);
            }

        }

        @Override
        public void handleFailures(ReportsCount count) {
            throw new IllegalStateException("No view should have been generated");
        }
    }
}
