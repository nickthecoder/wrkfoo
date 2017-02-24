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

        WrkF wrkF = new WrkF();
        wrkF.getTask().parseArgs( argv, false );
        wrkF.getTask().directory.setValue(new File("."));
        
        CommandPanel<File> panel = wrkF.getCommandPanel();
        MainWindow mainWindow = new MainWindow( panel );
        mainWindow.setVisible(true);
        panel.go();
    }
}


