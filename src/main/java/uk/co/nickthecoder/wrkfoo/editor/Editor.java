package uk.co.nickthecoder.wrkfoo.editor;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.WrkFoo;
import uk.co.nickthecoder.wrkfoo.editor.Editor.EditorTask;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class Editor extends AbstractUnthreadedTool<EditorPanel, EditorTask>
    implements EditorListener
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

        ActionBuilder builder = new ActionBuilder(this).component(editorPanel);
        builder.name("documentOpen").buildShortcut();
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

    private ChangeListener tabbedPaneListener;

    @Override
    public void attachTo(ToolTab tab)
    {
        super.attachTo(tab);

        tabbedPaneListener = new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent ce)
            {
                try {
                    boolean show = getToolTab().getTabbedPane().getSelectedTab() == getToolTab();
                    activate(show);
                } catch (Exception e) {
                    editorPanel.handleException(e);
                }
            }
        };

        getToolTab().getTabbedPane().addChangeListener(tabbedPaneListener);

        createResultsPanel();
        activate(true);
    }

    @Override
    public void detach()
    {
        activate(false);
        getToolTab().getTabbedPane().removeChangeListener(tabbedPaneListener);
        super.detach();
    }

    private void activate(boolean show)
    {
        JToolBar tb = editorPanel.toolBar;
        FindToolBar ftb = editorPanel.findToolBar;

        MainWindow mainWindow = MainWindow.getMainWindow(getToolPanel());
        if (show) {
            WrkFoo.assertIsEDT();

            if (tb.getParent() == null) {
                mainWindow.getToolBarPanel().add(tb);
            }
            if (ftb.getParent() == null) {
                mainWindow.getStatusBarPanel().add(ftb, 0);
            }

        } else {

            if (tb.getParent() != null) {
                tb.getParent().remove(tb);
            }
            if (ftb.getParent() != null) {
                ftb.getParent().remove(ftb);
            }
            editorPanel.replaceDialog.setVisible(false);
            editorPanel.replaceDialog.dispose();

        }

        if (mainWindow != null) {
            mainWindow.getToolBarPanel().repaint();
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

        MainWindow mainWindow = MainWindow.getMainWindow(this.getToolPanel());
        ToolTab newTab = mainWindow.insertTab(newEditor, true);
        mainWindow.tabbedPane.setSelectedComponent(newTab.getPanel());
    }

}
