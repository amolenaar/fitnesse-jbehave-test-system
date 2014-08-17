package org.fitnesse.jbehave;

import fitnesse.components.ComponentFactory;
import fitnesse.testrunner.TestSystemFactoryRegistry;
import fitnesse.wiki.WikiPageFactoryRegistry;
import fitnesse.wikitext.parser.SymbolProvider;

public class Plugin {

    private final ComponentFactory componentFactory;

    public Plugin(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    public void registerSymbolTypes(SymbolProvider symbolProvider) {
        symbolProvider.add(componentFactory.createComponent(StepsSymbolType.class));
    }

    public void registerWikiPageFactories(WikiPageFactoryRegistry wikiPageFactoryRegistry) {
        wikiPageFactoryRegistry.registerWikiPageFactory(componentFactory.createComponent(JBehavePageFactory.class));
    }

    public void registerTestSystemFactories(TestSystemFactoryRegistry testSystemFactoryRegistry) {
        testSystemFactoryRegistry.registerTestSystemFactory("jbehave", componentFactory.createComponent(JBehaveTestSystemFactory.class));
    }
}
