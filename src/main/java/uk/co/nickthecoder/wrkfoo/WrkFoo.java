package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.StringParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskCommand;
import uk.co.nickthecoder.jguifier.util.Util;

public class WrkFoo extends Task
{
    public static void assertIsEDT()
    {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Not running in the EDT");
        }
    }

    public FileParameter tabSetFile = new FileParameter.Builder("tabSetFile")
        .optional().file().mustExist()
        .parameter();

    public StringParameter tabSetName = new StringParameter.Builder("tabSetName")
        .optional()
        .parameter();

    public WrkFoo()
    {
        addParameters(tabSetFile, tabSetName);
    }

    @Override
    public void body()
    {
        if (tabSetFile.getValue() != null) {
            TabSetData.load(tabSetFile.getValue()).openMainWindow();

        } else if (tabSetName.getValue() != null) {
            File file = new File(Resources.instance.getTabsDirectory(), tabSetName.getValue());
            TabSetData.load(file).openMainWindow();

        } else {

            MainWindow mainWindow = new MainWindow();
            mainWindow.onWorkTabSets();
            mainWindow.setVisible(true);
        }
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
