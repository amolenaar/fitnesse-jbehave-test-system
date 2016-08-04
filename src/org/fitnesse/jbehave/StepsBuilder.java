package org.fitnesse.jbehave;

import fitnesse.components.TraversalListener;
import fitnesse.testrunner.WikiTestPageUtil;
import fitnesse.testsystems.TestPage;
import fitnesse.wiki.BaseWikitextPage;
import fitnesse.wiki.SymbolicPage;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikitextPage;
import fitnesse.wikitext.parser.*;

import java.util.ArrayList;
import java.util.List;

public class StepsBuilder {

    public List<String> getSteps(TestPage page) {
        final List<String> items = new ArrayList<String>();

        WikiTestPageUtil.getSourcePage(page).getPageCrawler().traversePageAndAncestors(new TraversalListener<WikiPage>() {
            @Override
            public void process(WikiPage p) {
                addItemsFromPage(p, items);
            }
        });
        return items;
    }

    private void addItemsFromPage(WikiPage itemPage, List<String> items) {
        if (itemPage instanceof WikitextPage) {
            List<String> itemsOnThisPage = getItemsFromPage((WikitextPage) itemPage);
            items.addAll(itemsOnThisPage);
        }
    }

    protected List<String> getItemsFromPage(WikitextPage wikitext) {
        ParsingPage page = null;
        if (wikitext instanceof SymbolicPage) {
            WikiPage wikiPage = ((SymbolicPage) wikitext).getRealPage();
            if (wikiPage instanceof JBehaveStoryPage) {
                page = BaseWikitextPage.makeParsingPage((JBehaveStoryPage) wikiPage);
            } else
                return new ArrayList<>();
        } else
            page = wikitext.getParsingPage();
        return new Steps(new HtmlTranslator(page.getPage(), page)).getSteps(wikitext.getSyntaxTree());
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
