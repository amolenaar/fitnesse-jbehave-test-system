package org.fitnesse.testsystems.jbehave;

import fitnesse.testrunner.WikiTestPage;
import fitnesse.testsystems.*;
import fitnesse.wikitext.Utils;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.model.*;
import org.jbehave.core.reporters.*;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.Steps;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static fitnesse.wikitext.Utils.escapeHTML;
import static java.lang.String.format;

public class JBehaveTestSystem implements TestSystem {

    private final String name;
    private final CompositeTestSystemListener testSystemListener;
    private boolean started = false;
    private TestSummary testSummary;

    public JBehaveTestSystem(String name) {
        super();
        this.name = name;
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

    private Embedder newEmbedder() throws MalformedURLException {
        Embedder embedder = new Embedder();
        embedder.configuration()
                .useStoryLoader(new LoadFromRelativeFile(new File("FitNesseRoot").toURL()))
                .useStoryLoader(new StoryLoader() {
                    @Override
                    public String loadStoryAsText(String storyPath) {
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

    @Override
    public void bye() throws IOException, InterruptedException {
        kill();
    }

    @Override
    public void kill() throws IOException {
        testSystemListener.testSystemStopped(this, new JBehaveExecutionLog(), null);
    }

    @Override
    public void runTests(TestPage pageToTest) throws IOException, InterruptedException {
        testSummary = new TestSummary();

        testSystemListener.testStarted(pageToTest);
        Embedder embedder = newEmbedder();

        Collection<String> stepNames = new StepsBuilder().getSteps(((WikiTestPage) pageToTest).getSourcePage());
        Collection<CandidateSteps> steps = resolveClassInstances(stepNames);

        embedder.candidateSteps().addAll(steps);

        embedder.runStoriesAsPaths(Arrays.asList(pageToTest.getDecoratedData().getContent()));

        testSystemListener.testComplete(pageToTest, testSummary);
    }

    private Collection<CandidateSteps> resolveClassInstances(Collection<String> stepNames) {
        List<CandidateSteps> candidateSteps = new LinkedList();
        for (String stepName : stepNames) {
            try {
                candidateSteps.add((CandidateSteps) Class.forName(stepName).newInstance());
            } catch (Exception e) {
                processStep(format("Unable to load steps from %s: %s", stepName, e.toString()), ExecutionResult.ERROR);
            }
        }
        return candidateSteps;
    }

    @Override
    public boolean isSuccessfullyStarted() {
        return started;
    }

    @Override
    public void addTestSystemListener(TestSystemListener listener) {
        testSystemListener.addTestSystemListener(listener);
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
            println(format("<h3>Scenario: %s</h3><div class='scenario'>", escapeHTML(scenarioTitle)));
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
            output("<div class='scenario'>");
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
            println("pending '" + step + "'");
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
            println(format("Pending methods: %s", methods));
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
