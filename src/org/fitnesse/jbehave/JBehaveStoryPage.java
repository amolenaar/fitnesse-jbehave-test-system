package org.fitnesse.jbehave;

import fitnesse.wiki.*;
import fitnesse.wikitext.parser.VariableSource;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;

public class JBehaveStoryPage extends BaseWikiPage {
    private final File path;
    private String content;

    public JBehaveStoryPage(File path, String name, WikiPage parent, VariableSource variableSource) {
        super(name, parent, variableSource);
        this.path = path;
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
        // no-op
    }

    @Override
    public List<WikiPage> getChildren() {
        return emptyList();
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

}
