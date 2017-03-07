package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTextCommand<T extends Task & TextResults> extends AbstractCommand<T>
{

    public AbstractTextCommand(T task)
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
        TextResultsPanel trp = (TextResultsPanel) getCommandPanel().getResultsPanel();
        trp.textArea.setText( task.getResults() );
    }
}
