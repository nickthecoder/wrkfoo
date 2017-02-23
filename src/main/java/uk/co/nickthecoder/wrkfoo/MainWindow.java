package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.util.AutoExit;

public class MainWindow extends JFrame
{
    private static final long serialVersionUID = 1L;

    private WrkFoo<?> wrkFoo;
    
    private JPanel header;
    
    private JPanel body;
    
    private JButton goButton;
    
    public MainWindow( WrkFoo<?> wrkFoo )
    {
        this.wrkFoo = wrkFoo;
        this.setTitle(wrkFoo.getTitle());
        
        header = new JPanel();
        body = new JPanel();
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add( header, BorderLayout.NORTH);
        getContentPane().add( body, BorderLayout.CENTER);
        
        goButton = new JButton( "Go" );
        goButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });
        header.add(goButton);
        
        setLocationRelativeTo(null);
        pack();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void go()
    {
        System.out.println( "MainWindow.go" );
        wrkFoo.run();
        System.out.println( "Completed run" );
        
        Results results = wrkFoo.getResults();
        for (Iterator i = results.rows(); i.hasNext(); ) {
            Object row = i.next();
            
            for (Column column : wrkFoo.getColumns()) {
                System.out.print( column.getValue(row));
                System.out.print( " " );
            }
            System.out.println();
        }
        System.out.println( "Mainwindow.go ended" );
    }
    
    public void setVisible( boolean show )
    {
        super.setVisible(show);
        AutoExit.setVisible( show );
    }
}
