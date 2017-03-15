package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions.OptionRow;

public class WrkOptions extends AbstractListTool<WrkOptionsTask, OptionRow>
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
    protected Columns<OptionRow> createColumns()
    {
        Columns<OptionRow> columns = new Columns<>();

        columns.add(new Column<OptionRow>(String.class, "code")
        {
            @Override
            public String getValue(OptionRow row)
            {
                return row.option.code;
            }
        }.width(50));

        columns.add(new Column<OptionRow>(String.class, "label")
        {
            @Override
            public String getValue(OptionRow row)
            {
                return row.option.label;
            }
        }.width(200));

        columns.add(new Column<OptionRow>(String.class, "definedIn")
        {
            @Override
            public String getValue(OptionRow row)
            {
                return row.file.getName();
            }
        }.width(200).tooltip(2));

        columns.add(new Column<OptionRow>(File.class, "inFile")
        {
            @Override
            public File getValue(OptionRow row)
            {
                return row.file;
            }
        }.width(200).hide());

        columns.add(new Column<OptionRow>(String.class, "action")
        {
            @Override
            public String getValue(OptionRow row)
            {
                return row.option.action;
            }
        }.width(300).tooltip(3));

        columns.add(new Column<OptionRow>(Boolean.class, "row")
        {
            @Override
            public Boolean getValue(OptionRow row)
            {
                return row.option.isRow();
            }
        }.width(70));

        columns.add(new Column<OptionRow>(Boolean.class, "multi")
        {
            @Override
            public Boolean getValue(OptionRow row)
            {
                return row.option.multi;
            }
        }.width(70));

        columns.add(new Column<OptionRow>(Boolean.class, "newTab")
        {
            @Override
            public Boolean getValue(OptionRow row)
            {
                return row.option.newTab;
            }
        }.width(70));

        columns.add(new Column<OptionRow>(Boolean.class, "refresh")
        {
            @Override
            public Boolean getValue(OptionRow row)
            {
                return row.option.refreshResults;
            }
        }.width(70));

        columns.add(new Column<OptionRow>(String.class, "if")
        {
            @Override
            public String getValue(OptionRow row)
            {
                return row.option.ifScript;
            }
        }.width(300));

        return columns;
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("options.png");
    }

    public Task editOption(OptionData optionData)
    {
        return new EditOption(getTask().optionsData, optionData);
    }

    public Task addOption()
    {
        return new EditOption.AddOption(getTask().optionsData);
    }

    public Task copyOption(OptionData from)
    {
        EditOption.AddOption add = new EditOption.AddOption(getTask().optionsData);

        add.action.setValue(from.action);
        add.code.setValue(from.code);
        add.ifScript.setValue(from.ifScript);
        add.label.setValue(from.label);
        add.newTab.setValue(from.newTab);
        add.refreshResults.setValue(from.refreshResults);
        add.type.setValue(from.multi ? "multi" : from.row ? "row" : "non-row");

        return add;
    }

    public Task deleteOption(OptionData optionData)
    {
        return new EditOption.DeleteOption(getTask().optionsData, optionData);
    }

    public static class OptionRow
    {
        public OptionsData.OptionData option;
        public File file;

        public OptionRow(OptionsData.OptionData data, File file)
        {
            this.option = data;
            this.file = file;
        }
    }

}
