package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskCommand;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.MultipleParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.Home;

public class WrkFoo extends Task
{
    public static void assertIsEDT()
    {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Not running in the EDT");
        }
    }

    /**
     * I use this when debugging code, instead of System.out.println.
     * When the bug is squashed, I can then find all calls to this method, and remove them.
     * Note. This isn't a substitute for logging, it is only used for short-term debugging,
     * that you would not want in your debug logs.
     * 
     * It also has the advantage of being able to print many objects without string concatenation.
     */
    public static void println(Object... objects)
    {
        boolean first = true;

        for (Object o : objects) {
            if (!first) {
                System.out.print(", ");
            } else {
                first = false;
            }
            System.out.print(o.toString());
        }
        System.out.println();
    }

    public FileParameter settings = new FileParameter.Builder("settings").file().mustExist()
        .description("Default is ~/.config/wrkfoo/settings.json")
        .optional().parameter();

    public MultipleParameter<FileParameter, File> projectFile = new FileParameter.Builder("")
        .file().mustExist()
        .multipleParameter("projectFile");

    public MultipleParameter<StringParameter, String> project = new StringParameter.Builder("")
        .multipleParameter("project");

    public WrkFoo()
    {
        addParameters(settings, project, projectFile);
    }

    @Override
    public MultipleParameter<StringParameter, String> getTrailingParameter()
    {
        return project;
    }

    @Override
    public void body()
    {
        if (settings.getValue() != null) {
            Resources.settingsFile = settings.getValue();
        }

        for (File file : projectFile.getValue()) {
            Project.load(file).openMainWindow();

        }

        for (String name : project.getValue()) {

            File file = new File(Resources.getInstance().getProjectsDirectory(), name + ".json");
            Project.load(file).openMainWindow();
        }

        if ((project.getValue().size() == 0) && (projectFile.getValue().size() == 0)) {
            MainWindow mainWindow = new MainWindow();

            Home tool = new Home();
            mainWindow.getCurrentOrNewTab().getMainHalfTab().go(tool);

            mainWindow.setVisible(true);
        }
    }

    public static void newWindow(final Tool<?> tool)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                Util.defaultLookAndFeel();

                MainWindow mainWindow = new MainWindow();
                mainWindow.addTab(tool);
                mainWindow.setVisible(true);
            }
        });
    }

    public static void main(final String[] argv)
    {
        try {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                @Override
                public void run()
                {
                    Util.defaultLookAndFeel();

                    WrkFoo wrkFoo = new WrkFoo();
                    try {
                        new TaskCommand(wrkFoo).neverExit().go(argv);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
