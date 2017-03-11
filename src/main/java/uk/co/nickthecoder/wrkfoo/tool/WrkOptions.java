package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;

public class WrkOptions extends AbstractListTool<WrkOptionsTask, OptionData>
{
    public static WrkOptionsTask createTask(File file)
    {
        WrkOptionsTask task = new WrkOptionsTask();
        task.optionsFile.setDefaultValue(file);
        return task;
    }

    public WrkOptions()
    {
        super(new WrkOptionsTask());
    }
    
    public WrkOptions(File optionsFile)
    {
        super(createTask(optionsFile));
    }

    @Override
    public String getLongTitle()
    {
        try {
            return "Options : " + task.optionsFile.getValue().getName();
        } catch (Exception e) {
            return super.getLongTitle();
        }
    }
    
    @Override
    public String getShortTitle()
    {
        try {
            return task.optionsFile.getValue().getName();
        } catch (Exception e) {
            return super.getShortTitle();
        }
    }
        
    @Override
    protected Columns<OptionData> createColumns()
    {
        Columns<OptionData> columns = new Columns<OptionData>();

        columns.add(new Column<OptionData>(String.class, "code")
        {
            @Override
            public String getValue(OptionData row)
            {
                return row.code;
            }
        }.width(50));

        columns.add(new Column<OptionData>(String.class, "label")
        {
            @Override
            public String getValue(OptionData row)
            {
                return row.label;
            }
        }.width(200));

        columns.add(new Column<OptionData>(String.class, "action")
        {
            @Override
            public String getValue(OptionData row)
            {
                return row.action;
            }
        }.width(300).tooltip(3));

        columns.add(new Column<OptionData>(Boolean.class, "row")
        {
            @Override
            public Boolean getValue(OptionData row)
            {
                return row.isRow();
            }
        }.width(70));
        
        columns.add(new Column<OptionData>(Boolean.class, "multi")
        {
            @Override
            public Boolean getValue(OptionData row)
            {
                return row.multi;
            }
        }.width(70));
        
        columns.add(new Column<OptionData>(String.class, "if")
        {
            @Override
            public String getValue(OptionData row)
            {
                return row.ifScript;
            }
        }.width(300));

        return columns;
    }


    @Override
    public Icon getIcon()
    {
        return Resources.icon("options.png");
    }
    
    public Task editOption( OptionData optionData )
    {
        return new EditOption( getTask().optionsData, optionData ).neverExit();
    }
    
    public Task addOption(  )
    {
        return new EditOption.AddOption( getTask().optionsData ).neverExit();
    }
    
    public Task deleteOption( OptionData optionData )
    {
        return new EditOption.DeleteOption( getTask().optionsData, optionData).neverExit();
    }

}
