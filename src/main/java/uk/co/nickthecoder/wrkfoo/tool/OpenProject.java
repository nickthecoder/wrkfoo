package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.SpecialParameter;
import uk.co.nickthecoder.jguifier.util.FileLister;
import uk.co.nickthecoder.wrkfoo.Project;
import uk.co.nickthecoder.wrkfoo.Resources;

public class OpenProject extends Task
{
    private SpecialParameter<FileParameter, File> project = new SpecialParameter.Builder<FileParameter, File>("project")
        .choice("Pick a Project", null)
        .regular(new FileParameter.Builder("project").parameter())
        .parameter();

    public OpenProject()
    {
        super();
        addParameters(project);

        FileLister lister = new FileLister()
            .extension("json");
        List<File> files = lister.listFiles(Resources.getInstance().getProjectsDirectory());

        for (File file : files) {
            project.addChoice(file.getName(), file);
        }
    }

    @Override
    public void body()
    {
        File file = project.getValue();
        Project.load(file).openMainWindow();
    }

}
