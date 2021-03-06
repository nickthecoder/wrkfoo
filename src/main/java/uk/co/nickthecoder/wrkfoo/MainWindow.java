package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.util.AutoExit;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.ScriptletException;
import uk.co.nickthecoder.wrkfoo.tool.NullTool;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.AutoComponentUpdater;

public class MainWindow extends JFrame implements TopLevel, TabListener
{
    private static final long serialVersionUID = 1L;

    static final List<MainWindow> windows = new ArrayList<>();

    public JPanel whole;

    public MainTabs mainTabs;

    private JToolBar toolBar;

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

    JButton errorButton;

    @SuppressWarnings("unused") // Keep a reference to stop it being garbage collected.
    private AutoComponentUpdater componentUpdater;

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

        toolBar = new JToolBar();
        statusBar = new JToolBar();
        toolBar.setFloatable(false);
        statusBar.setFloatable(false);

        fillToolBars();

        getContentPane().add(whole);

        whole.setLayout(new BorderLayout());
        whole.add(mainTabs.getComponent(), BorderLayout.CENTER);
        whole.add(toolBar, BorderLayout.NORTH);
        whole.add(statusBar, BorderLayout.SOUTH);

        setTitle("WrkFoo");

        pack();
        setLocationRelativeTo(null);
    }

    public MainTabs getMainTabs()
    {
        return mainTabs;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(1000, 600);
    }

    public Tab getCurrentTab()
    {
        return mainTabs.getSelectedTab();
    }

    private void fillToolBars()
    {
        MainWindowEvents mwe = new MainWindowEvents(this);

        ActionBuilder builder = new ActionBuilder(mwe).component(this.rootPane);

        JButton dupTab;
        JToggleButton splitV;
        JToggleButton splitH;
        JToggleButton unsplit;

        toolBar.add(builder.name("quit").tooltip("Quit : close all WrkFoo windows").buildButton());
        toolBar.add(builder.name("newWindow").tooltip("Open a new Window").buildButton());
        toolBar.add(dupTab = builder.name("duplicateTab").tooltip("Duplicate Tab").buildButton());
        toolBar.add(splitH = builder.name("splitHorizontal").tooltip("Split Horizontal").buildToggleButton());
        toolBar.add(splitV = builder.name("splitVertical").tooltip("Split Vertical").buildToggleButton());
        toolBar.add(unsplit = builder.name("unsplit").tooltip("Un-Split").buildToggleButton());
        toolBar.add(builder.name("newTab").tooltip("Open a new tab").buildButton());
        toolBar.add(builder.name("saveProject").tooltip("Save Project").buildButton());
        toolBar.add(builder.name("openProject").icon("projects.png").tooltip("Open a Project").buildButton());
        builder.name("closeTab").buildShortcut();

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

        componentUpdater = new AutoComponentUpdater(getClass().getSimpleName())
        {
            @Override
            protected void autoUpdate()
            {
                Tab tab = getMainTabs().getSelectedTab();
                boolean haveTab = tab != null;
                dupTab.setEnabled(haveTab);
                unsplit.setEnabled(haveTab);
                splitV.setEnabled(haveTab);
                splitH.setEnabled(haveTab);

                if (haveTab) {
                    unsplit.setSelected(!tab.isSplit());
                    splitV.setSelected(tab.isSplitV());
                    splitH.setSelected(tab.isSplitH());
                }
            }
        };
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

    public Tab insertTab(final Tool<?> tool, boolean prompt)
    {
        Tab tab = new Tab(tool);

        mainTabs.insert(tab);

        if (prompt) {
            tool.getToolPanel().getSplitPane().showRight();
            tool.getToolPanel().getSplitPane().getRightComponent();

        } else {
            tab.getMainHalfTab().go(tool);
        }

        return tab;
    }

    public Tab addTab(Tool<?> tool)
    {
        return addTab(tool, null);
    }

    public Tab addTab(Tool<?> mainTool, Tool<?> otherTool)
    {
        Tab tab = new Tab(mainTool, otherTool);

        mainTabs.add(tab);

        tab.getMainHalfTab().go(mainTool);

        if (otherTool != null) {
            tab.getOtherHalfTab().go(otherTool);
        }

        mainTabs.setSelectedIndex(mainTabs.getTabCount() - 1);

        return tab;
    }

    @Override
    public void setVisible(boolean show)
    {
        try {
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
                toolBar.addMouseListener(listener);
                mainTabs.getComponent().addMouseListener(listener);
                statusBar.addMouseListener(listener);

            } else {
                windows.remove(this);
                TabNotifier.removeTabListener(this);

                mainTabs.removeAllTabs();
                dispose();
            }
        } finally {
            AutoExit.setVisible(show);
        }
    }

    private void updateTitle()
    {
        String title = "wrkfoo";

        Tab tab = getCurrentTab();
        if (tab != null) {

            Tool<?> tool = tab.getMainHalfTab().getTool();
            title = tool.getLongTitle();

            if (tool.getTask().isRunning()) {
                setMessage("Running");
            } else {
                setMessage(" ");
            }
        }

        if (description != null) {
            title = description + " : " + title;
        }
        setTitle(title);
    }

    public void changedRunningState(Tool<?> changedTool, boolean running)
    {
        WrkFoo.assertIsEDT();

        Tab tab = getCurrentTab();
        if ((tab != null) && (tab == changedTool.getHalfTab().getTab())) {
            if (running) {
                setMessage("Running");
            } else {
                setMessage(" ");
            }
        }
    }

    Tab getCurrentOrNewTab()
    {
        Tab tab = getCurrentTab();
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
        Util.assertIsEDT();

        if (new Date().getTime() - lastError > ERROR_TIMEOUT_MILLIS) {
            message.setForeground(Color.black);
            message.setText(text);
        }
    }

    public void setErrorMessage(String text)
    {
        Util.assertIsEDT();

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
    public void attached(Tab tab)
    {
    }

    @Override
    public void detaching(Tab tab)
    {
    }

    @Override
    public void attached(HalfTab halfTab)
    {
    }

    @Override
    public void detaching(HalfTab halfTab)
    {
    }

    private boolean partOfMe(Tab tab)
    {
        return SwingUtilities.windowForComponent(tab.getComponent()) == this;
    }

    @Override
    public void selectedTab(Tab tab)
    {
        if (partOfMe(tab)) {
            updateTitle();
        }
    }

    @Override
    public void deselectingTab(Tab tab)
    {
    }

    @Override
    public void changedTitle(Tab tab)
    {
        if (partOfMe(tab)) {
            updateTitle();
            mainTabs.updateTitle(tab);
        }
    }
}
