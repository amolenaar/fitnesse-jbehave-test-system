package org.fitnesse.jbehave;

import fitnesse.wiki.*;
import fitnesse.wikitext.parser.VariableSource;
import fitnesse.wikitext.parser.WikiWordPath;
import util.Maybe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JBehaveTocPage implements WikiPage {
    private static final String CONTENTS = "!contents";

    private final File path;
    private final String name;
    private final WikiPage parent;
    private final VariableSource variableSource;

    public JBehaveTocPage(File path, String name, WikiPage parent, VariableSource variableSource) {
        this.path = path;
        this.name = name;
        this.parent = parent;
        this.variableSource = variableSource;
    }

    @Override
    public WikiPage getParent() {
        return parent;
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public WikiPage addChildPage(String name) {
        return null;
    }

    @Override
    public boolean hasChildPage(String name) {
        return getChildPage(name) != null;
    }

    @Override
    public WikiPage getChildPage(String name) {
        for (WikiPage child : getChildren()) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void removeChildPage(String name) {

    }

    @Override
    public List<WikiPage> getChildren() {
        List<WikiPage> children = new ArrayList<WikiPage>();
        for (String child : path.list()) {
            File childPath = new File(path, child);
            if (JBehavePageFactory.isStoryFile(childPath)) {
                children.add(new JBehaveStoryPage(childPath,
                        WikiWordPath.makeWikiWord(child.split("\\.", 2)[0]), this, variableSource));
            }
            // TODO: else: nested story directory?
        }
        return children;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PageData getData() {
        return new PageData(CONTENTS, wikiPageProperties());
    }

    private WikiPageProperties wikiPageProperties() {
        WikiPageProperties properties = new WikiPageProperties();
        properties.set(PageType.SUITE.toString());
        return properties;
    }

    @Override
    public Collection<VersionInfo> getVersions() {
        return null;
    }

    @Override
    public WikiPage getVersion(String versionName) {
        return null;
    }

    @Override
    public String getHtml() {
        return WikiPageUtil.makeHtml(this, getData());
    }

    @Override
    public VersionInfo commit(PageData data) {
        return null;
    }

    @Override
    public PageCrawler getPageCrawler() {
        return new PageCrawlerImpl(this);
    }

    @Override
    public String getVariable(String name) {
        Maybe<String> variable = variableSource.findVariable(name);
        if (variable.isNothing()) {
            return parent != null ? parent.getVariable(name) : null;
        }

        // TODO: substitute in context of current page
        return variable.getValue();
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
