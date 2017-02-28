package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.Task;

public class CommandTab
{    
    CommandTabbedPane tabbedPane;
    
    private Command<?> command;

    private History history;

    private JPanel panel;

    public CommandTab(Command<?> command)
    {
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
        if ( this.command != null ) {
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
            go( history.undo(), false );
        }
    }

    public void redo()
    {
        if (history.canRedo()) {
            go( history.redo(), false );
        }
    }

    
    public void go( Command<?> newCommand )
    {
        go( newCommand, true );
    }
    
    private void go( Command<?> newCommand, boolean updateHistory )
    {
        if (this.command != null) {
            this.command.getCommandPanel().stopEditing();
        }

        if ( newCommand != this.command ) {
            attach(newCommand);
            // setIcon( newCommand.getIcon() );
        }

        command.getCommandPanel().stopEditing();
        if (getTask().checkParameters()) {
            
            if (updateHistory) {
                history.add(command);
            }
            
            getTask().run();
            newCommand.updateResults();
        }

        if ( tabbedPane != null ) {
            tabbedPane.updateTabInfo( this );
        }
        
        this.panel.repaint();
    }
}
