package uk.co.nickthecoder.wrkfoo.editor;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JToolBar;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabNotifier;
import uk.co.nickthecoder.wrkfoo.Tab;
import uk.co.nickthecoder.wrkfoo.TopLevel;
import uk.co.nickthecoder.wrkfoo.editor.Editor.EditorTask;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class Editor extends AbstractUnthreadedTool<EditorPanel, EditorTask>
    implements EditorListener
{
    EditorPanel editorPanel;

    public static final Icon icon = Resources.icon("textEditor.png");

    private int jumpToLine = -1;

    private String highlightRegex = null;

    private boolean wasDirty = false;

    public Editor()
    {
        super(new EditorTask());
        editorPanel = new EditorPanel(this);
        editorPanel.addEditorListener(this);

        ActionBuilder builder = new ActionBuilder(this).component(editorPanel.getComponent());
        builder.name("documentOpen").buildShortcut();

        JToolBar tb = editorPanel.toolBar;
        FindToolBar ftb = editorPanel.findToolBar;

        getToolPanel().getToolBar().addToolBar(tb);
        getToolPanel().getToolBar().addStatusBar(ftb);
    }

    public Editor(File file)
    {
        this();
        task.file.setDefaultValue(file);
    }

    public Editor(File file, int lineNumber)
    {
        this(file);
        jumpToLine = lineNumber;
    }

    public Editor(File file, String regex)
    {
        this(file);
        highlightRegex = regex;
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public String getTitle()
    {
        if (task.file.getValue() == null) {
            return "New";
        }
        return task.file.getValue().getName() + (editorPanel.getEditorPane().isDirty() ? " *" : "");
    }

    @Override
    public String getLongTitle()
    {
        String text;
        if (task.file.getValue() == null) {
            text = "New";
        } else {
            text = task.file.getValue().getName();
        }

        if (editorPanel.getEditorPane().isDirty()) {
            text += " *";
        }
        return text;
    }

    private boolean firstTime = true;

    @Override
    public void updateResults()
    {
        if (firstTime) {
            editorPanel.load(task.file.getValue());
            if (jumpToLine > 0) {
                editorPanel.goToLine(jumpToLine);
            }
            if (highlightRegex != null) {
                editorPanel.find(highlightRegex, true);
            }
            firstTime = false;
        } else {
            editorPanel.onDocumentRevert();
        }
    }

    @Override
    public EditorPanel createResultsPanel()
    {
        return editorPanel;
    }

    public static class EditorTask extends Task
    {
        public FileParameter file = new FileParameter.Builder("file").file().mayExist()
            .parameter();

        public EditorTask()
        {
            addParameters(file);
        }

        @Override
        public void body()
        {
        }
    }

    public void checkDirty()
    {
        if (editorPanel.getEditorPane().isDirty() != wasDirty) {
            wasDirty = editorPanel.getEditorPane().isDirty();
            TabNotifier.fireChangedTitle(getTab());
        }
    }

    @Override
    public void documentChanged()
    {
        checkDirty();
    }

    public void onDocumentOpen()
    {
        Editor newEditor = new Editor();
        newEditor.task.file.setDefaultValue(this.task.file.getValue().getParentFile());

        TopLevel topLevel = this.getToolPanel().getTopLevel();

        Tab newTab = topLevel.insertTab(newEditor, true);
        newTab.select();
    }

}
