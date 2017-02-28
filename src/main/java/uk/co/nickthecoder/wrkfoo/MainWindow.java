package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.nickthecoder.jguifier.util.AutoExit;
import uk.co.nickthecoder.wrkfoo.command.NullCommand;
import uk.co.nickthecoder.wrkfoo.command.WrkCommand;
import uk.co.nickthecoder.wrkfoo.util.ButtonBuilder;

public class MainWindow extends JFrame
{
    private static final CommandTab DUMMY_COMMAND_TAB = new CommandTab(new NullCommand());

    private List<CommandTab> commandTabs;

    private JTabbedPane tabbedPane;

    private JToolBar toolbar;

    public MainWindow(Command<?>... commands)
    {
        commandTabs = new ArrayList<CommandTab>();

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                tabChanged();
            }
        });

        toolbar = new JToolBar();
        fillToolbar();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(toolbar, BorderLayout.NORTH);

        setTitle("WrkFoo");

        boolean first = true;
        for (Command<?> command : commands) {

            if (first) {
                this.setTitle(command.getTitle());
            }

            addTab(command);
        }

        setLocationRelativeTo(null);
        pack();
    }

    public CommandTab getCurrentTab()
    {
        if (commandTabs.size() == 0) {
            return DUMMY_COMMAND_TAB;
        }
        return commandTabs.get(tabbedPane.getSelectedIndex());
    }

    private void fillToolbar()
    {
        ButtonBuilder builder = new ButtonBuilder(this);

        toolbar.add(builder.name("home").tooltip("Show all commands").build());
        toolbar.add(builder.name("newTab").tooltip("Open a new Ta").build());
        toolbar.add(builder.name("newWindow").tooltip("Open a new Window").build());
        toolbar.add(builder.name("back").tooltip("Go back to the previous command").build());
        toolbar.add(builder.name("forward").tooltip("Go forward to the next command").build());
    }

    private void addTab(Command<?> command)
    {
        CommandTab tab = new CommandTab(command);
        commandTabs.add(tab);

        JLabel label = new JLabel(command.getTitle());
        label.setIcon(command.getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        tabbedPane.addTab(null, tab.getPanel());
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, label);

        tab.postCreate();
        tab.go(command);
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        AutoExit.setVisible(show);
    }

    public void putAction(String keyStroke, String name, Action action)
    {
        putAction(keyStroke, name, this.getRootPane(), action);
    }

    public static void putAction(String keyStroke, String name, JComponent component, Action action)
    {
        putAction(keyStroke, name, component, JComponent.WHEN_IN_FOCUSED_WINDOW, action);
    }

    public static void putAction(String key, String name, JComponent component, int condition, Action action)
    {
        InputMap inputMap = component.getInputMap(condition);
        ActionMap actionMap = component.getActionMap();

        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        inputMap.put(keyStroke, name);
        actionMap.put(name, action);

        // Bodge! I don't want the table stealing any of MY keyboard shortcuts
        // TODO table.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, name);
    }

    private void tabChanged()
    {
        int i = tabbedPane.getSelectedIndex();
        if (i < 0) {
            setTitle("WrkFoo");
        } else {
            CommandTab tab = commandTabs.get(i);

            setTitle(tab.getTitle());

            getRootPane().setDefaultButton(tab.getCommand().getCommandPanel().getGoButton());
        }
    }

    public void onHome()
    {
        WrkCommand command = new WrkCommand();
        getCurrentTab().go(command);
    }

    public void onNewTab()
    {
        WrkCommand command = new WrkCommand();
        addTab(command);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void onNewWindow()
    {
        WrkCommand command = new WrkCommand();
        MainWindow newWindow = new MainWindow(command);
        command.go();
        newWindow.setVisible(true);
    }

    public void onBack()
    {
        getCurrentTab().undo();
    }

    public void onForward()
    {
        getCurrentTab().redo();
    }

}
