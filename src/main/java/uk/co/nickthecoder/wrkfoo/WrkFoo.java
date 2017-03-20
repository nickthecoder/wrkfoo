package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskCommand;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Util;

public class WrkFoo extends Task
{
    public static void assertIsEDT()
    {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Not running in the EDT");
        }
    }

    public FileParameter settings = new FileParameter.Builder("settings").file().mustExist()
        .description("Default is ~/.config/wrkfoo/settings.json")
        .optional().parameter();

    public FileParameter tabsFile = new FileParameter.Builder("tabsFile")
        .optional().file().mustExist()
        .parameter();

    public StringParameter tabsName = new StringParameter.Builder("tabsName")
        .optional()
        .parameter();

    public WrkFoo()
    {
        addParameters(settings, tabsFile, tabsName);
    }

    @Override
    public void body()
    {
        if (settings.getValue() != null) {
            Resources.settingsFile = settings.getValue();
        }

        if (tabsFile.getValue() != null) {
            TabSetData.load(tabsFile.getValue()).openMainWindow();

        } else if (tabsName.getValue() != null) {
            File file = new File(Resources.getInstance().getTabsDirectory(), tabsName.getValue() + ".json");
            TabSetData.load(file).openMainWindow();

        } else {

            MainWindow mainWindow = new MainWindow();
            mainWindow.onWorkTabSets();
            mainWindow.setVisible(true);
        }
    }

    public static void newWindow( final Tool tool )
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                Util.defaultLookAndFeel();

                MainWindow mainWindow = new MainWindow();
                mainWindow.addTab( tool );
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
