package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class EditorPanel extends ResultsPanel implements ExceptionHandler
{
    private Editor editorTask;

    private TextEditorPane editorPane;

    private RTextScrollPane scrollPane;

    private JToolBar toolBar;

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
        editorTask.attachToolBar(toolBar);
    }

    private void populateToolBar()
    {
        ActionBuilder builder = new ActionBuilder(this);

        toolBar.add(builder.name("documentSave").tooltip("Save Document").shortcut("ctrl S").buildButton());

    }

    public void detach()
    {
        try {
            Container parent = toolBar.getParent();
            parent.remove(toolBar);
            parent.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(File file)
    {
        try {
            editorPane.load(FileLocation.create(file), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDocumentSave()
    {
        try {
            editorPane.save();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void handleException(Throwable e)
    {
        editorTask.getToolTab().getMainWindow().handleException(e);
    }

}
