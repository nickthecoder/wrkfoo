package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Project;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.ProjectsTask.ProjectFile;

public class ProjectsTask extends FileListerTask implements ListResults<ProjectFile>
{
    public List<ProjectFile> wrappedResults;

    public ProjectsTask()
    {
        super();
        for (Parameter parameter : parameters()) {
            parameter.visible = false;
        }

        directory.setValue(Resources.getInstance().getProjectsDirectory());
        directory.visible = true;

        fileExtensions.setValue("json");
        fileExtensions.visible = true;
    }

    @Override
    public List<ProjectFile> getResults()
    {
        return wrappedResults;
    }

    @Override
    public void body()
    {
        super.body();

        wrappedResults = new ArrayList<>();
        for (File file : results) {
            ProjectFile wrapped = new ProjectFile(file);
            Project tsd = Project.load(file);
            wrapped.description = tsd.description;

            wrappedResults.add(wrapped);
        }

    }

    public class ProjectFile extends WrappedFile
    {
        public String description;

        public ProjectFile(File file)
        {
            super(file);
        }

    }
}
