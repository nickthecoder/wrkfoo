package uk.co.nickthecoder.wrkfoo.util;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.SimplePrintSource;
import uk.co.nickthecoder.jguifier.util.SimpleSink;

public class SimpleTerminalWidget implements ProcessListener
{
    private Exec exec;
    
    private JTextPane textArea;

    private JScrollPane scrollPane;

    private JPanel inputArea;

    private JTextField input;

    private SimplePrintSource printSource;

    public SimpleTerminalWidget(final Exec exec)
    {
        this.exec = exec;
        
        textArea = new JTextPane();
        textArea.setEditable(false);

        StyledDocument doc = textArea.getStyledDocument();

        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        doc.addStyle("output", defaultStyle);
        Style inputStyle = doc.addStyle("input", defaultStyle);
        StyleConstants.setBold(inputStyle, true);

        Style errorStyle = doc.addStyle("error", defaultStyle);
        StyleConstants.setItalic(errorStyle, true);

        scrollPane = new JScrollPane(textArea);

        inputArea = new JPanel();
        inputArea.setLayout(new BorderLayout());
        input = new JTextField();
        inputArea.add(input, BorderLayout.CENTER);

        ActionBuilder builder = new ActionBuilder(this).component(inputArea);
        
        JButton goButton = builder.name("SimpleTerminalWidget.go").label("Go").buildButton();
        inputArea.add(goButton, BorderLayout.EAST);

        JButton terminateButton = builder.name("SimpleTerminalWidget.terminate").label("Terminate").buildButton();
        inputArea.add(terminateButton, BorderLayout.WEST);

        exec.mergeStderr();
        exec.stdout(new TerminalSink());

        printSource = new SimplePrintSource();

        exec.stdin(printSource);

        try {
            exec.runWithoutWaiting();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessPoller poller = new ProcessPoller(exec.getProcess());
        poller.addProcessListener(this);
        poller.start();
    }

    public void onGo()
    {
        String text = input.getText();
        printSource.out.println(text);
        printSource.out.flush();
        input.setText("");
        append(text + "\n", "input");
    }

    public void onTerminate()
    {
        printSource.out.close();
        exec.getProcess().destroy();
    }

    public JComponent getOutputComponent()
    {
        return scrollPane;
    }

    public JComponent getInputComponent()
    {
        return inputArea;
    }

    public JComponent getInputTextField()
    {
        return input;
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
                    append(sb.toString(), "ouput");
                }
            });
        }
    }

    private void append(String text, String style)
    {
        StyledDocument doc = textArea.getStyledDocument();

        try {
            doc.insertString(doc.getLength(), text, doc.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finished(Process process)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                append("\n<ended>\n", "error");
                inputArea.setVisible(false);
            }
        });
    }
}
