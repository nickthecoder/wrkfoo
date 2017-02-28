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

        //WrkFTree wrkFTreeSrc = new WrkFTree();
        //wrkFTreeSrc.getTask().directory.setValue(new File("/home/nick/projects/wrkfoo/src"));

        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValue(new File(System.getProperty("user.home")));

        //WrkF wrkFImages = new WrkF();
        //wrkFImages.getTask().directory.setValue(new File("/gidea/images"));
        
        //WrkF wrkFVideos= new WrkF();
        //wrkFVideos.getTask().directory.setValue(new File("/gidea/video/categories/TV Shows"));
        
        
        MainWindow mainWindow = new MainWindow( wrkFHome );
        //MainWindow mainWindow = new MainWindow( wrkFTreeSrc, wrkFHome, wrkFImages, wrkFVideos );
        mainWindow.setVisible(true);
        
    }
}


