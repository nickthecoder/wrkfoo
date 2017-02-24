package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import uk.co.nickthecoder.jguifier.util.AutoExit;

public class MainWindow extends JFrame
{    
    public MainWindow(CommandPanel panel)
    {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        
        panel.postCreate();
        setLocationRelativeTo(null);
        pack();
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        AutoExit.setVisible(show);
    }
}
