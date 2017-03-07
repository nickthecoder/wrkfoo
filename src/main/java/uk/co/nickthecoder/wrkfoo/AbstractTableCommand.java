package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;

public abstract class AbstractTableCommand<T extends Task, R> extends AbstractCommand<T> implements TableCommand<R>
{
    public AbstractTableCommand(T task)
    {
        super(task);
    }

    protected Columns<R> columns;


    public Columns<R> getColumns()
    {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

    protected abstract Columns<R> createColumns();



    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;

    }
    
    @Override
    public void detach()
    {
        super.detach();
        this.columns = null;
    }
    
    public abstract void updateResults();

    private Options options;

    public File getOptionsFile()
    {
        return Resources.instance.getOptionsFile(optionsName());
    }

    public Options getOptions()
    {
        if (options == null) {

            OptionsGroup og = new OptionsGroup();
            String name = optionsName();
            if (name != null) {
                og.add(Resources.instance.readOptions(name));
            }
            og.add(Resources.instance.globalOptions());
            options = og;
        }
        return options;
    }

}
