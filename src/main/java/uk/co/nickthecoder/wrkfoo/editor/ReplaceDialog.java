package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.Frame;

import javax.swing.JDialog;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.SearchContext;

public class ReplaceDialog extends JDialog
{
    private SearchContext context;

    public ReplaceDialog( Frame parent, TextEditorPane tep )
    {
        this.context = new SearchContext();
    }

    public void setSearchContext(SearchContext value)
    {
        this.context = value;
    }

    public SearchContext getSearchContext()
    {
        return this.context;
    }
    
}
