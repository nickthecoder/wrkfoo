package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.nickthecoder.jguifier.guiutil.WrapLayout;
import uk.co.nickthecoder.jguifier.util.AutoExit;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.option.ScriptletException;
import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.tool.NullTool;
import uk.co.nickthecoder.wrkfoo.tool.Projects;
import uk.co.nickthecoder.wrkfoo.tool.SaveProject;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

public class MainWindow extends JFrame implements ExceptionHandler
{
    private static final long serialVersionUID = 1L;

    private static final List<MainWindow> windows = new ArrayList<>();

    public JPanel whole;

    public TabbedPane tabbedPane;

    private JPanel toolBarPanel;

    private JToolBar toolBar;

    private JPanel statusBarPanel;

    private JToolBar statusBar;

    /**
     * remembers the MainWindow that the mouse was last inside (or null if it isn't in
     * a MainWindow). Used when dragging/dropping tabs.
     */
    private static MainWindow mouseMainWindow;

    private JTextField optionTextField;

    private JLabel message;

    /**
     * Description of the tab set, set when the tabs are save/loaded.
     */
    public String description;

    public File projectFile;

    private JButton goButton;

    private JButton stopButton;

    private JButton errorButton;

    /**
     * The main window that the mouse last entered. Used by {@link TabbedPane} for drag/drop tabs.
     */
    public static MainWindow getMouseMainWindow()
    {
        return mouseMainWindow;
    }

    public static MainWindow getMainWindow(Component component)
    {
        return (MainWindow) SwingUtilities.getWindowAncestor(component);
    }

