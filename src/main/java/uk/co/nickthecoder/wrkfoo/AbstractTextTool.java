package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTextTool<T extends Task & TextResults> extends AbstractThreadedTool<T>
{

    public AbstractTextTool(T task)
    {
        super(task);
    }

    @Override
    public TextResultsPanel createResultsComponent()
    {
        return new TextResultsPanel(task.getResults());
    }

    @Override
    public void updateResults()
    {
        TextResultsPanel trp = (TextResultsPanel) getToolPanel().getResultsPanel();
        trp.textArea.setText(task.getResults());
    }
}
