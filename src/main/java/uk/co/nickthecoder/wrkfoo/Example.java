package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.wrkfoo.file.WrkF;

public class Example
{

    public static void main( String[] argv )
    {
        System.out.println( "WrkF Example" );

        WrkF wrkF = new WrkF();
        wrkF.getTask().parseArgs( argv, false );
        WrkF.wrkFTask.directory.setValue(new File("."));
        
        MainWindow mainWindow = new MainWindow( wrkF );
        mainWindow.setVisible(true);
        mainWindow.go();
    }
}


