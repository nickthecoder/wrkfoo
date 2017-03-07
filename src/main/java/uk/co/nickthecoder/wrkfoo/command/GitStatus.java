package uk.co.nickthecoder.wrkfoo.command;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.AbstractListCommand;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.command.GitStatusTask.GitStatusLine;

public class GitStatus extends AbstractListCommand<GitStatusTask, GitStatusLine>
{
    public static Color UNTRACKED = new Color(255, 255, 128);

    public static Color NOT_UPDATED = new Color(255, 128, 128);

    public static Color UPDATED = new Color(128, 255, 128);
    
    public static Color RENAMED = new Color(128, 200, 128);

    public GitStatus()
    {
        super(new GitStatusTask());
    }

    protected ListTableModel<GitStatusLine> createTableModel()
    {
        ListTableModel<GitStatusLine> tableModel = new ListTableModel<GitStatusLine>(this,
            new ArrayList<GitStatusLine>(), getColumns())
        {
            @Override
            public Color getRowBackground(int row)
            {
                // SimpleTable table = GitStatus.this.getCommandPanel().table;

                GitStatusLine line = list.get(row);

                if (line.x == '?') {
                    return UNTRACKED;
                }

                if ((line.y == 'M') && (line.x == ' ')) {
                    return NOT_UPDATED;
                }

                if (line.x == 'R') {
                    return RENAMED;
                }

                if (line.x == 'M') {
                    return UPDATED;
                }

                return Color.white;
            }
        };

        return tableModel;
    }

    @Override
    protected Columns<GitStatusLine> createColumns()
    {
        Columns<GitStatusLine> columns = new Columns<GitStatusLine>();

        columns.add(new Column<GitStatusLine>(Character.class, "index")
        {
            @Override
            public Character getValue(GitStatusLine row)
            {
                return row.x;
            }

        }.width(10));

        columns.add(new Column<GitStatusLine>(Character.class, "work")
        {
            @Override
            public Character getValue(GitStatusLine row)
            {
                return row.y;
            }

        }.width(10));

        columns.add(new Column<GitStatusLine>(String.class, "name")
        {
            @Override
            public String getValue(GitStatusLine row)
            {
                return row.name;
            }

        }.tooltip(4).sort().width(250));

        columns.add(new Column<GitStatusLine>(String.class, "path")
        {
            @Override
            public String getValue(GitStatusLine row)
            {
                return row.path;
            }

        }.tooltip(4).hide().width(50));

        columns.add(new Column<GitStatusLine>(File.class, "renamedFrom")
        {
            @Override
            public File getValue(GitStatusLine row)
            {
                return row.getRenamedFile();
            }

        }.tooltip(5).width(50));

        return columns;
    }

    @Override
    protected String optionsName()
    {
        return "gitstatus";
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("git.png");
    }
}
