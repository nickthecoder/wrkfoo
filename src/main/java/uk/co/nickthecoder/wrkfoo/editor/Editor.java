package uk.co.nickthecoder.wrkfoo.editor;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JToolBar;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.HalfTab;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.Tab;
import uk.co.nickthecoder.wrkfoo.TabAdater;
import uk.co.nickthecoder.wrkfoo.TabNotifier;
import uk.co.nickthecoder.wrkfoo.TopLevel;
import uk.co.nickthecoder.wrkfoo.editor.Editor.EditorTask;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class Editor extends AbstractUnthreadedTool<EditorPanel, EditorTask> implements EditorListener
{
    EditorPanel editorPanel;

    public static final Icon icon = Resources.icon("textEditor.png");

    private int jumpToLine = -1;

    private String highlightRegex = null;

    private boolean firstTime = true;

    /**
     * Set by {@link #splitTool(boolean)} to indicate that the document should NOT be loaded,
     * as it is sharing the document with the original Editor's document.
     */
    private boolean ignoreUpdate;

    private Shared shared;

    public Editor()
    {
        super(new EditorTask());
        editorPanel = new EditorPanel(this);
        shared = new Shared();
        shared.listen(editorPanel.editorPane);
        TabNotifier.addTabListener(new TabAdater()
        {
            public void detaching(HalfTab halfTab)
            {
                shared.removeEditorListener(Editor.this);
                TabNotifier.removeTabListener(this);
            }
        });
        shared.addEditorListener(this);

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
        return task.file.getValue().getName() + (shared.isDirty() ? " *" : "");
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

        if (shared.isDirty()) {
            text += " *";
        }
        return text;
    }

    @Override
    public void updateResults()
    {
        if (ignoreUpdate) {
            ignoreUpdate = false;
        } else {
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

    public void onDocumentOpen()
    {
        Editor newEditor = new Editor();
        newEditor.task.file.setDefaultValue(this.task.file.getValue().getParentFile());

        TopLevel topLevel = this.getToolPanel().getTopLevel();

        Tab newTab = topLevel.insertTab(newEditor, true);
        newTab.select();
    }

    @Override
    public Editor splitTool(boolean vertical)
    {
        Editor result = new Editor();
        result.ignoreUpdate = true;
        result.task.file.setDefaultValue(this.task.file.getValue());
        result.editorPanel.editorPane.setDocument(this.editorPanel.editorPane.getDocument());
        result.shared.removeEditorListener(result);
        result.shared = this.shared;
        result.shared.listen(result.editorPanel.editorPane);
        result.shared.addEditorListener(this);

        return result;
    }

    @Override
    public void dirtyChanged()
    {
        TabNotifier.fireChangedTitle(getToolPanel().getHalfTab().getTab());
    }
}
