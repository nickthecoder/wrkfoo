package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.WrkFoo;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class EditorPanel extends ResultsPanel implements ExceptionHandler, DocumentListener
{
    private static final long serialVersionUID = 1L;

    Editor editorTool;

    TextEditorPane editorPane;

    RTextScrollPane scrollPane;

    JToolBar toolBar;

    FindToolBar findToolBar;

    JToggleButton findButton;

    Searcher searcher;

    ReplaceDialog replaceDialog;

    List<EditorListener> listeners = new ArrayList<>();

    public EditorPanel(Editor editorTool)
    {
        this.editorTool = editorTool;

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
        editorPane.getDocument().addDocumentListener(this);
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
        toolBar.add(builder.name("documentRevert").tooltip("Revert (F5)").buildButton());
        toolBar.addSeparator();
        toolBar.add(builder.name("editUndo").tooltip("Undo").shortcut("ctrl Z").buildButton());
        toolBar.add(builder.name("editRedo").tooltip("Redo").shortcut("ctrl shift Z").buildButton());
        toolBar.add(builder.name("editCopy").tooltip("Copy").shortcut("ctrl C").buildButton());
        toolBar.add(builder.name("editPaste").tooltip("Paste").shortcut("ctrl V").buildButton());

        toolBar.add(
            findButton = builder.name("editFind").tooltip("Search").shortcut("ctrl F").buildToggleButton());

        toolBar.add(builder.name("editReplace").tooltip("Find and Replace").shortcut("ctrl H").buildButton());
        toolBar.add(builder.name("editGoToLine").tooltip("Go to Line").shortcut("ctrl L").buildButton());

        findToolBar.rightPanel.add(builder.name("editReplace").label("Replace...").buildButton());
        builder.name("escape").shortcut("ESCAPE").buildShortcut();
    }

    private void initSearchDialogs()
    {
        findToolBar = new FindToolBar(searcher);
        findToolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        findToolBar.setVisible(false);

        Frame window = (Frame) SwingUtilities.getWindowAncestor(this);
        replaceDialog = new ReplaceDialog(window, searcher);
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
        MainWindow.getMainWindow(editorTool.getToolPanel()).handleException(e);
    }

    public void onDocumentSave() throws IOException
    {
        editorPane.save();
        fireChange();
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
        if (!findButton.isSelected()) {
            onCloseFind();
        } else {
            findToolBar.setVisible(true);
        }
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
        new GoToLineTask(editorPane).neverExit().promptTask();
    }

    public void onDocumentRevert()
    {
        WrkFoo.assertIsEDT();

        int result = JOptionPane.showConfirmDialog(this, "Revert file?", "Revert", JOptionPane.OK_CANCEL_OPTION);

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
        fireChange();

        editorPane.requestFocus();
    }

    public void onEscape()
    {
        findToolBar.setVisible(false);
        findButton.setSelected(false);
        replaceDialog.setVisible(false);
        editorPane.requestFocus();
    }

    public void addEditorListener(EditorListener listener)
    {
        listeners.add(listener);
    }

    public void removeEditorListener(EditorListener listener)
    {
        listeners.remove(listener);
    }

    private void fireChange()
    {
        for (EditorListener listener : listeners) {
            listener.documentChanged();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        fireChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        fireChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
    }

}
