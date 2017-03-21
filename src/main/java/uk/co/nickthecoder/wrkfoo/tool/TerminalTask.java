package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import groovy.lang.Binding;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.Sink;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.option.GroovyScriptlet;
import uk.co.nickthecoder.wrkfoo.util.ProcessPoller;

public class TerminalTask extends Task
{
    public StringParameter command = new StringParameter.Builder("command").multiLine()
        .value("/bin/bash\n--login\n")
        .parameter();

    public FileParameter directory = new FileParameter.Builder("directory")
        .value(new File("."))
        .parameter();

    public Command cmd;

    ResultsPanel panel;

    private JPanel terminal;

    private JTextArea textArea;

    private Process process;

    private ProcessPoller processPoller;

    public TerminalTask()
    {
        super();

        addParameters(command);
        init();
    }

    public TerminalTask(Command c)
    {
        super();
        cmd = c;
        init();
    }

    private final void init()
    {
        panel = new ResultsPanel();
        panel.setLayout(new BorderLayout());
    }

    public ResultsPanel createResultsComponent()
    {
        return panel;
    }

    @Override
    public void body()
    {
        panel.removeAll();

        String[] commandArray;
        String directoryString;
        Map<String, String> env;

        if (cmd == null) {
            commandArray = command.getValue().split("\n");
            directoryString = directory.getValue().getPath();
            env = new HashMap<>(System.getenv());
        } else {
            commandArray = cmd.getCommandArray();
            directoryString = cmd.directory;
            env = cmd.env;
        }

        try {
            boolean console = false;
            env.put("TERM", "xterm");
            Charset charset = Charset.forName("UTF-8");
            terminal = createTerminal(commandArray, env, directoryString, charset, console);
            panel.add(terminal);
            // TODO Bodge?
            panel.doLayout();
            terminal.repaint();

        } catch (Exception e) {

            System.err.println("Failed to start JeditTerm, falling back to a display-only terminal.");
            e.printStackTrace();

            createExecPanel(commandArray, env, directoryString);
            panel.add(textArea);
        }
    }

    public void createExecPanel(String[] commandArray, Map<String, String> env, String directoryString)
    {
        // JediTerm failed, so lets try Exec
        final Exec exec = new Exec(commandArray);
        if (directoryString != null) {
            exec.dir(new File(directoryString));
        }
        for (Entry<String, String> entry : env.entrySet()) {
            exec.var(entry.getKey(), entry.getValue());
        }
        exec.combineStdoutStderr();
        exec.runStreaming();
        process = exec.getProcess();

        exec.stdout(new TerminalSink());

        textArea = new JTextArea();

        Thread thread = new Thread(exec);
        thread.start();
    }

    private class TerminalSink implements Sink
    {
        Reader reader;

        @Override
        public void setStream(InputStream in)
        {
            reader = new InputStreamReader(in);
        }

        @Override
        public void run()
        {
            try {
                char[] buffer = new char[1000];
                int read = 0;
                while (read >= 0) {
                    read = reader.read(buffer);
                    if (read > 0) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(buffer, 0, read);
                        append(sb.toString());
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void append(final String str)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    textArea.append(str);
                }
            });
        }

    }

    private JPanel createTerminal(String[] cmd, Map<String, String> envs, String dir, Charset charset,
        boolean console) throws IOException
    {
        // Create the terminal widget dynamically using Groovy, so that the WrkFoo is not strongly tied
        // to the butt loads of jar files required by JediTerm. This means that WrkFoo can be compiled and used
        // without JediTerm, and only the terminal emulation will be missing.
        // When/if JediTerm is published to Maven Central or similar, then maybe we can include the dependencies.
        // Here's the java code that is replaced by Groovy...
        /*
         * PtyProcess process = PtyProcess.exec(cmd, envs, dir, console);
         * PtyProcessTtyConnector connector = new PtyProcessTtyConnector(process, charset);
         * DefaultSettingsProvider settings = new DefaultSettingsProvider();
         * 
         * JediTermWidget result = new JediTermWidget(settings);
         * 
         * TerminalSession session = result.createTerminalSession(connector);
         * session.start();
         * return result;
         */

        Binding bindings = new Binding();
        bindings.setProperty("cmd", cmd);
        bindings.setProperty("envs", envs);
        bindings.setProperty("dir", dir);
        bindings.setProperty("charset", charset);
        bindings.setProperty("console", console);

        GroovyScriptlet script1 = new GroovyScriptlet(
            "com.pty4j.PtyProcess process = com.pty4j.PtyProcess.exec(cmd, envs, dir, console);");
        process = (Process) script1.run(bindings);

        System.out.println("Set TerminalTask.process");

        bindings.setProperty("process", process);

        GroovyScriptlet script2 = new GroovyScriptlet("" +
            "com.jediterm.pty.PtyProcessTtyConnector connector = new com.jediterm.pty.PtyProcessTtyConnector(process, charset);"
            +
            "com.jediterm.terminal.ui.settings.DefaultSettingsProvider settings = new com.jediterm.terminal.ui.settings.DefaultSettingsProvider();"
            +
            "com.jediterm.terminal.ui.JediTermWidget result = new com.jediterm.terminal.ui.JediTermWidget(settings);"
            +
            "com.jediterm.terminal.ui.TerminalSession session = result.createTerminalSession(connector);"
            +
            "session.start();" +
            "result");

        JPanel panel = (JPanel) script2.run(bindings);

        return panel;
    }

    public ProcessPoller getProcessPoller()
    {
        if (processPoller == null) {
            System.out.println("Created poller");
            processPoller = new ProcessPoller(process);
            processPoller.start();
        }
        return processPoller;
    }

    private void closeTerminal()
    {
        Binding bindings = new Binding();
        bindings.setProperty("terminal", terminal);

        GroovyScriptlet script = new GroovyScriptlet("terminal.close();");
        script.run(bindings);
    }

    void detach()
    {
        if (terminal != null) {
            closeTerminal();
            terminal = null;
        }
        panel.removeAll();
    }
}