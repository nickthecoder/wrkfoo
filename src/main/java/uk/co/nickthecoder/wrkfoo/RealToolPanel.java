package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskListener;
import uk.co.nickthecoder.jguifier.guiutil.FocusNextListener;
import uk.co.nickthecoder.jguifier.guiutil.ScrollablePanel;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public class RealToolPanel implements ToolPanel, TaskListener
{
    private JPanel panel;

    private HalfTab halfTab;

    private Tool<?> tool;

    private HidingSplitPane splitPane;

    private ToolPanelToolBar toolPanelToolBar;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    private JButton stopButton;

    private JScrollPane parametersScrollPane;

    public RealToolPanel(Tool<?> foo)
    {
        Util.assertIsEDT();

        this.tool = foo;
        panel = new JPanel();

        sidePanel = new JPanel();
        sidePanel.addFocusListener(new FocusNextListener());

        sidePanel.setPreferredSize(new Dimension(300, 300));
        body = new JPanel();
        body.setLayout(new BorderLayout());

        sidePanel.setLayout(new BorderLayout());
        parametersPanel = foo.createParametersPanel();

        ScrollablePanel scrollablePanel = new ScrollablePanel();
        scrollablePanel.setScrollableTracksViewportWidth(true);
        scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
        scrollablePanel.add(parametersPanel);

        parametersScrollPane = new JScrollPane(scrollablePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        parametersScrollPane.setMinimumSize(new Dimension(0, 0));

        // For some reason sharing the same colour instance doesn't work. (And there is no copy, clone or copy
        // constructor), so lets copy the long way...
        Color srcColor = parametersPanel.getBackground();
        Color background = new Color(srcColor.getRed(), srcColor.getGreen(), srcColor.getBlue());
        parametersScrollPane.getViewport().setBackground(background);

        sidePanel.add(parametersScrollPane, BorderLayout.CENTER);

        JPanel goStop = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        ActionBuilder builder = new ActionBuilder(this).component(sidePanel);
        goButton = builder.name("toolpanel.go").method("go").label("Go").icon("run.png").buildButton();
        goStop.add(goButton, gbc);

        builder.component(panel);

        stopButton = builder.name("toolpanel.stop").method("stop").label("Stop").buildButton();
        stopButton.setVisible(false);
        goStop.add(stopButton, gbc);

        sidePanel.add(goStop, BorderLayout.SOUTH);

        body.add(tool.getResultsPanel().getComponent(), BorderLayout.CENTER);

        splitPane = new HidingSplitPane(JSplitPane.VERTICAL_SPLIT, true, body, sidePanel);
        splitPane.setResizeWeight(0.5);

        panel.setLayout(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);

        toolPanelToolBar = new ToolPanelToolBar(this);
        panel.add(toolPanelToolBar.getComponent(), BorderLayout.SOUTH);

        splitPane.setState(HidingSplitPane.State.LEFT);
        if (!this.tool.getTask().getRootParameter().children().iterator().hasNext()) {
            sidePanel.add(new JLabel("No Parameters"), BorderLayout.NORTH);
        }

        this.tool.getTask().addTaskListener(this);

        ActionBuilder builder2 = new ActionBuilder(this).component(panel);

        builder2.name("cyclePane").buildShortcut();
        builder2.name("toggleLeftPane").buildShortcut();
        builder2.name("toggleRightPane").buildShortcut();
    }

    public Tool<?> getTool()
    {
        return tool;
    }

    @Override
    public JComponent getComponent()
    {
        Util.assertIsEDT();
        return panel;
    }

    @Override
    public ToolPanelToolBar getToolBar()
    {
        Util.assertIsEDT();
        return toolPanelToolBar;
    }

    @Override
    public HidingSplitPane getSplitPane()
    {
        Util.assertIsEDT();
        return splitPane;
    }

    @Override
    public ParametersPanel getParametersPanel()
    {
        return parametersPanel;
    }

    @Override
    public void attachTo(HalfTab halfTab)
    {
        assert (this.halfTab == null);

        this.halfTab = halfTab;
    }

    @Override
    public void detach()
    {
        this.halfTab = null;
    }

    @Override
    public HalfTab getHalfTab()
    {
        return halfTab;
    }

    public void onCyclePane()
    {
        splitPane.cycle();
        if (splitPane.getState() == HidingSplitPane.State.LEFT) {
            Focuser.focusLater("ToolPanel.Cycle. Results", tool.getResultsPanel().getFocusComponent(), 7);
        } else {
            Focuser.focusLater("ToolPanel.Cycle. Parameters", getParametersPanel(), 7);
        }
    }

    public void onToggleLeftPane()
    {
        splitPane.toggleLeft();
        if (splitPane.getState() == HidingSplitPane.State.LEFT) {
            Focuser.focusLater("ToolPanel.ToggleLeft. Results", tool.getResultsPanel().getFocusComponent(), 7);
        } else {
            Focuser.focusLater("ToolPanel.ToggleLeft. Parameters", getParametersPanel(), 7);
        }
    }

    public void onToggleRightPane()
    {
        splitPane.toggleRight();
        if (splitPane.getState() == HidingSplitPane.State.RIGHT) {
            Focuser.focusLater("ToolPanel.ToggleRight. Parameters", getParametersPanel(), 7);
        } else {
            Focuser.focusLater("ToolPanel.ToggleRight. Results", tool.getResultsPanel().getFocusComponent(), 7);
        }
    }

    @Override
    public boolean check()
    {
        return parametersPanel.check(tool.getTask());
    }

    @Override
    public void go()
    {
        halfTab.go(tool);
    }

    public void stop()
    {
        tool.stop();
    }

    public MainWindow getMainWindow()
    {
        Component root = SwingUtilities.getRoot(getComponent());
        if ( root instanceof MainWindow ) {
            return (MainWindow) root;
        }
        return null;
    }

    private void changedRunningState(final boolean running)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                goButton.setEnabled(!running);
                if (tool.getTask() instanceof Stoppable) {
                    goButton.setVisible(!running);
                    stopButton.setVisible(running);
                }

                MainWindow mainWindow = getMainWindow();
                if (mainWindow != null) {
                    mainWindow.changedRunningState(tool, running);
                }
            }
        });
    }

    @Override
    public void started(Task task)
    {
        changedRunningState(true);
    }

    @Override
    public void ended(Task task, boolean normally)
    {
        changedRunningState(false);
    }

    @Override
    public void aborted(Task task)
    {
    }

    @Override
    public TopLevel getTopLevel()
    {
        return TopLevel.getTopLevel(getComponent());
    }

}
