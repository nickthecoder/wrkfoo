package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabSetData;
import uk.co.nickthecoder.wrkfoo.command.WrkTabSetsTask.WrkTabSetsFile;

public class WrkTabSets extends ListCommand<WrkTabSetsTask, WrkTabSetsFile>
{
    public WrkTabSets()
    {
        super(new WrkTabSetsTask());
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("workTabSets.png");
    }

    @Override
    public String getTitle()
    {
        return "Tab Sets";
    }

    @Override
    public Columns<WrkTabSetsFile> createColumns()
    {
        Columns<WrkTabSetsFile> columns = new Columns<WrkTabSetsFile>();

        columns = new Columns<WrkTabSetsFile>();

        columns.add(new Column<WrkTabSetsFile>(String.class, "name")
        {
            @Override
            public String getValue(WrkTabSetsFile row)
            {
                return row.file.getName();
            }
        }.width(100));

        columns.add(new Column<WrkTabSetsFile>(String.class, "description")
        {
            @Override
            public String getValue(WrkTabSetsFile row)
            {
                return row.description;
            }
        }.width(500));

        return columns;
    }

    @Override
    protected String optionsName()
    {
        return "wrktabsets";
    }

    public void load( File file )
    {
        TabSetData.load(file).openMainWindow();
    }
}
