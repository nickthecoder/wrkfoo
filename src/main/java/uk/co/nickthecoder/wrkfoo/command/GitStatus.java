package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.command.GitStatusTask.GitStatusLine;

public class GitStatus extends ListCommand<GitStatusTask, GitStatusLine>
{

    public GitStatus()
    {
        super(new GitStatusTask());
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
