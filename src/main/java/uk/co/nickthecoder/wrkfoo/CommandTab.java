package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.Task;

public class CommandTab
{
    CommandTabbedPane tabbedPane;

    private MainWindow mainWindow;

    private Command<?> command;

    private History history;

    private JPanel panel;

    public CommandTab(MainWindow mainWindow, Command<?> command)
    {
        this.mainWindow = mainWindow;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        history = new History();
        attach(command);
    }

    public String getTitle()
    {
        return command.getShortTitle();
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public MainWindow getMainWindow()
    {
        return mainWindow;
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

    private final void attach(Command<?> command)
    {
        if (this.command != null) {
            this.command.detach();
        }

        this.command = command;
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

    public void undo()
    {
        if (history.canUndo()) {
            go(history.undo(), false);
        }
    }

    public void redo()
    {
        if (history.canRedo()) {
            go(history.redo(), false);
        }
    }

    public void go(Command<?> newCommand)
    {
        go(newCommand, true);
    }

    private void go(Command<?> newCommand, boolean updateHistory)
    {
        if (newCommand != this.command) {
            attach(newCommand);
            // setIcon( newCommand.getIcon() );
        }

        if (updateHistory) {
            history.add(command);
        }

        if (getCommand().getCommandPanel().check()) {
            // All parameters are ok, run the command.

            getTask().run();
            newCommand.updateResults();

        } else {
            // Missing/incorrect parameters. Show the parameters panel.
            getCommand().getCommandPanel().getSplitPane().toggle(true);
        }

        if (tabbedPane != null) {
            tabbedPane.updateTabInfo(this);
        }

        this.panel.repaint();
    }
}
