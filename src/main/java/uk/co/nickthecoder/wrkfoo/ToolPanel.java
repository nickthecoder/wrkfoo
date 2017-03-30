package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskListener;
import uk.co.nickthecoder.jguifier.guiutil.ScrollablePanel;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public class ToolPanel extends JPanel implements TaskListener
{
    private static final long serialVersionUID = 1L;

    private Tool<?> tool;

    private HidingSplitPane splitPane;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    private JButton stopButton;

    private JScrollPane parametersScrollPane;

    protected ResultsPanel resultsPanel;

    public ToolPanel(Tool<?> foo)
    {
        this.tool = foo;

        sidePanel = new JPanel();
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
        // parametersScrollPane = new
        // JScrollPane(parametersPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        parametersScrollPane.setMinimumSize(new Dimension(0, 0));

        // For some reason sharing the same color instance doesn't work. (And there is no copy, clone or copy
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

        ActionBuilder builder = new ActionBuilder(this);

        goButton = builder.name("toolpanel.go").method("go").label("Go").icon("run.png").buildButton();
        goStop.add(goButton, gbc);

        stopButton = builder.name("toolpanel.stop").method("stop").label("Stop").buildButton();
        stopButton.setVisible(false);
        goStop.add(stopButton, gbc);

        sidePanel.add(goStop, BorderLayout.SOUTH);

        resultsPanel = tool.createResultsPanel();
        body.add(resultsPanel, BorderLayout.CENTER);

        splitPane = new HidingSplitPane(JSplitPane.VERTICAL_SPLIT, true, body, sidePanel);
        splitPane.setResizeWeight(0.5);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        splitPane.setState(HidingSplitPane.State.LEFT);
        if (!this.tool.getTask().getRootParameter().children().iterator().hasNext()) {
            sidePanel.add(new JLabel("No Parameters"), BorderLayout.NORTH);
        }

        this.setBackground(Color.blue);
        this.tool.getTask().addTaskListener(this);
    }

    public HidingSplitPane getSplitPane()
    {
        return splitPane;
    }

    public ResultsPanel getResultsPanel()
    {
        return resultsPanel;
    }

    public ParametersPanel getParametersPanel()
    {
        return parametersPanel;
    }

    public void postCreate()
    {
        tool.postCreate();

        ActionBuilder builder = new ActionBuilder(this).condition(WHEN_IN_FOCUSED_WINDOW);

        builder.name("cyclePane").buildShortcut();
        builder.name("toggleLeftPane").buildShortcut();
        builder.name("toggleRightPane").buildShortcut();
    }

    public void onCyclePane()
    {
        MainWindow.focusLater("cycle", splitPane.cycle(), 10);
    }

    public void onToggleLeftPane()
    {
        MainWindow.focusLater("toogle left", splitPane.toggleLeft(), 10);
    }

    public void onToggleRightPane()
    {
        MainWindow.focusLater("toogle right", splitPane.toggleRight(), 10);
    }

    public boolean check()
    {
        return parametersPanel.check(tool.getTask());
    }

    public void go()
    {
        tool.getToolTab().go(tool);
    }

    public void stop()
    {
        tool.stop();
    }

    public MainWindow getMainWindow()
    {
        return (MainWindow) SwingUtilities.getRoot(this);
    }

    private void changedRunningState(final boolean running)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
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

}
