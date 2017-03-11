package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.tool.Editor.EditorTask;

public class Editor extends AbstractTool<EditorTask>
{
    private EditorPanel editorPanel;

    public static final Icon icon = Resources.icon("textEditor.png");

    public Editor()
    {
        super(new EditorTask());
    }

    public Icon getIcon()
    {
        return icon;
    }

    public Editor(File file)
    {
        this();
        task.file.setDefaultValue(file);
    }
    
    @Override
    public String getTitle()
    {
        return task.file.getValue().getName();
    }

    @Override
    public String getLongTitle()
    {
        return task.file.getValue().getPath();
    }

    @Override
    public EditorTask getTask()
    {
        return task;
    }

    @Override
    public void updateResults()
    {
        editorPanel.load(task.file.getValue());
    }

    @Override
    public ResultsPanel createResultsComponent()
    {
        if (editorPanel == null) {
            editorPanel = new EditorPanel(this);
        }
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
                    boolean show = getToolTab().getTabbedPane().getCurrentTab() == getToolTab();
                    setToolBarVisible(show);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if (getToolTab() == null) {
            System.out.println("No tool tab");
        }
        if (getToolTab().getTabbedPane() == null) {
            System.out.println("No tabbed pane");
        }

        getToolTab().getTabbedPane().addChangeListener(tabbedPaneListener);

        createResultsComponent();
        MainWindow mainWindow = MainWindow.getMainWindow(getToolPanel());
        mainWindow.getToolbar().add(editorPanel.toolBar, 0);
    }

    public void setToolBarVisible(boolean show)
    {
        MainWindow mainWindow = MainWindow.getMainWindow(getToolPanel());
        JToolBar toolBar = editorPanel.toolBar;
        if (show) {
            if (toolBar.getParent() == null) {
                mainWindow.getToolbar().add(toolBar, 0);
                mainWindow.getToolbar().repaint();
            }
        } else {
            if ((toolBar.getParent() != null) && (mainWindow != null)) {
                mainWindow.getToolbar().remove(toolBar);
                mainWindow.getToolbar().repaint();
            }
        }
    }

    @Override
    public void detach()
    {
        setToolBarVisible(false);
        if (getToolTab() == null) {
            System.out.println("Editor.detach. No tool tab");
        }
        if (getToolTab().getTabbedPane() == null) {
            System.out.println("itor.detach. No tabbed pane");
        }
        getToolTab().getTabbedPane().removeChangeListener(tabbedPaneListener);
        super.detach();
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

}
