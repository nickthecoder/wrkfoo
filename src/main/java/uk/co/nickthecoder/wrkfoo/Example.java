package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.tool.ScanF;
import uk.co.nickthecoder.wrkfoo.tool.WrkF;

public class Example
{

    public static void main(String[] argv)
    {
        System.out.println("WrkF Example");

        Util.defaultLookAndFeel();

        // WrkFTree wrkFTreeSrc = new WrkFTree();
        // wrkFTreeSrc.getTask().directory.setValue(new File("/home/nick/projects/wrkfoo/src"));

        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValue(new File(System.getProperty("user.home")));

        // WrkMounts wrkMounts = new WrkMounts();

        ScanF scanF = new ScanF();
        scanF.getTask().directory.setValue(new File("/home/nick/documents"));

        // WrkF wrkFImages = new WrkF();
        // wrkFImages.getTask().directory.setValue(new File("/gidea/images"));

        // WrkF wrkFVideos= new WrkF();
        // wrkFVideos.getTask().directory.setValue(new File("/gidea/video/categories/TV Shows"));

        Home wrkTool = new Home();

        MainWindow mainWindow = new MainWindow(wrkTool);
        // MainWindow mainWindow = new MainWindow( wrkFTreeSrc, wrkFHome, wrkFImages, wrkFVideos );
        mainWindow.setVisible(true);

    }
}
