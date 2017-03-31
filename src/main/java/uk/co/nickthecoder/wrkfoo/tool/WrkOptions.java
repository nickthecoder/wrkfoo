package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.FakeToolPanel;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.PanelResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.SimpleTable;
import uk.co.nickthecoder.wrkfoo.TableResults;
import uk.co.nickthecoder.wrkfoo.TaskResults;
import uk.co.nickthecoder.wrkfoo.ToolPanel;
import uk.co.nickthecoder.wrkfoo.TopLevel;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions.WrkOptionsResults;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsTask.OptionRow;

public class WrkOptions extends AbstractListTool<WrkOptionsResults, WrkOptionsTask, OptionRow>
{
    public static Color EDITABLE_COLOR = Color.BLACK;

    public static Color FIXED_COLOR = new Color(128, 0, 0); // Dark red

    private ResultsTask resultsTask = new ResultsTask();

    private WrkOptionsIncludes includesTool;

    public WrkOptions()
    {
        super(new WrkOptionsTask());
        init();
    }

    public WrkOptions(String optionsName)
    {
        super(new WrkOptionsTask(optionsName));
        init();
    }

    public WrkOptions(URL path, String name)
    {
        super(new WrkOptionsTask(path, name));
        init();
    }

    private final void init()
    {
        includesTool = new WrkOptionsIncludes()
        {
            @Override
            protected ToolPanel createToolPanel()
            {
                return new FakeToolPanel() {
                    @Override
                    public TopLevel getTopLevel()
                    {
                        return WrkOptions.this.getToolPanel().getTopLevel();
                    }
                };
            }
        };
    }

    public WrkOptionsIncludes getIncludesTool()
    {
        return includesTool;
    }

    @Override
    public void go()
    {
        super.go();

        includesTool.getTask().path.setValue(getTask().path.getValue());
        includesTool.getTask().optionsName.setValue(getTask().optionsName.getValue());

        includesTool.go();
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
        }.width(80));

        columns.add(new Column<OptionRow>(String.class, "label")
        {
            @Override
            public String getValue(OptionRow row)
            {
                return row.option.label;
            }
        }.width(200));

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
        }.width(90));

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
        if (task.path.getValue() == null) {
            return "<all> " + task.optionsName.getValue();
        } else {
            return task.path.getValue() + " " + task.optionsName.getValue();
        }
    }

    public Task editOption(OptionsData optionsData, OptionData optionData)
    {
        return new EditOption(optionsData, optionData);
    }

    public Task addOption() throws MalformedURLException, IOException, URISyntaxException
    {
        if (task.path.getValue() == null) {
            return new EditOption.AddOption(task.optionsName.getValue());
        } else {
            return new EditOption.AddOption(task.path.getValue(), task.optionsName.getValue());
        }
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

    @Override
    public SimpleTable<OptionRow> getTable()
    {
        return getResultsPanel().tableResults.getTable();
    }

    @Override
    protected WrkOptionsResults createResultsPanel()
    {
        return new WrkOptionsResults();
    }

    public class WrkOptionsResults extends PanelResults
    {
        TaskResults taskResults;

        TableResults<OptionRow> tableResults;

        TableResults<String> includesTableResults;

        public WrkOptionsResults()
        {
            super(WrkOptions.this);
            SimpleTable<OptionRow> table = getColumns().createTable(getTableModel());

            taskResults = new TaskResults(WrkOptions.this, resultsTask);
            tableResults = new TableResults<OptionRow>(WrkOptions.this, table);

            includesTableResults = getIncludesTool().getResultsPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.7);

            getComponent().add(taskResults.getComponent(), BorderLayout.NORTH);
            getComponent().add(splitPane, BorderLayout.CENTER);

            splitPane.setTopComponent(tableResults.getComponent());
            splitPane.setBottomComponent(includesTableResults.getComponent());
        }

        @Override
        public JComponent getFocusComponent()
        {
            return tableResults.getFocusComponent();
        }
    }

    public class ResultsTask extends Task
    {
        public StringParameter ifScript = new StringParameter.Builder("if")
            .optional().multiLine().size(300, 50).parameter();

        public ResultsTask()
        {
            addParameters(ifScript);
        }

        @Override
        public void body() throws Exception
        {
            getTask().optionsData.save();
            getTask().optionsData.reload();
        }

    }
}
