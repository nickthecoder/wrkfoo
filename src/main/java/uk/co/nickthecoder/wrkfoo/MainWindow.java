package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.guiutil.WrapLayout;
import uk.co.nickthecoder.jguifier.util.AutoExit;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.option.ScriptletException;
import uk.co.nickthecoder.wrkfoo.tool.NullTool;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class MainWindow extends JFrame implements TopLevel, TabListener
{
    private static final long serialVersionUID = 1L;

    static final List<MainWindow> windows = new ArrayList<>();

    public JPanel whole;

    public MainTabs mainTabs;

    private JPanel toolBarPanel;

    private JToolBar toolBar;

    private JPanel statusBarPanel;

    private JToolBar statusBar;

    String stackTrace;

    /**
     * remembers the MainWindow that the mouse was last inside (or null if it isn't in
     * a MainWindow). Used when dragging/dropping tabs.
     */
    private static MainWindow mouseMainWindow;

    private JLabel message;

    /**
     * Description of the tab set, set when the tabs are save/loaded.
     */
    public String description;

    public File projectFile;

    private JButton goButton;

    private JButton stopButton;

    JButton errorButton;

    /**
     * The main window that the mouse last entered. Used by {@link MainTabs} for drag/drop tabs.
     */
    public static MainWindow getMouseMainWindow()
    {
        return mouseMainWindow;
    }

    public MainWindow()
    {
        whole = new JPanel();

        mainTabs = new MainTabs();
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
        whole.add(mainTabs.getComponent(), BorderLayout.CENTER);
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

    @Override
    public void addToolBar(JComponent toolBar)
    {
        toolBarPanel.add(toolBar);
    }

    @Override
    public void addStatusBar(JComponent statusBar)
    {
        statusBarPanel.add(statusBar, 0);
    }

    public ToolTab getCurrentTab()
    {
        return mainTabs.getSelectedTab();
    }

    private void fillToolBars()
    {
        MainWindowEvents mwe = new MainWindowEvents(this);

        ActionBuilder builder = new ActionBuilder(mwe).component(this.rootPane);

        toolBar.add(builder.name("quit").tooltip("Quit : close all WrkFoo windows").buildButton());
        toolBar.add(builder.name("newWindow").tooltip("Open a new Window").buildButton());
        toolBar.add(builder.name("duplicateTab").tooltip("Duplicate Tab").buildButton());
        toolBar.add(builder.name("newTab").tooltip("Open a new tab").buildButton());
        toolBar.add(builder.name("closeTab").tooltip("Close tab").buildButton());
        toolBar.add(builder.name("saveProject").tooltip("Save Project").buildButton());
        toolBar.add(builder.name("openProject").icon("projects.png").tooltip("Open a Project").buildButton());

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
                if (tabNumber <= mainTabs.getTabCount()) {
                    mainTabs.setSelectedIndex(tabNumber - 1);
                }
            }
        });
    }
    public ToolTab insertTab(final Tool<?> tool, boolean prompt)
    {
        ToolTab tab = new ToolTab(tool);

        mainTabs.insert(tab);

        tab.postCreate();
        if (prompt) {
            tool.getToolPanel().getSplitPane().showRight();
            tool.getToolPanel().getSplitPane().getRightComponent();

        } else {
            tab.go(tool);
        }

        return tab;
    }

    public ToolTab addTab(Tool<?> tool)
    {
        ToolTab tab = new ToolTab(tool);

        mainTabs.add(tab);

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
            TabNotifier.addTabListener(this);

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
            mainTabs.getComponent().addMouseListener(listener);
            statusBarPanel.addMouseListener(listener);

        } else {
            windows.remove(this);
            TabNotifier.removeTabListener(this);

            mainTabs.removeAllTabs();
            dispose();
        }

        AutoExit.setVisible(show);
    }

    private void updateTitle()
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

    ToolTab getCurrentOrNewTab()
    {
        ToolTab tab = getCurrentTab();
        if (tab == null) {
            NullTool tool = new NullTool();
            tab = addTab(tool);
        }
        return tab;
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

    @Override
    public void attachedTab(ToolTab tab)
    {
    }

    @Override
    public void detachingTab(ToolTab tab)
    {
    }

    @Override
    public void selectedTab(ToolTab tab)
    {
        if (tab.getTool().getToolPanel().getTopLevel() == this) {
            updateTitle();
        }
    }

    @Override
    public void deselectingTab(ToolTab tab)
    {
    }

    @Override
    public void changedTitle(ToolTab tab)
    {
        if (tab.getTool().getToolPanel().getTopLevel() == this) {
            updateTitle();
            mainTabs.updateTitle(tab);
        }
    }
}
