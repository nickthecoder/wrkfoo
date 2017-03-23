package uk.co.nickthecoder.wrkfoo.util;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.SimpleSink;

public class SimpleTerminalWidget
{
    private JTextArea textArea;

    private JScrollPane scrollPane;

    public SimpleTerminalWidget(Exec exec)
    {
        textArea = new JTextArea();
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea);

        exec.combineStdoutStderr();
        exec.stdout(new TerminalSink());

        try {
            exec.runWithoutWaiting();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JComponent getOutputComponent()
    {
        return scrollPane;
    }
    
    private class TerminalSink extends SimpleSink
    {
        protected void sink(char[] buffer, int len) throws IOException
        {
            final StringBuffer sb = new StringBuffer();
            sb.append(buffer, 0, len);
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    textArea.append(sb.toString());
                }
            });
        }
    }
}
