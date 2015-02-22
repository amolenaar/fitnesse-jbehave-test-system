package org.fitnesse.jbehave;

import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.testrunner.TestSystemFactoryRegistry;
import fitnesse.wiki.WikiPageFactoryRegistry;
import fitnesse.wikitext.parser.SymbolProvider;

import java.util.logging.Logger;

public class Plugin extends PluginFeatureFactoryBase {
    private static final Logger LOG = Logger.getLogger(Plugin.class.getName());

    @Override
    public void registerSymbolTypes(SymbolProvider symbolProvider) {
        symbolProvider.add(new StepsSymbolType());
    }

    @Override
    public void registerWikiPageFactories(WikiPageFactoryRegistry wikiPageFactoryRegistry) {
        wikiPageFactoryRegistry.registerWikiPageFactory(new JBehavePageFactory());
    }

    @Override
    public void registerTestSystemFactories(TestSystemFactoryRegistry testSystemFactoryRegistry) {
        testSystemFactoryRegistry.registerTestSystemFactory("jbehave", new JBehaveTestSystemFactory());
        LOG.info("Registered JBehave test system");
    }
}
