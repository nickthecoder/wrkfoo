package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.Task;

public class CommandTab
{
    private Command<?> command;

    private History history;

    private JPanel panel;

    public CommandTab(Command<?> command)
    {
        this.command = command;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        attach(command);
        history = new History();
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public void postCreate()
    {
        MainWindow.putAction("alt LEFT", "Action.undo", panel, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                undo();
            }
        });

        MainWindow.putAction("alt RIGHT", "Action.redo", panel, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                redo();
            }
        });
    }

    public final void attach(Command<?> command)
    {
        command.attachTo(this);
        panel.removeAll();
        panel.add(command.getCommandPanel());
    }

    public Command<?> getCommand()
    {
        return command;
    }

    public Task getTask()
    {
        return command.getTask();
    }

    public void go()
    {
        command.getCommandPanel().stopEditing();
        history.add(command);
        getTask().run();
    }

    public void undo()
    {
        if (history.canUndo()) {
            update( history.undo() );
        }
    }

    public void redo()
    {
        if (history.canRedo()) {
            update( history.redo() );
        }
    }

    public void update( Command<?> command )
    {
        command.getCommandPanel().stopEditing();

        if ( command != this.command ) {
            this.command.detach();
            command.attachTo(this);
        }

        command.getTask().run();
        command.updateResults();

    }
}
