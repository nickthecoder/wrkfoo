package uk.co.nickthecoder.wrkfoo.option;

import java.util.List;

import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.TableCommand;

public interface Option
{    
    public String getCode();
    
    public String getLabel();

    public void runMultiOption( TableCommand<?> command, List<Object> row, boolean newTab );
    
    public void runOption( Command command, boolean newTab );

    public void runOption( TableCommand<?> command, Object row, boolean newTab );

    public boolean isApplicable( Object row );
    
    public boolean isRow();
    
    public boolean isMultiRow();
}
