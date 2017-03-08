package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.nickthecoder.jguifier.util.AutoExit;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.ScriptletException;
import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.NullTool;
import uk.co.nickthecoder.wrkfoo.tool.SaveTabSet;
import uk.co.nickthecoder.wrkfoo.tool.WrkTabSets;
import uk.co.nickthecoder.wrkfoo.tool.WrkTool;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class MainWindow extends JFrame implements ExceptionHandler
{

    public JPanel whole;

    public ToolTabbedPane tabbedPane;

    private JToolBar toolbar;

    /**
     * remembers the MainWindow that the mouse was last inside (or null if it isn't in
     * a MainWindow). Used when dragging/dropping tabs.
     */
    private static MainWindow mouseMainWindow;

    private JTextField optionsTextField;

    private JToolBar statusBar;

    private JLabel message;

    /**
     * Description of the tab set, set when the tabs are save/loaded.
     */
    public String description;

    public File tabSetFile;

    private JButton goButton;

    private JButton stopButton;

    /**
     * The main window that the mouse last entered. Used by {@link ToolTabbedPane} for drag/drop tabs.
     */
    public static MainWindow getMouseMainWindow()
    {
        return mouseMainWindow;
    }

    public MainWindow(Tool... tools)
    {
        whole = new JPanel();

        tabbedPane = new ToolTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                changedTab();
            }
        });

        toolbar = new JToolBar();
        statusBar = new JToolBar();

        fillToolbars();

        getContentPane().add(whole);

        whole.setLayout(new BorderLayout());
        whole.add(tabbedPane, BorderLayout.CENTER);
        whole.add(toolbar, BorderLayout.NORTH);
        whole.add(statusBar, BorderLayout.SOUTH);

        setTitle("WrkFoo");

        for (Tool tool : tools) {
            addTab(tool);
        }

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(1000, 600);
    }

    public ToolTab getCurrentTab()
    {
        return tabbedPane.getCurrentTab();
    }

    private void fillToolbars()
    {
        ActionBuilder builder = new ActionBuilder(this).component(this.rootPane);

        toolbar.add(createToolbarOption());

        toolbar.add(builder.name("quit").tooltip("Quit : close all WrkFoo windows").shortcut("ctrl Q").buildButton());
        toolbar.add(builder.name("newWindow").tooltip("Open a new Window").shortcut("ctrl N").buildButton());
        toolbar.add(builder.name("home").tooltip("Home : Show all Tools").shortcut("ctrl HOME").buildButton());
        toolbar.add(builder.name("reloadOptions").tooltip("Reload Option Files").shortcut("ctrl F5").buildButton());
        toolbar.addSeparator();
        toolbar.add(builder.name("duplicateTab").tooltip("Duplicate Tab").buildButton());
        toolbar.add(builder.name("newTab").tooltip("Open a new tab").shortcut("ctrl T").buildButton());
        toolbar.add(builder.name("closeTab").tooltip("Close tab").shortcut("ctrl W").buildButton());
        toolbar.add(builder.name("workTabSets").tooltip("Work with Tab Sets").buildButton());
        toolbar.add(builder.name("saveTabSet").tooltip("Save Tab Sets").buildButton());
        toolbar.add(builder.name("exportTable").tooltip("Export Table Data").buildButton());
        toolbar.addSeparator();
        toolbar
            .add(builder.name("back").tooltip("Go back through the tool history").shortcut("alt Left").buildButton());
        toolbar.add(builder.name("forward").tooltip("Go forward through the tool history").shortcut("alt RIGHT")
            .buildButton());

        goButton = builder.name("run").tooltip("Re-Run the current tool").shortcut("F5").disable().buildButton();
        stopButton = builder.name("stop").tooltip("Stop current tool").shortcut("ctrl ESCAPE").hide().buildButton();
        statusBar.add(goButton);
        statusBar.add(stopButton);
        message = new JLabel("");
        statusBar.add(message);

        builder.name("previousTab").shortcut("alt PAGE_UP").buildShortcut();
        builder.name("nextTab").shortcut("alt PAGE_DOWN").buildShortcut();

        // There's an illusive bug, which causes alt F4 not to work, so I've added a different shortcut.
        // I've not spent too long hunting the bug, because I think it may be a bug in Gnome (or maybe Java).
        builder.name("closeWindow").shortcut("ctrl F4").buildShortcut();

        // Keyboard shortcuts alt+1 .. alt+9 switches to that tab number.
        for (int i = 1; i <= 9; i++) {
            switchTabMapping(i);
        }
    }

    /**
     * Create a keyboard shortcut to switch to a numbered tab.
     * 
     * @param tabNumber
     *            1 based, so subtract 1 to get valid indexes.
     */
    private void switchTabMapping(final int tabNumber)
    {
        InputMap inputMap = this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.rootPane.getActionMap();

        String name = "switchToTab" + tabNumber;
        KeyStroke keyStroke = KeyStroke.getKeyStroke("alt " + tabNumber);
        inputMap.put(keyStroke, name);
        actionMap.put(name, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (tabNumber <= tabbedPane.getTabCount()) {
                    tabbedPane.setSelectedIndex(tabNumber - 1);
                }
            }
        });
    }

    private JComponent createToolbarOption()
    {
        optionsTextField = new JTextField();
        optionsTextField.setToolTipText("Enter non-row Options");
        optionsTextField.setColumns(6);

        putAction("ENTER", "nonRowOption", optionsTextField, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    processNonRowOption(false);
                }
            });

        putAction("ctrl ENTER", "nonRowOptionNewTab", optionsTextField, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    processNonRowOption(false);

                }
            });

        optionsTextField.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseReleased(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    createOptionsMenu(me);
                }
            }

            @Override
            public void mousePressed(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    createOptionsMenu(me);
                }
            }
        });

        return optionsTextField;
    }

    private void createOptionsMenu(MouseEvent me)
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            tab.getTool().getToolPanel().createNonRowOptionsMenu(me);
        }
    }

    public void processNonRowOption(boolean newTab)
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            Tool tool = tab.getTool();

            Option option = tool.getOptions().getNonRowOption(optionsTextField.getText());
            if (option != null) {
                if (runOption(option, tool, newTab)) {
                    optionsTextField.setText("");
                }
            }
        }
    }

    public boolean runOption(Option option, Tool tool, boolean newTab)
    {
        try {
            option.runOption(tool, newTab);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    public boolean runOption(Option option, TableTool<?> tool, Object row, boolean newTab)
    {
        try {
            option.runOption(tool, row, newTab);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    public boolean runMultipleOption(Option option, TableTool<?> tool, List<Object> rows, boolean newTab)
    {
        try {
            option.runMultiOption(tool, rows, newTab);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    public ToolTab addTab(Tool tool)
    {
        ToolTab tab = new ToolTab(this, tool);

        tabbedPane.add(tab);

        tab.postCreate();
        tab.go(tool);

        return tab;
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);

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
        } else {
            dispose();
        }

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
    }

    public void changedTab()
    {
        String title = "wrkfoo";

        int goState = -1; // -1 Disabled Go, 0 = Go, 1 = Stop

        ToolTab tab = getCurrentTab();
        if (tab != null) {
            Tool tool = tab.getTool();
            title = tool.getLongTitle();
            if (tool.isRunning()) {
                goState = (tool.getTask() instanceof Stoppable) ? 1 : -1;
                title = title + " (running)";
            } else {
                goState = 0;
            }
        }

        goButton.setVisible(goState != 1);
        stopButton.setVisible(goState == 1);
        goButton.setEnabled(goState >= 0);

        if (description != null) {
            title = description + " : " + title;
        }
        setTitle(title);
    }

    public void changedState(Tool changedTool)
    {
        ToolTab tab = getCurrentTab();
        if ((tab != null) && (tab.getTool() == changedTool)) {
            if (changedTool.isRunning()) {
                setMessage("Running");
            } else {
                setMessage("Finished");
            }
            changedTab();
        }
    }

    private ToolTab getCurrentOrNewTab()
    {
        ToolTab tab = getCurrentTab();
        if (tab == null) {
            NullTool tool = new NullTool();
            tab = addTab(tool);
        }
        return tab;
    }

    public void onQuit()
    {
        System.exit(0);
    }

    public void onHome()
    {
        WrkTool tool = new WrkTool();
        getCurrentOrNewTab().go(tool);
    }

    public void onNewTab()
    {
        WrkTool tool = new WrkTool();
        addTab(tool);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void onDuplicateTab()
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            Tool copy = tab.getTool().duplicate();
            addTab(copy);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
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
        WrkTool tool = new WrkTool();
        MainWindow newWindow = new MainWindow(tool);
        tool.go();
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

    public void onRun()
    {
        if (getCurrentTab() != null) {
            getCurrentTab().getTool().getToolPanel().go();
        }
    }

    public void onStop()
    {
        if (getCurrentTab() != null) {
            getCurrentTab().getTool().stop();
        }
    }

    public void onReloadOptions()
    {
        Resources.instance.reloadOptions();
    }

    public void onWorkTabSets()
    {
        WrkTabSets tool = new WrkTabSets();
        getCurrentOrNewTab().go(tool);
    }

    public void onSaveTabSet()
    {
        SaveTabSet sts = new SaveTabSet(this);
        sts.neverExit();
        sts.promptTask();
    }

    public void onExportTable()
    {
        if (getCurrentTab() != null) {
            Tool tool = getCurrentTab().getTool();
            if (tool instanceof TableTool<?>) {
                ExportTableData std = new ExportTableData((TableTool<?>) tool);
                std.neverExit();
                std.promptTask();
            }
        }
    }

    public void onNextTab()
    {
        tabbedPane.nextTab();
    }

    public void onPreviousTab()
    {
        tabbedPane.previousTab();
    }

    public void onCloseWindow()
    {
        setVisible(false);
    }

    /**
     * Records the time that the last error message was sent. Used to determine if later messages should obscure the
     * error.
     */
    private long lastError = new Date().getTime();

    /**
     * If an error and then a regular message is sent, how long must have elpsed for the message to replace the error.
     */
    private final static long ERROR_TIMEOUT_MILLIS = 5000l; // 5 seconds

    public void setMessage(String text)
    {
        if (new Date().getTime() - lastError > ERROR_TIMEOUT_MILLIS) {
            message.setForeground(Color.black);
            message.setText(text);

        }
    }

    public void setErrorMessage(String text)
    {
        message.setForeground(Color.red);
        message.setText(text);
        lastError = new Date().getTime();
    }

    @Override
    public void handleException(Throwable e)
    {
        // Find the root cause, but stop at ScriptletExceptions, because they are useful.
        do {
            if (e instanceof ScriptletException) {
                break;
            }

            e = e.getCause();
        } while (e.getCause() != null);

        setErrorMessage(e.getMessage());
    }
}
