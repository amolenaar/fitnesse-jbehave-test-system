package org.fitnesse.jbehave;

import fitnesse.components.TraversalListener;
import fitnesse.testrunner.WikiTestPage;
import fitnesse.wiki.PageData;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.parser.*;

import java.util.ArrayList;
import java.util.List;

public class StepsBuilder {

    private VariableSource variableSource;

    public StepsBuilder(VariableSource variableSource) {
        this.variableSource = variableSource;
    }

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
        List<String> itemsOnThisPage = getItemsFromPage(itemPage);
        items.addAll(itemsOnThisPage);
    }

    protected List<String> getItemsFromPage(WikiPage page) {
        PageData data = page.getData();
        ParsedPage parsedPage = new ParsedPage(new ParsingPage(new WikiSourcePage(page), variableSource), data.getContent());
        return new Steps(new HtmlTranslator(new WikiSourcePage(page), parsedPage.getParsingPage())).getSteps(parsedPage.getSyntaxTree());
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
