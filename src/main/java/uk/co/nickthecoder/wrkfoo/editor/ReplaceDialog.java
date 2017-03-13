package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.Frame;

import javax.swing.JDialog;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.SearchContext;

public class ReplaceDialog extends JDialog
{
    private Searcher searcher;
    
    public ReplaceDialog( Frame parent, Searcher searcher)
    {
        this.searcher = searcher;
    }
    
}
