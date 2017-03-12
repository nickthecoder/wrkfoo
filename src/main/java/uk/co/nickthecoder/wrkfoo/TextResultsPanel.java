package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

public class TextResultsPanel extends ResultsPanel
{
    private static final long serialVersionUID = 1L;

    public JTextArea textArea;

    public TextResultsPanel(String text)
    {
        super();
        this.textArea = new JTextArea(text);

        this.add(textArea, BorderLayout.CENTER);
    }
}
