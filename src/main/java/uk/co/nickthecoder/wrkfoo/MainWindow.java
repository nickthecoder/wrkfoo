package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

    public JPanel whole;

    public CommandTabbedPane tabbedPane;

    private JToolBar toolbar;

    private static MainWindow mouseMainWindow;

    /**
     * The main window that the mouse last entered. Used by {@link CommandTabbedPane} for drag/drop tabs.
     */
    public static MainWindow getMouseMainWindow()
    {
        return mouseMainWindow;
    }

    public MainWindow(Command<?>... commands)
    {
        whole = new JPanel();

        tabbedPane = new CommandTabbedPane();
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

        getContentPane().add(whole);

        whole.setLayout(new BorderLayout());
        whole.add(tabbedPane, BorderLayout.CENTER);
        whole.add(toolbar, BorderLayout.NORTH);

        setTitle("WrkFoo");

        boolean first = true;
        for (Command<?> command : commands) {

            if (first) {
                this.setTitle(command.getShortTitle());
            }

            addTab(command);
        }

        setLocationRelativeTo(null);
        pack();

    }

    public CommandTab getCurrentTab()
    {
        return tabbedPane.getCurrentTab();
    }

    private void fillToolbar()
    {
        ButtonBuilder builder = new ButtonBuilder(this).component(this.rootPane);

        toolbar.add(builder.name("quit").tooltip("Quit : close all WrkFoo windows").shortcut("ctrl Q").build());
        toolbar.add(builder.name("home").tooltip("Home : Show all commands").shortcut("ctrl HOME").build());
        toolbar.add(builder.name("newTab").tooltip("Open a new tab").shortcut("ctrl T").build());
        toolbar.add(builder.name("newWindow").tooltip("Open a new Window").shortcut("ctrl N").build());
        toolbar.add(builder.name("back").tooltip("Go back through the command history").shortcut("alt Left").build());
        toolbar.add(builder.name("forward").tooltip("Go forward through the command history").shortcut("alt RIGHT")
            .build());
        toolbar.add(builder.name("closeTab").tooltip("Close tab").shortcut("ctrl W").build());
        toolbar.add(builder.name("reloadOptions").tooltip("Reload Option Files").shortcut("ctrl F5").build());
    }

    public CommandTab addTab(Command<?> command)
    {
        CommandTab tab = new CommandTab(this, command);

        tabbedPane.add(tab);

        tab.postCreate();
        tab.go(command);

        return tab;
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        AutoExit.setVisible(show);

        if (show) {
            MouseListener listener = new MouseAdapter()
            {
                @Override
                public void mouseExited(MouseEvent e)
                {
                    mouseMainWindow = null;
                }

                @Override
                public void mouseEntered(MouseEvent e)
                {
                    mouseMainWindow = MainWindow.this;
                }
            };
            // whole.addMouseListener(listener);
            // When I add the listener to "whole", no events are detected, so I add it to both of its children instead.
            toolbar.addMouseListener(listener);
            tabbedPane.addMouseListener(listener);
        }
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
        CommandTab tab = tabbedPane.getSelectedCommandTab();

        if (tab == null) {
            setTitle("WrkFoo");

        } else {

            setTitle(tab.getTitle());

            getRootPane().setDefaultButton(tab.getCommand().getCommandPanel().getGoButton());
        }
    }

    private CommandTab getCurrentOrNewTab()
    {
        CommandTab tab = getCurrentTab();
        if (tab == null) {
            NullCommand command = new NullCommand();
            tab = addTab(command);
        }
        return tab;
    }

    public void onQuit()
    {
        System.exit(0);
    }
    
    public void onHome()
    {
        WrkCommand command = new WrkCommand();
        getCurrentOrNewTab().go(command);
    }

    public void onNewTab()
    {
        WrkCommand command = new WrkCommand();
        addTab(command);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void onCloseTab()
    {
        int currentTabIndex = tabbedPane.getSelectedIndex();
        if (currentTabIndex >= 0) {
            tabbedPane.removeTabAt(currentTabIndex);
        }
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
        if (getCurrentTab() != null) {
            getCurrentTab().undo();
        }
    }

    public void onForward()
    {
        if (getCurrentTab() != null) {
            getCurrentTab().redo();
        }
    }

    public void onReloadOptions()
    {
        Resources.instance.reloadOptions();
    }

}
