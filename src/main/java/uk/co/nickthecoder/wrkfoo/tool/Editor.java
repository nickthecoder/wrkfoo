package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

    @Override
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
        return task.file.getValue().getName() + (editorPanel.editorPane.isDirty() ? " *" : "");
    }

    @Override
    public String getLongTitle()
    {
        return task.file.getValue().getName();
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
            editorPanel.editorPane.getDocument().addDocumentListener( new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e)
                {                  
                    checkDirty();
                }

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    checkDirty();
                }

                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    checkDirty();
                }
                
            });
        }
        return editorPanel;
    }

    private boolean wasDirty = false;
    
    public void checkDirty()
    {
        if ( editorPanel.editorPane.isDirty() != wasDirty ) {
            wasDirty = editorPanel.editorPane.isDirty();
            getToolTab().getTabbedPane().updateTabInfo(getToolTab());
        }
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

        getToolTab().getTabbedPane().addChangeListener(tabbedPaneListener);

        createResultsComponent();
        setToolBarVisible(true);
    }

    @Override
    public void detach()
    {
        setToolBarVisible(false);
        getToolTab().getTabbedPane().removeChangeListener(tabbedPaneListener);
        super.detach();
    }
    
    private void setToolBarVisible(boolean show)
    {
        JToolBar tb = editorPanel.toolBar;

        if (show) {
            if (tb.getParent() == null) {
                MainWindow mainWindow = MainWindow.getMainWindow(getToolPanel());
                mainWindow.getToolbarPanel().add(tb);
                mainWindow.getToolbarPanel().repaint();
            }

        } else {
            if (tb.getParent() != null) {
                MainWindow mainWindow = MainWindow.getMainWindow(getToolPanel());
                mainWindow.getToolbarPanel().remove(editorPanel.toolBar);
                mainWindow.getToolbarPanel().repaint();
            }
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

}
