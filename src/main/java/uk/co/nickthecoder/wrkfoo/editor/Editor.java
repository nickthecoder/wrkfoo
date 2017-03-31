package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.Container;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JToolBar;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabListener;
import uk.co.nickthecoder.wrkfoo.TabNotifier;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.TopLevel;
import uk.co.nickthecoder.wrkfoo.WrkFoo;
import uk.co.nickthecoder.wrkfoo.editor.Editor.EditorTask;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class Editor extends AbstractUnthreadedTool<EditorPanel, EditorTask>
    implements EditorListener, TabListener
{
    EditorPanel editorPanel;

    public static final Icon icon = Resources.icon("textEditor.png");

    private int jumpToLine = -1;

    private String highlightRegex = null;

    public Editor()
    {
        super(new EditorTask());
        editorPanel = new EditorPanel(this);
        editorPanel.addEditorListener(this);

        ActionBuilder builder = new ActionBuilder(this).component(editorPanel.getComponent());
        builder.name("documentOpen").buildShortcut();

        TabNotifier.addTabListener(this);
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
        if (task.file.getValue() == null) {
            return "New";
        }
        return task.file.getValue().getName();
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

    @Override
    public void attachedTab(ToolTab tab)
    {
        // Do nothing.
    }

    @Override
    public void detachingTab(ToolTab tab)
    {
        if (tab.getTool() == this) {
            TabNotifier.removeTabListener(this);
        }
    }

    @Override
    public void selectedTab(ToolTab tab)
    {
        if (tab.getTool() == this) {
            Util.assertIsEDT();

            JToolBar tb = editorPanel.toolBar;
            FindToolBar ftb = editorPanel.findToolBar;

            TopLevel topLevel = getToolPanel().getTopLevel();

            WrkFoo.assertIsEDT();

            if (tb.getParent() == null) {
                topLevel.addToolBar(tb);
            }
            if (ftb.getParent() == null) {
                topLevel.addStatusBar(ftb);
            }
        }
    }

    @Override
    public void deselectingTab(ToolTab tab)
    {
        if (tab.getTool() == this) {

            JToolBar tb = editorPanel.toolBar;
            FindToolBar ftb = editorPanel.findToolBar;

            Container parent = tb.getParent();
            if (parent != null) {
                parent.remove(tb);
                parent.repaint();
            }
            parent = ftb.getParent();
            if (parent != null) {
                parent.remove(ftb);
                parent.repaint();
            }

            editorPanel.replaceDialog.setVisible(false);
            editorPanel.replaceDialog.dispose();
        }
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

    private boolean wasDirty = false;

    public void checkDirty()
    {
        if (editorPanel.getEditorPane().isDirty() != wasDirty) {
            wasDirty = editorPanel.getEditorPane().isDirty();
            getToolTab().getTabbedPane().updateTabInfo(getToolTab());
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

        ToolTab newTab = topLevel.insertTab(newEditor, true);
        newTab.select();
    }

}
