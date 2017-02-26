package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.command.WrkFCommand;
import uk.co.nickthecoder.wrkfoo.command.WrkFTreeCommand;

public class Example
{

    public static void main( String[] argv )
    {
        System.out.println( "WrkFCommand Example" );

        Util.defaultLookAndFeel();

        WrkFTreeCommand wrkFTreeSrc = new WrkFTreeCommand();
        wrkFTreeSrc.getTask().directory.setValue(new File("/home/nick/projects/wrkfoo/src"));

        WrkFCommand wrkFHome = new WrkFCommand();
        wrkFHome.getTask().directory.setValue(new File(System.getProperty("user.home")));

        WrkFCommand wrkFImages = new WrkFCommand();
        wrkFImages.getTask().directory.setValue(new File("/gidea/images"));
        
        WrkFCommand wrkFVideos= new WrkFCommand();
        wrkFVideos.getTask().directory.setValue(new File("/gidea/video/categories/TV Shows"));
        
        
        MainWindow mainWindow = new MainWindow( wrkFTreeSrc, wrkFHome, wrkFImages, wrkFVideos );
        mainWindow.setVisible(true);
        
    }
}


