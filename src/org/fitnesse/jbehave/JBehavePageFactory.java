package org.fitnesse.jbehave;

import fitnesse.wiki.SystemVariableSource;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageFactory;
import fitnesse.wikitext.parser.VariableSource;

import java.io.File;
import java.util.Properties;

public class JBehavePageFactory implements WikiPageFactory {
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
        if (path.isDirectory()) {
            for (String child : path.list()) {
                if (isStoryFile(new File(path, child))) return true;
            }
        }
        return false;
    }

    static boolean isStoryFile(File path) {
        return (path.getName().endsWith(STORY_EXTENSION));
    }
}
