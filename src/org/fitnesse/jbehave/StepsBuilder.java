package org.fitnesse.jbehave;

import fitnesse.components.TraversalListener;
import fitnesse.testrunner.WikiTestPage;
import fitnesse.wiki.BaseWikiPage;
import fitnesse.wiki.PageData;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikitextPage;
import fitnesse.wikitext.parser.*;

import java.util.ArrayList;
import java.util.List;

public class StepsBuilder {

    public List<String> getSteps(WikiTestPage page) {
        final List<String> items = new ArrayList<String>();

        page.getSourcePage().getPageCrawler().traversePageAndAncestors(new TraversalListener<WikiPage>() {
            @Override
            public void process(WikiPage p) {
                addItemsFromPage(p, items);
            }
        });
        return items;
    }

    private void addItemsFromPage(WikiPage itemPage, List<String> items) {
        if (itemPage instanceof WikitextPage) {
            List < String > itemsOnThisPage = getItemsFromPage((WikitextPage) itemPage);
            items.addAll(itemsOnThisPage);
        }
    }

    protected List<String> getItemsFromPage(WikitextPage wikitext) {
        return new Steps(new HtmlTranslator(wikitext.getParsingPage().getPage(), wikitext.getParsingPage())).getSteps(wikitext.getSyntaxTree());
    }

    private static class Steps {
        private Translator translator;

        public Steps(Translator translator) {
            this.translator = translator;
        }

        public List<String> getSteps(Symbol syntaxTree) {
            TreeWalker walker = new TreeWalker();
            syntaxTree.walkPostOrder(walker);
            return walker.result;
        }

        private class TreeWalker implements SymbolTreeWalker {
            public List<String> result = new ArrayList<String>();

            public boolean visit(Symbol node) {
                if (node.getType() instanceof StepsProvider) {
                    result.addAll(((StepsProvider) node.getType()).provideSteps(translator, node));
                }
                return true;
            }

            public boolean visitChildren(Symbol node) { return true; }
        }
    }


}
