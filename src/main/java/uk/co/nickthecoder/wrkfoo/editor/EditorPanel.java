package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class EditorPanel extends ResultsPanel implements ExceptionHandler
{
    private static final long serialVersionUID = 1L;

    private Editor editorTask;

    private TextEditorPane editorPane;

    private RTextScrollPane scrollPane;

    JToolBar toolBar;

    FindToolBar findToolBar;

    JToggleButton findButton;

    Searcher searcher;

    public EditorPanel(Editor editorTask)
    {
        this.editorTask = editorTask;

        this.setLayout(new BorderLayout());

        editorPane = new TextEditorPane();

        ErrorStrip errorStrip = new ErrorStrip(editorPane);
        this.add(errorStrip, BorderLayout.EAST);

        scrollPane = new RTextScrollPane(editorPane);
        this.add(scrollPane, BorderLayout.CENTER);
        searcher = new Searcher(editorPane);

        toolBar = new JToolBar();

        initSearchDialogs();
        populateToolBar();
    }

    public TextEditorPane getEditorPane()
    {
        return editorPane;
    }

    private void populateToolBar()
    {
        ActionBuilder builder = new ActionBuilder(this);

        toolBar.add(builder.name("documentSave").tooltip("Save Document").shortcut("ctrl S").buildButton());
        // toolBar.add(builder.name("documentSaveAs").tooltip("Save As…").shortcut("ctrl S").buildButton());
        toolBar.add(builder.name("documentRevert").tooltip("Revert").shortcut("ctrl F5").buildButton());
        toolBar.addSeparator();
        toolBar.add(builder.name("editUndo").tooltip("Undo").shortcut("ctrl Z").buildButton());
        toolBar.add(builder.name("editRedo").tooltip("Redo").shortcut("ctrl shift Z").buildButton());
        toolBar.add(builder.name("editCopy").tooltip("Copy").shortcut("ctrl C").buildButton());
        toolBar.add(builder.name("editPaste").tooltip("Paste").shortcut("ctrl V").buildButton());

        toolBar.add(
            findButton = builder.name("editFind").tooltip("Search").shortcut("ctrl F").buildToggleButton());

        toolBar.add(builder.name("editReplace").tooltip("Find and Replace").shortcut("ctrl H").buildButton());
        toolBar.add(builder.name("editGoToLine").tooltip("Go to Line").shortcut("ctrl L").buildButton());

        builder.name("escape").shortcut("ESCAPE").buildShortcut();
    }

    public void load(File file)
    {
        try {
            editorPane.load(FileLocation.create(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleException(Throwable e)
    {
        MainWindow.getMainWindow(editorTask.getToolPanel()).handleException(e);
    }

    public void onDocumentSave() throws IOException
    {
        editorPane.save();
        editorTask.checkDirty();
    }

    public void onDocumentRevert() throws IOException
    {
        editorPane.reload();
    }

    public void onEditUndo()
    {
        editorPane.undoLastAction();
    }

    public void onEditRedo()
    {
        editorPane.redoLastAction();
    }

    public void onEditCopy()
    {
        editorPane.copy();
    }

    public void onEditPaste()
    {
        editorPane.paste();
    }

    public void onCloseFind()
    {
        findButton.setSelected(false);
        findToolBar.setVisible(false);
        // TODO Remove highlights
    }

    public void onEditFind()
    {
        replaceDialog.setVisible(false);
        if (!findButton.isSelected()) {
            onCloseFind();
        } else {
            findToolBar.setVisible(true);
        }
    }

    public void onEditReplace()
    {
        onCloseFind();
        replaceDialog.setVisible(true);
    }

    public void onEditGoToLine()
    {
        /*
         * Frame frame = (Frame) SwingUtilities.getWindowAncestor(EditorPanel.this);
         * GoToDialog dialog = new GoToDialog(frame);
         * dialog.setMaxLineNumberAllowed(editorPane.getLineCount());
         * dialog.setVisible(true);
         * int line = dialog.getLineNumber();
         * if (line > 0) {
         * try {
         * editorPane.setCaretPosition(editorPane.getLineStartOffset(line - 1));
         * } catch (BadLocationException ble) { // Never happens
         * UIManager.getLookAndFeel().provideErrorFeedback(editorPane);
         * ble.printStackTrace();
         * }
         * }
         */
    }

    public void onEscape()
    {
        findToolBar.setVisible(false);
        findButton.setSelected(false);
        replaceDialog.setVisible(false);
    }

    private ReplaceDialog replaceDialog;

    /**
     * Creates our Find and Replace dialogs.
     */
    public void initSearchDialogs()
    {
        findToolBar = new FindToolBar(searcher);
        findToolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        findToolBar.setVisible(false);

        Frame window = (Frame) SwingUtilities.getWindowAncestor(this);
        replaceDialog = new ReplaceDialog(window, searcher);

        // SearchContext context = findToolBar.getSearchContext();
        // replaceDialog.setSearchContext(context);
    }

}
