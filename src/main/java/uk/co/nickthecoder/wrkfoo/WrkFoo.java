package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;

public class WrkFoo extends Task
{
    public FileParameter tabSet = new FileParameter.Builder("tabSet")
        .optional().file().mustExist()
        .parameter();

    @Override
    public void body()
    {
        if (tabSet.getValue() == null ) {
            MainWindow mainWindow = new MainWindow();
            mainWindow.onWorkTabSets();
            mainWindow.setVisible(true);
        } else {
            TabSetData.load(tabSet.getValue()).openMainWindow();
        }
    }

    public static void main(String[] argv)
    {
        Util.defaultLookAndFeel();

        WrkFoo wrkFoo = new WrkFoo();
        wrkFoo.go(argv);
    }

}
