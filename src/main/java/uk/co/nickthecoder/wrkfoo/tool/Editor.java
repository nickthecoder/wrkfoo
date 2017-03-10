package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.JToolBar;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.tool.Editor.EditorTask;

public class Editor extends AbstractTool<EditorTask>
{
    private EditorPanel editorPanel;

    public Editor()
    {
        super(new EditorTask());
    }

    public Editor(File file)
    {
        this();
        task.file.setDefaultValue(file);
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

    public void attachToolBar(JToolBar toolBar)
    {
        getToolTab().getMainWindow().getToolbar().add(toolBar, 0);
        toolBar.addSeparator();
    }

    @Override
    public void attachTo(ToolTab tab)
    {   
        super.attachTo(tab);
        createResultsComponent();
        getToolTab().getMainWindow().getToolbar().add(editorPanel.toolBar, 0);
        editorPanel.toolBar.addSeparator();
    }

    @Override
    public void detach()
    {
        editorPanel.toolBar.getParent().remove(editorPanel.toolBar);
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
