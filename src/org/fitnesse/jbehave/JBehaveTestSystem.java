package org.fitnesse.jbehave;

import fitnesse.testsystems.*;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.model.*;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.parsers.StoryParser;
import org.jbehave.core.parsers.TransformingStoryParser;
import org.jbehave.core.parsers.gherkin.GherkinStoryParser;
import org.jbehave.core.reporters.*;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.MarkUnmatchedStepsAsPending;
import util.FileUtil;

import java.io.Closeable;
import java.util.*;

import static fitnesse.html.HtmlUtil.escapeHTML;
import static java.lang.String.format;

/**
 * <p>JBehave test system.</p>
 */
public class JBehaveTestSystem implements TestSystem {

    private final String name;
    private final ClassLoader classLoader;
    private final CompositeTestSystemListener testSystemListener;

    private boolean started = false;
    private TestSummary testSummary;
    private int scenarioCnt;

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
    public void start() {

        started = true;

        testSystemListener.testSystemStarted(this);
    }

    @Override
    public void bye() {
        kill();
    }

    @Override
    public void kill() {
        testSystemListener.testSystemStopped(this, null);

        if (classLoader instanceof Closeable) {
            FileUtil.close((Closeable) classLoader);
        }
    }

    @Override
    public void runTests(TestPage pageToTest) {
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        testSummary = new TestSummary();

        testSystemListener.testStarted(pageToTest);

        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            Embedder embedder = newEmbedder(getLocale(pageToTest), pageToTest);

            resolveCandidateSteps(pageToTest, embedder);

            embedder.runStoriesAsPaths(Collections.singletonList(pageToTest.getContent()));
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            testSystemListener.testComplete(pageToTest, testSummary);
        }
    }

    private Locale getLocale(TestPage pageToTest) {
        String language = pageToTest.getVariable("language");
        Locale locale = Locale.ENGLISH;
        if (StringUtils.isNotEmpty(language))
            locale = Locale.forLanguageTag(language);
        return locale;
    }

    @Override
    public boolean isSuccessfullyStarted() {
        return started;
    }

    @Override
    public void addTestSystemListener(TestSystemListener listener) {
        testSystemListener.addTestSystemListener(listener);
    }

    private Embedder newEmbedder(Locale locale, TestPage pageToTest) {
        Embedder embedder = new Embedder();
        embedder.useConfiguration(getConfig(new LocalizedKeywords(locale), pageToTest));
        embedder.useEmbedderFailureStrategy(new FitNesseFailureStrategy());
        embedder.embedderControls().doGenerateViewAfterStories(false);
        return embedder;
    }

    private Configuration getConfig(LocalizedKeywords keywords, TestPage pageToTest) {
        StoryParser parser = new RegexStoryParser(keywords);
        String parserType = pageToTest.getVariable("parser");
        if (parserType != null && "gherkin".equals(parserType.trim().toLowerCase())){
            parser = new TransformingStoryParser(parser,new GherkinStoryParser.GherkinTransformer(keywords));
        }
        return new MostUsefulConfiguration().useKeywords(keywords)
                .useStepCollector(new MarkUnmatchedStepsAsPending(keywords))
                .useStoryParser(parser)
                .useDefaultStoryReporter(new ConsoleOutput(keywords))
                .useStoryLoader(getStoryLoader())
                .useStoryReporterBuilder(getStoryReporterBuilder(keywords));
    }

    private StoryReporterBuilder getStoryReporterBuilder(final LocalizedKeywords keywords) {
        return new StoryReporterBuilder()
                .withFormats(new Format("FITNESSE") {
                    @Override
                    public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
                        return new FitNesseStoryReporter(keywords);
                    }
                })
                .withCodeLocation(CodeLocations.codeLocationFromPath("."))
                .withRelativeDirectory("")
                .withKeywords(keywords);
    }

    private StoryLoader getStoryLoader() {
        return new StoryLoader() {
            @Override
            public String loadStoryAsText(String storyPath) {
                // We pass in the story text, so just pass it through
                return storyPath;
            }

            @Override
            public String loadResourceAsText(String resourcePath) {
                throw new IllegalStateException("Should not load resources as text.");
            }
        };
    }

    protected void resolveCandidateSteps(TestPage pageToTest, Embedder embedder) {
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
        List<Object> steps = new LinkedList<>();
        for (String stepName : stepNames) {
            try {
                steps.add(classLoader.loadClass(stepName).newInstance());
            } catch (Exception e) {
                processStep(ExecutionResult.ERROR, format("Unable to load steps from %s: %s", stepName, e.toString()));
            }
        }
        return steps;
    }


    private void println(String message) {
        output(format("%s<br/>", message));
    }

    private void output(String message) {
        testSystemListener.testOutputChunk(message);
    }

    private void processStep(ExecutionResult result, String... message) {
        testSummary.add(result);
        StringBuilder sb = new StringBuilder();
        sb.append("<span class='").append(result.name().toLowerCase()).append("'>");
        if (message != null && message.length > 0) {
            sb.append(escapeHTML(message[0]));
            if (message.length > 1) {
                for (int i = 1; i < message.length; i++) {
                    sb.append("<br>").append(escapeHTML(message[i]));
                }
            }
        }
        sb.append("</span><br/>");
        output(sb.toString());
    }

    public int getScenarioCnt() {
        return scenarioCnt;
    }

    public class FitNesseStoryReporter implements StoryReporter {

        private final LocalizedKeywords keywords;

        public FitNesseStoryReporter(LocalizedKeywords keywords) {
            this.keywords = keywords;
        }

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
                println("<em>" + keywords.inOrderTo() + "</em> " + escapeHTML(narrative.inOrderTo()));
            if (!"".equals(narrative.asA()))
                println("<em>" + keywords.asA() + "</em> " + escapeHTML(narrative.asA()));
            if (!"".equals(narrative.iWantTo()))
                println("<em>" + keywords.iWantTo() + "</em> " + escapeHTML(narrative.iWantTo()));
            if (!"".equals(narrative.soThat()))
                println("<em>" + keywords.soThat() + "</em> " + escapeHTML(narrative.soThat()));
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
            scenarioCnt++;
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
            processStep(ExecutionResult.PASS, step);
        }

        @Override
        public void failed(String step, Throwable cause) {
            String[] messages = cause.getCause() == null ? new String[]{step} : new String[]{step, "  " + cause.getCause().getMessage()};
            processStep(ExecutionResult.FAIL, messages);
        }

        @Override
        public void ignorable(String step) {
            processStep(ExecutionResult.IGNORE, step);
        }

        @Override
        public void pending(String step) {
            processStep(ExecutionResult.ERROR, format("Missing step: '%s'", step));
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
        public void restartedStory(Story story, Throwable cause) {
            println("restarted story " + story.getName() + " " + cause);
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
                processStep(ExecutionResult.ERROR, failure.getValue().getMessage());
            }

        }

        @Override
        public void handleFailures(ReportsCount count) {
            throw new IllegalStateException("No view should have been generated");
        }
    }
}
