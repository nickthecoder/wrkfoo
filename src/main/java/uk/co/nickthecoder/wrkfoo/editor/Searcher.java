package uk.co.nickthecoder.wrkfoo.editor;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

public class Searcher
{
    public SearchContext context;

    private RSyntaxTextArea textEditorPane;

    private SearchResult searchResult;

    private int currentMatchNumber;

    private List<SearcherListener> listeners = new ArrayList<>();

    public Searcher(RSyntaxTextArea tep)
    {
        this.textEditorPane = tep;
        context = new SearchContext();
    }

    public void addSearcherListener(SearcherListener sl)
    {
        listeners.add(sl);
    }

    public void removeSearcherListener(SearcherListener sl)
    {
        listeners.remove(sl);
    }

    public void setSearchContext(SearchContext value)
    {
        this.context = value;
    }

    public SearchContext getSearchContext()
    {
        return this.context;
    }

    public void onFindPrev()
    {
        context.setSearchForward(false);
        if (searchResult.getMarkedCount() > 0) {
            if (currentMatchNumber == 1) {
                textEditorPane.setCaretPosition(textEditorPane.getDocument().getLength());
            }
            performSearch();
        }
    }

    public void onFindNext()
    {
        context.setSearchForward(true);
        if (searchResult.getMarkedCount() > 0) {
            if (currentMatchNumber == searchResult.getMarkedCount()) {
                textEditorPane.setCaretPosition(0);
            }

            performSearch();
        }
    }

    private String searchText = "";

    public void setSearchText(String text)
    {
        searchText = text;
        performSearch();

        if ((currentMatchNumber == 0) && (searchResult.getMarkedCount() > 0)) {
            context.setSearchForward(true);
            textEditorPane.setCaretPosition(0);
            performSearch();
        }

    }

    public void performSearch()
    {
        context.setSearchFor(searchText);
        SearchEngine.markAll(textEditorPane, context);

        searchResult = SearchEngine.find(textEditorPane, context);

        currentMatchNumber = getMatchNumber();

        fire();
    }

    private void fire()
    {
        for (SearcherListener sl : listeners) {
            sl.searched();
        }
    }

    // This seams to be the easiest way to find which match number we are on. Grr.
    private int getMatchNumber()
    {
        RSyntaxTextAreaHighlighter highlighter = (RSyntaxTextAreaHighlighter) textEditorPane.getHighlighter();

        if (searchResult.getMarkedCount() > 0) {
            DocumentRange currentRange = searchResult.getMatchRange();

            List<DocumentRange> ranges = highlighter.getMarkAllHighlightRanges();
            int i = 0;
            if (currentRange != null) {
                for (DocumentRange range : ranges) {
                    if (range.getStartOffset() == currentRange.getStartOffset()) {
                        return i + 1;
                    }
                    i++;
                }
            }
        }
        return 0;
    }

    public int getCurrentMatchNumber()
    {
        return currentMatchNumber;
    }

    public int getMatchCount()
    {
        return searchResult.getMarkedCount();
    }
}