    public MainWindow()
    {
        whole = new JPanel();

        tabbedPane = new TabbedPane();
        tabbedPane.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                changedTab();
            }
        });

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new WrapLayout(FlowLayout.LEFT));

        statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new BoxLayout(statusBarPanel, BoxLayout.Y_AXIS));

        toolBar = new JToolBar();
        statusBar = new JToolBar();
        toolBar.setFloatable(false);
        statusBar.setFloatable(false);

        toolBarPanel.add(toolBar);
        statusBarPanel.add(statusBar);

        fillToolBars();

        getContentPane().add(whole);

        whole.setLayout(new BorderLayout());
        whole.add(tabbedPane, BorderLayout.CENTER);
        whole.add(toolBarPanel, BorderLayout.NORTH);
        whole.add(statusBarPanel, BorderLayout.SOUTH);

        setTitle("WrkFoo");

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(1000, 600);
    }

    public JPanel getToolBarPanel()
    {
        return toolBarPanel;
    }

    public JPanel getStatusBarPanel()
    {
        return statusBarPanel;
    }

    public ToolTab getCurrentTab()
    {
        return tabbedPane.getSelectedTab();
    }

    private void fillToolBars()
    {
        ActionBuilder builder = new ActionBuilder(this).component(this.rootPane);

        optionTextField = createOptionTextField();
        toolBar.add(optionTextField);

        toolBar.add(builder.name("quit").tooltip("Quit : close all WrkFoo windows").buildButton());
        toolBar.add(builder.name("newWindow").tooltip("Open a new Window").buildButton());
        toolBar.add(builder.name("home").tooltip("Home : Show all Tools").buildButton());
        // MainWindow.toolBar.add(builder.name("reloadOptions").tooltip("Reload Option Files").buildButton());
        toolBar.addSeparator();
        toolBar.add(builder.name("duplicateTab").tooltip("Duplicate Tab").buildButton());
        toolBar.add(builder.name("newTab").tooltip("Open a new tab").buildButton());
        toolBar.add(builder.name("closeTab").tooltip("Close tab").buildButton());
        toolBar.add(builder.name("workProjects").icon("projects.png").tooltip("Work with Projects").buildButton());
        toolBar.add(builder.name("saveProject").tooltip("Save Project").buildButton());
        toolBar.add(builder.name("exportTable").tooltip("Export Table Data").buildButton());
        toolBar.addSeparator();
        toolBar.add(builder.name("back").tooltip("Go back through the tool history").buildButton());
        toolBar.add(builder.name("forward").tooltip("Go forward through the tool history").buildButton());

        goButton = builder.name("run").tooltip("Re-Run the current tool").disable().buildButton();
        stopButton = builder.name("stop").tooltip("Stop current tool").hide().buildButton();
        statusBar.add(goButton);
        statusBar.add(stopButton);

        errorButton = builder.name("showError").tooltip("Show stack trace").hide().buildButton();
        statusBar.add(errorButton);

        message = new JLabel("");
        statusBar.add(message);

        builder.name("previousTab").buildShortcut();
        builder.name("nextTab").buildShortcut();
        builder.name("jumpToToolBar").buildShortcut();
        builder.name("jumpToResults").buildShortcut();
        builder.name("jumpToParameters").buildShortcut();

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
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (tabNumber <= tabbedPane.getTabCount()) {
                    tabbedPane.setSelectedIndex(tabNumber - 1);
                }
            }
        });
    }

    private JTextField createOptionTextField()
    {
        JTextField textField = new JTextField();
        textField.setToolTipText("Enter non-row Options (F10)");
        textField.setColumns(6);

        ActionBuilder builder = new ActionBuilder(this).component(textField);

        builder.name("promptNonRowOption").buildShortcut();
        builder.name("promptNonRowOptionInNewTab").buildShortcut();

        builder.name("runNonRowOptionInNewTab").buildShortcut();
        builder.name("runNonRowOption").buildShortcut();

        textField.addMouseListener(new MouseAdapter()
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

        return textField;
    }

    private void createOptionsMenu(MouseEvent me)
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            new OptionsRunner(tab.getTool()).popupNonRowMenu(me);
        }
    }

    private void processOptionField(boolean newTab, boolean prompt)
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            Tool<?> tool = tab.getTool();

            if (!optionTextField.getText().equals("")) {
                if (new OptionsRunner(tool).runOption(optionTextField.getText(), newTab, prompt)) {
                    optionTextField.setText("");
                }
            }
        }
    }

    public ToolTab insertTab(final Tool<?> tool, boolean prompt)
    {
        ToolTab tab = new ToolTab(tool);

        tabbedPane.insert(tab);

        tab.postCreate();
        if (prompt) {
            tool.getToolPanel().getSplitPane().showRight();
            MainWindow.focusLater("right - inserted tab being prompted",
                tool.getToolPanel().getSplitPane().getRightComponent(), 8);

        } else {
            tab.go(tool);
        }

        return tab;
    }

    public ToolTab addTab(Tool<?> tool)
    {
        ToolTab tab = new ToolTab(tool);

        tabbedPane.add(tab);

        tab.postCreate();
        tab.go(tool);

        return tab;
    }

    @Override
    public void setVisible(boolean show)
    {
        super.setVisible(show);

        if (show) {
            windows.add(this);

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

            // When I add the listener to this, or "whole", no events are detected, this is the best I could do.
            toolBarPanel.addMouseListener(listener);
            tabbedPane.addMouseListener(listener);
            statusBarPanel.addMouseListener(listener);

        } else {
            windows.remove(this);
            tabbedPane.removeAllTabs();
            dispose();
        }

        AutoExit.setVisible(show);
    }

    public void changedTab()
    {
        String title = "wrkfoo";

        ToolTab tab = getCurrentTab();
        if (tab == null) {
            stopGoButtons(false);
        } else {
            Tool<?> tool = tab.getTool();
            title = tool.getLongTitle();
            stopGoButtons(tool.getTask().isRunning());

            if (tool.getTask().isRunning()) {
                setMessage("Running");
            } else {
                setMessage("");
            }
        }

        if (description != null) {
            title = description + " : " + title;
        }
        setTitle(title);
    }

    private void stopGoButtons(boolean running)
    {
        WrkFoo.assertIsEDT();
        int goState = running ? -1 : 0; // -1 Disabled Go, 0 = Go, 1 = Stop

        ToolTab tab = getCurrentTab();
        if (running && (tab != null) && (tab.getTool().getTask() instanceof Stoppable)) {
            goState = 1;
        }

        goButton.setVisible(goState != 1);
        stopButton.setVisible(goState == 1);
        goButton.setEnabled(goState >= 0);
    }

    public void changedRunningState(Tool<?> changedTool, boolean running)
    {
        WrkFoo.assertIsEDT();

        ToolTab tab = getCurrentTab();
        if ((tab != null) && (tab.getTool() == changedTool)) {
            if (running) {
                setMessage("Running");
            } else {
                setMessage("");
            }
        }
        stopGoButtons(running);
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
        // Close all of the windows, which will stop any stoppable tasks.
        for (MainWindow window : windows) {
            window.setVisible(false);
        }
        System.exit(0);
    }

    public void onHome()
    {
        Home tool = new Home();
        getCurrentOrNewTab().go(tool);
    }

    public void onNewTab()
    {
        Home tool = new Home();
        addTab(tool);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void onDuplicateTab()
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            Tool<?> copy = tab.getTool().duplicate();
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
        Home tool = new Home();
        MainWindow newWindow = new MainWindow();
        newWindow.addTab(tool);
        tool.go();
        newWindow.setVisible(true);
    }

    public void onBack()
    {
        if (getCurrentTab() != null) {
            getCurrentTab().onUndoTool();
        }
    }

    public void onForward()
    {
        if (getCurrentTab() != null) {
            getCurrentTab().onRedoTool();
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

    public void onWorkProjects()
    {
        Projects tool = new Projects();
        getCurrentOrNewTab().go(tool);
    }

    public void onSaveProject()
    {
        SaveProject sp = new SaveProject(this);
        sp.promptTask();
    }

    public void onExportTable()
    {
        if (getCurrentTab() != null) {
            Tool<?> tool = getCurrentTab().getTool();
            if (tool instanceof TableTool<?,?>) {
                ExportTableData std = new ExportTableData((TableTool<?,?>) tool);
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

    public void onJumpToToolBar()
    {
        optionTextField.requestFocusInWindow();
    }

    public JTextField getOptionField()
    {
        return optionTextField;
    }

    private int focusImportance = -1;
    private Component focusComponent;
    private long focusTime;
    @SuppressWarnings("unused")
    private String focusDescription; // This is only used when I uncomment the System.out s.

    public static void focusLater(String description, Component c, int importance)
    {
        MainWindow mw = MainWindow.getMainWindow(c);
        if (mw != null) {
            mw.privateFocusLater(c, description, importance);
        }
    }

    /**
     * Focuses on the component invoking later, using SwingUtilities.invokeLater.
     * If a component is already requested to be focused, and before it has been carried out,
     * then the less important will be ignored.
     * If a component has very recently been focused, and then a request to focus with lower importance
     * will be ignored
     * 
     * @param c
     *            The component to focus on
     * @param description
     *            A description of why/what is being focused on, just to help debugging
     * @param importance
     *            Range 0..10, 10 being the most important
     */
    private void privateFocusLater(Component c, String description, int importance)
    {
        // Note. focusComponent is null, when there is nothing waiting, but focusImportance is NOT reset back to -1.

        if ((focusComponent == null) || (importance > focusImportance)) {
            if (focusComponent == null) {
                // No focus pending.
                long now = new Date().getTime();
                if ((importance < focusImportance) && (now - focusTime < 1000)) {
                    // Ignore a lower importance soon after a high importance.
                    //System.out.println("Skipping - too soon");
                    return;
                }

            } else {
                // There is a focus pending
                if (importance < focusImportance) {
                    // Ignore lower importance
                    //System.out.println("Ignoring low importance " + description + " for " + focusDescription);
                    return;
                } else {
                    // Ok, lets replace the pending one with this higher importance one.
                    //System.out.println("Doing " + description + " instead of " + focusDescription);
                }
            }

            Component old = focusComponent;

            focusComponent = c;
            focusImportance = importance;
            focusDescription = description;

            if (old == null) {
                //System.out.println("Invoking later");
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //System.out.println("Focusing NOW. " + focusDescription);
                        focusComponent.requestFocusInWindow();
                        focusComponent = null;
                        focusTime = new Date().getTime();
                    }
                });
            }
        } else {
            //System.out.println("Ignoring " + description + ". Doing this instead : " + focusDescription);
        }
    }

    public void onJumpToResults()
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            tab.getTool().getToolPanel().getSplitPane().showLeft();
            MainWindow.focusLater("Jump to results", tab.getTool().getToolPanel().getSplitPane().getRightComponent(),
                10);
        }
    }

    public void onJumpToParameters()
    {
        ToolTab tab = getCurrentTab();
        if (tab != null) {
            tab.getTool().getToolPanel().getSplitPane().showRight();
            MainWindow.focusLater("Jump to parameters", tab.getTool().getToolPanel().getSplitPane().getLeftComponent(),
                10);
        }
    }

    public void onShowError()
    {
        errorButton.setVisible(false);
        if (stackTrace != null) {
            JTextArea textArea = new JTextArea(stackTrace);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(900, 300));
            JOptionPane.showMessageDialog(null, scrollPane, "Error", JOptionPane.OK_OPTION);
        }
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
        e.printStackTrace();

        // Find the root cause, but stop at ScriptletExceptions, because they are useful.
        do {
            if (e instanceof ScriptletException) {
                break;
            }
            if (e.getCause() == null) {
                break;
            }
            e = e.getCause();
        } while (true);

        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        stackTrace = writer.toString();
        try {
            writer.close();

        } catch (IOException e1) {
        }

        errorButton.setVisible(true);
        setErrorMessage(e.getMessage());
    }

    private String stackTrace;

    public void onRunNonRowOption()
    {
        processOptionField(false, false);
    }

    public void onRunNonRowOptionInNewTab()
    {
        processOptionField(true, false);
    }

    public void onPromptNonRowOption()
    {
        processOptionField(false, true);
    }

    public void onPromptNonRowOptionInNewTab()
    {
        processOptionField(true, true);
    }
}
