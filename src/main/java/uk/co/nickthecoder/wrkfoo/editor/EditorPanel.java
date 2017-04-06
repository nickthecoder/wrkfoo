package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import uk.co.nickthecoder.wrkfoo.Focuser;
import uk.co.nickthecoder.wrkfoo.PanelResults;
import uk.co.nickthecoder.wrkfoo.WrkFoo;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class EditorPanel extends PanelResults implements ExceptionHandler
{
    Editor editorTool;

    TextEditorPane editorPane;

    RTextScrollPane scrollPane;

    JToolBar toolBar;

    FindToolBar findToolBar;

    JToggleButton findButton;

    Searcher searcher;

    ReplaceDialog replaceDialog;

    public EditorPanel(Editor editorTool)
    {
        super(editorTool);
        this.editorTool = editorTool;

        editorPane = new TextEditorPane();
        editorPane.setTabsEmulated(true);
        editorPane.setTabSize(4);

        ErrorStrip errorStrip = new ErrorStrip(editorPane);
        getComponent().add(errorStrip, BorderLayout.EAST);

        scrollPane = new RTextScrollPane(editorPane);
        getComponent().add(scrollPane, BorderLayout.CENTER);
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
        // Should the condition be WHEN_IN_FOCUSED_WINDOW?
        // This allows the shortcuts to take affect when the focus is in the FindToolBar.
        // However, they will ALSO take affect when in other places, where it may not make sense.
        // Currently it is OK, but if we ever have two Tools visible in one MainWindow, then it may not be correct.
        ActionBuilder builder = new ActionBuilder(this).component(getComponent())
            .condition(JComponent.WHEN_IN_FOCUSED_WINDOW);

        toolBar.add(builder.name("documentSave").tooltip("Save Document").buildButton());
        // toolBar.add(builder.name("documentSaveAs").tooltip("Save As…").buildButton());
        toolBar.add(builder.name("documentRevert").tooltip("Revert (F5)").buildButton());
        toolBar.addSeparator();
        toolBar.add(builder.name("editUndo").tooltip("Undo").buildButton());
        toolBar.add(builder.name("editRedo").tooltip("Redo").buildButton());
        toolBar.add(builder.name("editCopy").tooltip("Copy").buildButton());
        toolBar.add(builder.name("editPaste").tooltip("Paste").buildButton());

        toolBar.add(findButton = builder.name("editFind").tooltip("Search").buildToggleButton());

        toolBar.add(builder.name("editReplace").tooltip("Find and Replace").buildButton());
        toolBar.add(builder.name("editGoToLine").tooltip("Go to Line").buildButton());

        findToolBar.rightPanel.add(builder.name("editReplace").label("Replace...").buildButton());

        builder.name("escape").buildShortcut();
    }

    private void initSearchDialogs()
    {
        findToolBar = new FindToolBar(searcher);
        findToolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        findToolBar.setVisible(false);

        Frame window = (Frame) SwingUtilities.getWindowAncestor(getComponent());
        replaceDialog = new ReplaceDialog(window, searcher);
    }

    @Override
    public JComponent getFocusComponent()
    {
        return editorPane;
    }

    public void load(File file)
    {
        try {
            editorPane.load(FileLocation.create(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToLine(int lineNumber)
    {
        try {
            editorPane.setCaretPosition(editorPane.getLineStartOffset(lineNumber + 1));
        } catch (Exception e) {
            // We must have asked for a line number too large, so just go to the end of the document
            editorPane.setCaretPosition(editorPane.getDocument().getLength());
        }
    }

    public void find(String search, boolean regex)
    {
        searcher.setSearchText(search);
        searcher.context.setRegularExpression(regex);
        searcher.markMatches();
        searcher.onFindNext();
    }

    @Override
    public void handleException(Throwable e)
    {
        editorTool.getToolPanel().getTopLevel().handleException(e);
    }

    public void onDocumentSave() throws IOException
    {
        editorPane.save();
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
        searcher.clearMarks();
    }

    public void onEditFind()
    {
        replaceDialog.setVisible(false);
        findToolBar.setVisible(true);
        Focuser.focusLater("EditorPanel.onEditFind. Find text field", findToolBar.getTextField(), 8);
    }

    public void onEditReplace()
    {
        onCloseFind();
        replaceDialog.pack();
        replaceDialog.setLocationRelativeTo(null);
        replaceDialog.setVisible(true);
    }

    public void onEditGoToLine()
    {
        new GoToLineTask(editorPane).promptTask();
    }

    public void onDocumentRevert()
    {
        WrkFoo.assertIsEDT();

        int result = JOptionPane.showConfirmDialog(getComponent(), "Revert file?", "Revert",
            JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            try {
                int line = editorPane.getCaretLineNumber();
                editorPane.reload();
                try {
                    editorPane.setCaretPosition(editorPane.getLineStartOffset(line));
                } catch (BadLocationException e) {
                    // Tried to got to a line number too high for the reverted document, so go to EOF
                    editorPane.setCaretPosition(editorPane.getDocument().getLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Focuser.focusLater("EditorPaned reverted", editorPane, 9);
    }

    public void onEscape()
    {
        findToolBar.setVisible(false);
        findButton.setSelected(false);
        replaceDialog.setVisible(false);
        Focuser.focusLater("Editor.onEscape", editorPane, 8);
    }
}
