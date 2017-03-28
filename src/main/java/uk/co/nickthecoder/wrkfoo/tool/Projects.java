package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.DragFileConverter;
import uk.co.nickthecoder.wrkfoo.Project;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.ProjectsTask.ProjectFile;

public class Projects extends AbstractListTool<ProjectsTask, ProjectFile> implements DirectoryTool
{
    public Projects()
    {
        super(new ProjectsTask());
        dragListConverter = new DragFileConverter<ProjectFile>();
    }

    @Override
    public Columns<ProjectFile> createColumns()
    {
        Columns<ProjectFile> columns = new Columns<>();

        columns = new Columns<>();

        columns.add(new Column<ProjectFile>(String.class, "name")
        {
            @Override
            public String getValue(ProjectFile row)
            {
                return row.file.getName();
            }
        }.width(100));

        columns.add(new Column<ProjectFile>(String.class, "description")
        {
            @Override
            public String getValue(ProjectFile row)
            {
                return row.description;
            }
        }.width(500));

        return columns;
    }

    public void load(File file)
    {
        Project.load(file).openMainWindow();
    }

    @Override
    public File getDirectory()
    {
        return Resources.getInstance().getProjectsDirectory();
    }
}
