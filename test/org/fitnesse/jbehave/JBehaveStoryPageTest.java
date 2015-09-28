package org.fitnesse.jbehave;

import fitnesse.wiki.SystemVariableSource;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.parser.VariableSource;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class JBehaveStoryPageTest {

    @Test
    public void scenarioNamesShouldBeRenderedAsHeaders() {
        VariableSource variableSource = new SystemVariableSource();
        WikiPage storyPage = new JBehaveStoryPage(new File("stories/simplestory.story"), "simplestory", null, variableSource);
        String html = storyPage.getHtml();
        assertThat(html, containsString("<h3>Scenario: 2 squared</h3>"));
    }
}