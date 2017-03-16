package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.GitStatusTask.GitStatusLine;

public class GitStatus extends AbstractListTool<GitStatusTask, GitStatusLine> implements DirectoryTool
{
    public static Color UNTRACKED = new Color(16, 16, 160);

    public static Color NOT_UPDATED = new Color(160, 16, 16);

    public static Color UPDATED = new Color(16, 160, 16);

    public static Color RENAMED = new Color(16, 160, 16);

    public GitStatus()
    {
        super(new GitStatusTask());
    }

    @Override
    protected ListTableModel<GitStatusLine> createTableModel()
    {
        ListTableModel<GitStatusLine> tableModel = new ListTableModel<GitStatusLine>(this,
            new ArrayList<GitStatusLine>(), getColumns())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Color getRowForeground(int row)
            {
                GitStatusLine line = getRow(row);

                if (line.index == '?') {
                    return UNTRACKED;
                }

                if (line.work == 'M') {
                    return NOT_UPDATED;
                }

                if (line.index == 'R') {
                    return RENAMED;
                }

                if (line.index == 'M') {
                    return UPDATED;
                }
                return Color.black;
            }
        };

        return tableModel;
    }

    @Override
    protected Columns<GitStatusLine> createColumns()
    {
        Columns<GitStatusLine> columns = new Columns<>();

        columns.add(new Column<GitStatusLine>(Character.class, "i")
        {
            @Override
            public Character getValue(GitStatusLine row)
            {
                return row.index;
            }

        }.width(30).lock());

        columns.add(new Column<GitStatusLine>(Character.class, "w")
        {
            @Override
            public Character getValue(GitStatusLine row)
            {
                return row.work;
            }

        }.width(30).lock());

        columns.add(new Column<GitStatusLine>(String.class, "name")
        {
            @Override
            public String getValue(GitStatusLine row)
            {
                return row.name;
            }

        }.tooltip(4).width(300));

        columns.add(new Column<GitStatusLine>(String.class, "path")
        {
            @Override
            public String getValue(GitStatusLine row)
            {
                return row.path;
            }

        }.tooltip(4).sort().width(500));

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
    public Icon getIcon()
    {
        return Resources.icon("git.png");
    }

    @Override
    public File getDirectory()
    {
        return task.directory.getValue();
    }
}
