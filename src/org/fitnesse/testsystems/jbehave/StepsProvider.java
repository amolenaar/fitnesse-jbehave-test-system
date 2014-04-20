package org.fitnesse.testsystems.jbehave;

import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.Translator;

import java.util.Collection;

public interface StepsProvider {
    Collection<String> provideSteps(Translator translator, Symbol symbol);
}
