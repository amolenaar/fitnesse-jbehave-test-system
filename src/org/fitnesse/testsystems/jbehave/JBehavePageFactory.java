package org.fitnesse.testsystems.jbehave;

import fitnesse.wiki.SystemVariableSource;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageFactory;
import fitnesse.wikitext.parser.VariableSource;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class JBehavePageFactory implements WikiPageFactory {
    private static final Logger LOG = Logger.getLogger(JBehavePageFactory.class.getName());
    private static final String STORY_EXTENSION = ".story";

    private final VariableSource variableSource;

    public JBehavePageFactory(Properties properties) {
        this(new SystemVariableSource(properties));
    }

    public JBehavePageFactory(VariableSource variableSource) {
        this.variableSource = variableSource;
    }

    @Override
    public WikiPage makePage(File path, String pageName, WikiPage parent) {
        return new JBehaveTocPage(path, pageName, parent, variableSource);
    }

    @Override
    public boolean supports(File path) {
        LOG.fine("Is " + path + " a JBehave story folder?");
        if (path.isDirectory()) {
            for (String child : path.list()) {
                if (isStoryFile(new File(path, child))) return true;
            }
        }
        return false;
    }

    static boolean isStoryFile(File path) {
        LOG.fine("Is " + path + " a JBehave story file? " + (path.getName().endsWith(STORY_EXTENSION)));
        return (path.getName().endsWith(STORY_EXTENSION));
    }
}
