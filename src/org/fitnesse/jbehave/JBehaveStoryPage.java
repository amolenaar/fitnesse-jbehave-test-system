package org.fitnesse.jbehave;

import fitnesse.wiki.*;
import fitnesse.wikitext.parser.VariableSource;
import util.FileUtil;
import util.Maybe;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class JBehaveStoryPage implements WikiPage {
    private final File path;
    private final String name;
    private final WikiPage parent;
    private final VariableSource variableSource;
    private String content;

    public JBehaveStoryPage(File path, String name, WikiPage parent, VariableSource variableSource) {
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
        return false;
    }

    @Override
    public WikiPage addChildPage(String name) {
        return null;
    }

    @Override
    public boolean hasChildPage(String name) {
        return false;
    }

    @Override
    public WikiPage getChildPage(String name) {
        return null;
    }

    @Override
    public void removeChildPage(String name) {

    }

    @Override
    public List<WikiPage> getChildren() {
        return emptyList();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PageData getData() {
        return new PageData(readContent(), wikiPageProperties());
    }

    private WikiPageProperties wikiPageProperties() {
        WikiPageProperties properties = new WikiPageProperties();
        properties.set(PageType.TEST.toString());
        return properties;
    }


    @Override
    public Collection<VersionInfo> getVersions() {
        return emptyList();
    }

    @Override
    public WikiPage getVersion(String versionName) {
        return null;
    }

    @Override
    public String getHtml() {
        return WikiPageUtil.makeHtml(this, variableSource);
    }

    private String readContent() {
        if (content == null) {
            try {
                content = FileUtil.getFileContent(path);
            } catch (IOException e) {
                content = String.format("<p class='error'>Unable to read story file %s: %s</p>", path, e.getMessage());
            }
        }
        return content;
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
