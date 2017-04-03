package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.Parameter;

/**
 * Results, which include the {@link Parameter}s from a {@link Task}, plus a 'run' button.
 */
public class TaskResults<T extends Task> extends PanelResults
{
    private T task;

    private ParametersPanel parametersPanel;

    public TaskResults(Tool<?> tool, T task)
    {
        this(tool, task, "Save");
    }

    public TaskResults(Tool<?> tool, T task, String goText)
    {
        super(tool);
        this.task = task;

        parametersPanel = new ParametersPanel();
        parametersPanel.addParameters(task.getRootParameter());

        JPanel buttons = new JPanel();

        JButton go = new JButton(goText);
        buttons.add(go);

        go.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                task.check();
                task.run();
            }

        });

        panel.add(parametersPanel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.EAST);
    }

    public T getTask()
    {
        return task;
    }
}
