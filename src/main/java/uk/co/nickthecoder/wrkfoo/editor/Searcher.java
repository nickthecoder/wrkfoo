package uk.co.nickthecoder.wrkfoo.editor;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import uk.co.nickthecoder.jguifier.util.Util;

public class Searcher
{
    public SearchContext context;

    private RSyntaxTextArea textEditorPane;

    private SearchResult searchResult;

    private List<SearcherListener> listeners = new ArrayList<>();

    public Searcher(RSyntaxTextArea tep)
    {
        this.textEditorPane = tep;
        context = new SearchContext();
    }

    public RSyntaxTextArea getTextArea()
    {
        return textEditorPane;
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

    public void onGo()
    {
        onFindNext();
    }

    public void onFindPrev()
    {
        if (searchResult == null) {
            return;
        }

        context.setSearchForward(false);
        if (searchResult.getMarkedCount() > 0) {

            // Already on the first match, start from the end
            if (getCurrentMatchNumber() == 1) {
                textEditorPane.setCaretPosition(textEditorPane.getDocument().getLength());
            }

            performSearch();

            // Didn't find anything, lets wrap round and try again.
            if (getCurrentMatchNumber() == 0) {
                textEditorPane.setCaretPosition(textEditorPane.getDocument().getLength());
                performSearch();
            }
        }
    }

    public void onFindNext()
    {
        if (searchResult == null) {
            return;
        }

        context.setSearchForward(true);
        if (searchResult.getMarkedCount() > 0) {

            // Already on the last match, start from the beginning
            if (getCurrentMatchNumber() == searchResult.getMarkedCount()) {
                textEditorPane.setCaretPosition(0);
            }

            performSearch();

            // Didn't find anything, lets wrap round and try again.
            if (getCurrentMatchNumber() == 0) {
                textEditorPane.setCaretPosition(0);
                performSearch();
            }
        }
    }

    public void setSearchText(String text)
    {
        context.setSearchFor(text);
        markMatches();
    }

    public void markMatches()
    {
        searchResult = SearchEngine.markAll(textEditorPane, context);

        fire(SearcherEvent.Type.MARK, getFindResultsMessage());
    }

    public void performSearch()
    {
        searchResult = SearchEngine.find(textEditorPane, context);

        fire(SearcherEvent.Type.FIND, getFindResultsMessage());
    }

    public void replace(String searchString, String replace)
    {
        context.setReplaceWith(replace);
        context.setSearchFor(searchString);
        searchResult = SearchEngine.replace(textEditorPane, context);
    }

    public void replaceAll(String searchString, String replace)
    {
        context.setReplaceWith(replace);
        context.setSearchFor(searchString);
        searchResult = SearchEngine.replaceAll(textEditorPane, context);

        String message = "Found and replaced " + searchResult.getCount() + " occurrences";
        fire(SearcherEvent.Type.REPLACEALL, message);
    }

    private void fire(SearcherEvent.Type type, String string)
    {
        SearcherEvent event = new SearcherEvent(this, type, string);
        for (SearcherListener sl : listeners) {
            sl.searched(event);
        }
    }

    // This seams to be the easiest way to find which match number we are on. Grr.
    public int getCurrentMatchNumber()
    {
        RSyntaxTextAreaHighlighter highlighter = (RSyntaxTextAreaHighlighter) textEditorPane.getHighlighter();

        if (searchResult.getMarkedCount() > 0) {
            int caretPos = textEditorPane.getCaretPosition();

            List<DocumentRange> ranges = highlighter.getMarkAllHighlightRanges();
            int i = 1;
            for (DocumentRange range : ranges) {
                if (range.getEndOffset() == caretPos) {
                    return i;
                }
                i++;
            }
        }
        return 0;
    }

    public int getMatchCount()
    {
        return searchResult.getMarkedCount();
    }

    private String getFindResultsMessage()
    {
        int currentMatchNumber = getCurrentMatchNumber();
        int count = getMatchCount();

        if (Util.empty(context.getSearchFor())) {
            return "";
        } else {
            String prefix = currentMatchNumber > 0 ? "" + currentMatchNumber + " of " : "";
            String suffix = count > 0 ? "" + count + " matches" : "no matches";
            return prefix + suffix;
        }
    }

    public void clearMarks()
    {
        context.setSearchFor("");
        SearchEngine.markAll(textEditorPane, context);
    }
}
