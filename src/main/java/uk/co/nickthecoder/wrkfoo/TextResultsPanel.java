package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

public class TextResultsPanel extends ResultsPanel
{
    public JTextArea textArea;

    public TextResultsPanel(String text)
    {
        super();
        this.textArea = new JTextArea( text );

        this.add(textArea, BorderLayout.CENTER);
    }
}
