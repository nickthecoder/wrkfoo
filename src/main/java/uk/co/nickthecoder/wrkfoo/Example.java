package uk.co.nickthecoder.wrkfoo;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.Home;

public class Example
{

    public static void main(String[] argv)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                System.out.println("WrkF Example");

                Util.defaultLookAndFeel();

                Home wrkTool = new Home();

                MainWindow mainWindow = new MainWindow();
                mainWindow.addTab(wrkTool);
                mainWindow.setVisible(true);

            }
        });

    }
}
