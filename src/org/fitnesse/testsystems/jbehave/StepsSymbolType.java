package org.fitnesse.testsystems.jbehave;

import fitnesse.wikitext.parser.*;
import util.Maybe;

import java.util.Arrays;
import java.util.Collection;

public class StepsSymbolType extends SymbolType implements Rule, StepsProvider {

    public StepsSymbolType() {
        super("Steps");
        wikiMatcher(new Matcher().startLineOrCell().string("!steps"));
        wikiRule(this);
        htmlTranslation(new HtmlBuilder("span").body(0, "steps: ").attribute("class", "meta").inline());
    }

    public Collection<String> provideSteps(Translator translator, Symbol symbol) {
        return Arrays.asList(translator.translate(symbol.childAt(0)));
    }

    public Maybe<Symbol> parse(Symbol current, Parser parser) {
        if (!parser.isMoveNext(SymbolType.Whitespace)) return Symbol.nothing;

        return new Maybe(current.add(parser.parseToEnds(0, SymbolProvider.pathRuleProvider, new SymbolType[] {SymbolType.Newline})));
    }
}
