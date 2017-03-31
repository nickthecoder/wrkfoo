package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.Parameter;

/**
 * Results, which include the {@link Parameter}s from a {@link Task}, plus a 'run' button.
 */
public class TaskResults extends PanelResults
{
    private Task task;

    private ParametersPanel parametersPanel;

    public TaskResults(Task task)
    {
        this(task, "Save");
    }

    public TaskResults(Task task, String goText)
    {
        super();
        this.task = task;

        parametersPanel = new ParametersPanel();
        parametersPanel.addParameters(task.getRootParameter());

        JPanel buttons = new JPanel();

        JButton go = new JButton(goText);
        buttons.add(go);

        panel.add(parametersPanel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.EAST);
    }

    public Task getTask()
    {
        return task;
    }
}
