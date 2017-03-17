package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsTask.OptionRow;

public class WrkOptions extends AbstractListTool<WrkOptionsTask, OptionRow>
{
    public static Color EDITABLE_COLOR = Color.black;

    public static Color FIXED_COLOR = Color.red;

    public WrkOptions()
    {
        super(new WrkOptionsTask());
    }

    public WrkOptions(String optionsName)
    {
        super(new WrkOptionsTask(optionsName));
    }

    public WrkOptions(URL path, String name)
    {
        super(new WrkOptionsTask(path, name));
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
    protected ListTableModel<OptionRow> createTableModel()
    {
        ListTableModel<OptionRow> tableModel = new ListTableModel<OptionRow>(this,
            new ArrayList<OptionRow>(), getColumns())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Color getRowForeground(int row)
            {
                OptionRow line = getRow(row);

                if (line.url.getProtocol().equals("file")) {
                    return EDITABLE_COLOR;
                } else {
                    return FIXED_COLOR;
                }
            }
        };

        return tableModel;
    }
    
    @Override
    public Icon getIcon()
    {
        return Resources.icon("options.png");
    }

    @Override
    public String getShortTitle()
    {
        return task.optionsName.getValue();
    }

    @Override
    public String getLongTitle()
    {
        if (task.path.getValue() == null ) {
            return "<all> " + task.optionsName.getValue();
        } else {
            return task.path.getValue() + " " + task.optionsName.getValue();
        }
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
        add.type.setStringValue(from.multi ? "multi" : from.isRow() ? "row" : "non-row");

        return add;
    }

    public Task deleteOption(OptionsData optionsData, OptionData optionData)
    {
        return new EditOption.DeleteOption(optionsData, optionData);
    }

    public WrkOptionsIncludes wrkOptionsIncludes()
    {
        return new WrkOptionsIncludes(task.path.getValue(), task.optionsName.getValue());
    }

}
