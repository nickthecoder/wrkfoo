package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;

public interface Command<R>
{
    public void postCreate();

    public Task getTask();
    
    public String getTitle();

    public Icon getIcon();

    public GroupParameter getParameters();

    
    public void attachTo( CommandTab tab );
    
    public void detach();
    
    public CommandTab getCommandTab();
    
    public Results<R> getResults();
    
    public void updateResults();

    public CommandTableModel<R> getTableModel();
    
    public SimpleTable<R> createTable();
    
    
    
    public ParametersPanel createParametersPanel();
    
    public CommandPanel<R> getCommandPanel();
        
    public void defaultAction( R row );
    
    
    
    public void go();
    
    
    public Options getOptions();
    
}
