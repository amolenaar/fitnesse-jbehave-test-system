package org.fitnesse.jbehave;

import fitnesse.wiki.SymbolicPage;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.fs.FileSystemPage;
import fitnesse.wiki.fs.FileSystemPageFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class JBehavePageFactoryTest {
    private FileSystemPageFactory factory;
    private FileSystemPage root;

    @Before
    public void setUp() {
        factory = new FileSystemPageFactory();
        factory.registerWikiPageFactory(new JBehavePageFactory(new Properties()));
        root = factory.makePage(new File("./FitNesseRoot"), "FitNesseRoot", null);
    }

    @Test
    public void shouldLoadTocPage() {
        WikiPage page = root.getChildPage("StoryFiles");
        assertThat(page, not(nullValue()));
        assertThat(page.getName(), is("StoryFiles"));
        assertThat(page.getData().getContent(), is("!contents"));
    }

    @Test
    public void tocPageShouldHaveChildren() {
        WikiPage page = root.getChildPage("StoryFiles");
        List<WikiPage> children = page.getChildren();
        assertThat(children, not(nullValue()));
        assertThat(children.size(), is(1));
        assertThat(children.get(0).getName(), is("SimpleStory"));
    }

    @Test
    public void tocPageCanRender() {
        WikiPage page = root.getChildPage("StoryFiles");
        assertThat(page.getHtml(), not(nullValue()));
    }

    @Test
    public void canResolveVariablesDefinedInAParentPage() {
        WikiPage stories = root.getChildPage("StoryFiles");
        WikiPage simpleStory = stories.getChildPage("SimpleStory");
        assertThat(stories.getVariable("TEST_SYSTEM"), is("jbehave"));
        assertThat(simpleStory.getVariable("TEST_SYSTEM"), is("jbehave"));
    }

    @Test
    public void shouldLoadSymlinkedStoriesFolder() {
        WikiPage symlinked = root.getChildPage("SymLinked");
        WikiPage page = symlinked.getChildPage("StoryFiles");
        List<WikiPage> children = page.getChildren();

        assertThat(page, instanceOf(SymbolicPage.class));
        assertThat(((SymbolicPage) page).getRealPage(), instanceOf(JBehaveTocPage.class));

        assertThat(children, not(nullValue()));
        assertThat(children.size(), is(1));
        assertThat(children.get(0).getName(), is("SimplestorY"));
    }
}
