package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.command.WrkFCommand;

public class Example
{

    public static void main( String[] argv )
    {
        System.out.println( "WrkFCommand Example" );

        Util.defaultLookAndFeel();

        WrkFCommand wrkFHome = new WrkFCommand();
        wrkFHome.getTask().directory.setValue(new File(System.getProperty("user.home")));
        wrkFHome.go();
        
        WrkFCommand wrkFImages = new WrkFCommand();
        wrkFImages.getTask().directory.setValue(new File("/gidea/images"));
        wrkFImages.go();
        
        WrkFCommand wrkFVideos= new WrkFCommand();
        wrkFVideos.getTask().directory.setValue(new File("/gidea/video/categories/TV Shows"));
        wrkFVideos.go();
        
        
        MainWindow mainWindow = new MainWindow( wrkFHome, wrkFImages, wrkFVideos );
        mainWindow.setVisible(true);
        
    }
}


