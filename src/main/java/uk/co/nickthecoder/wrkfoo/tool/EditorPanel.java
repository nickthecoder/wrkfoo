package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class EditorPanel extends ResultsPanel implements ExceptionHandler
{
    private Editor editorTask;

    private TextEditorPane editorPane;

    private RTextScrollPane scrollPane;

    JToolBar toolBar;

    public EditorPanel(Editor editorTask)
    {
        this.editorTask = editorTask;

        this.setLayout(new BorderLayout());

        editorPane = new TextEditorPane();
        scrollPane = new RTextScrollPane(editorPane);
        this.add(scrollPane, BorderLayout.CENTER);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        populateToolBar();
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
        //toolBar.add(builder.name("editFind").tooltip("Find").shortcut("ctrl V").buildButton());
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

}
