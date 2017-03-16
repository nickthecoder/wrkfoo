package uk.co.nickthecoder.wrkfoo.option;

import java.util.List;

import uk.co.nickthecoder.wrkfoo.TableTool;
import uk.co.nickthecoder.wrkfoo.Tool;

public interface Option
{
    public String getCode();

    public String getLabel();

    public void runMultiOption(TableTool<?> tool, List<Object> row, boolean newTab);

    public void runOption(Tool tool, boolean newTab);

    public void runOption(TableTool<?> tool, Object row, boolean newTab);

    public boolean isApplicable(Object row);

    public boolean isRow();

    public boolean isMultiRow();
    
    public boolean getNewTab();
        
    public boolean getRefreshResults();

    public boolean getPrompt();
}
