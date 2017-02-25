package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;

public abstract class TaskCommand<T extends Task & Results<R>, R> implements Command<R>
{
    public T task;

    private TaskHistory history;

    public TaskCommand(T task)
    {
        this.task = task;
        history = new TaskHistory();
    }

    public T getTask()
    {
        return task;
    }

    @Override
    public String getTitle()
    {
        return task.getName();
    }

    @Override
    public GroupParameter getParameters()
    {
        return task.getParameters();
    }

    private CommandPanel<R> commandPanel;

    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;

    }

    public CommandPanel<R> getCommandPanel()
    {
        if (commandPanel == null) {
            commandPanel = new CommandPanel<R>(this);
            setupShortcuts();
        }
        return commandPanel;
    }

    private void setupShortcuts()
    {
        commandPanel.putAction("alt LEFT", "Action.undo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                undo();
            }
        });
        commandPanel.putAction("alt RIGHT", "Action.redo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                redo();
            }
        });
    }

    @Override
    public void go()
    {
        getCommandPanel().stopEditing();
        history.add(task);
        task.run();
    }

    public Results<R> getResults()
    {
        return task;
    }

    public void undo()
    {
        if (history.canUndo()) {
            history.undo();
            getCommandPanel().stopEditing();
            task.run();
        }
    }

    public void redo()
    {
        if (history.canRedo()) {
            history.redo();
            getCommandPanel().stopEditing();
            task.run();
        }
    }

}
