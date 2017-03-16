package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.net.URL;

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
    public WrkOptions()
    {
        super(new WrkOptionsTask());
    }

    public WrkOptions(String optionsName)
    {
        super(new WrkOptionsTask(optionsName));
    }

    public WrkOptions(File file)
    {
        super(new WrkOptionsTask(file));
    }

    @Override
    public String getLongTitle()
    {
        try {
            return "Options : " + task.optionsName.getValue();
        } catch (Exception e) {
            return super.getLongTitle();
        }
    }

    @Override
    public String getShortTitle()
    {
        try {
            return task.optionsName.getValue();
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
                String path = row.url.getPath();
                int slash = path.lastIndexOf("/");
                if (slash > 0) {
                    return path.substring(slash + 1);
                } else {
                    return path;
                }
            }
        }.width(200).tooltip(4));

        columns.add(new Column<OptionRow>(URL.class, "URL")
        {
            @Override
            public URL getValue(OptionRow row)
            {
                return row.url;
            }
        }.width(200).tooltip(4).hide());

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
        return Resources.icon("optionData.png");
    }

    public Task editOption(OptionsData optionsData, OptionData optionData)
    {
        return new EditOption(optionsData, optionData);
    }

    public Task addOption()
    {
        return new EditOption.AddOption(task.optionsName.getValue());
    }

    public Task copyOption(OptionData from)
    {
        EditOption.AddOption add = new EditOption.AddOption(task.optionsName.getValue());

        add.action.setValue(from.action);
        add.code.setValue(from.code);
        add.ifScript.setValue(from.ifScript);
        add.label.setValue(from.label);
        add.newTab.setValue(from.newTab);
        add.refreshResults.setValue(from.refreshResults);
        add.type.setValue(from.multi ? "multi" : from.row ? "row" : "non-row");

        return add;
    }

    public Task deleteOption(OptionsData optionsData, OptionData optionData)
    {
        return new EditOption.DeleteOption(optionsData, optionData);
    }

    public static class OptionRow
    {
        public OptionsData options;
        public OptionsData.OptionData option;
        public URL url;

        public OptionRow(OptionsData optionsData, OptionsData.OptionData data, URL url)
        {
            this.options = optionsData;
            this.option = data;
            this.url = url;
        }

        public boolean canEdit()
        {
            return url.getProtocol().equals("file");
        }
    }

}
