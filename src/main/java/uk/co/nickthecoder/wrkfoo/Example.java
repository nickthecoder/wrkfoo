package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.command.WrkF;

public class Example
{

    public static void main( String[] argv )
    {
        System.out.println( "WrkF Example" );

        Util.defaultLookAndFeel();

        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValue(new File(System.getProperty("user.home")));
        wrkFHome.go();
        
        WrkF wrkFImages = new WrkF();
        wrkFImages.getTask().directory.setValue(new File("/gidea/images"));
        wrkFImages.go();
        
        WrkF wrkFVideos= new WrkF();
        wrkFVideos.getTask().directory.setValue(new File("/gidea/video/categories/TV Shows"));
        wrkFVideos.go();
        
        
        MainWindow mainWindow = new MainWindow( wrkFHome, wrkFImages, wrkFVideos );
        mainWindow.setVisible(true);
        
    }
}


