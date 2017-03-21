package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import groovy.lang.Binding;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.option.GroovyScriptlet;

public class TerminalTask extends Task
{
    public StringParameter command = new StringParameter.Builder("command").multiLine()
        .value("/bin/bash\n--login\n")
        .parameter();

    public FileParameter directory = new FileParameter.Builder("directory")
        .value(new File("."))
        .parameter();

    JPanel termWidget;

    public TerminalTask()
    {
        super();

        addParameters(command);
    }

    @Override
    public void body()
    {
        String[] cmd = command.getValue().split("\n");
        String dir = directory.getValue().getPath();
        boolean console = false;

        Map<String, String> envs = new HashMap<>();
        envs.put("TERM", "xterm");
        Charset charset = Charset.forName("UTF-8");

        try {
            termWidget = createTerminal(cmd, envs, dir, charset, console);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static JPanel createTerminal(String[] cmd, Map<String, String> envs, String dir, Charset charset,
        boolean console) throws IOException
    {
        // Create the terminal widget dynamically using Groovy, so that the WrkFoo is not strongly tied
        // to the butt loads of jar files required by JediTerm. This means that WrkFoo can be compiled and used
        // without JediTerm, and only the terminal emulation will be missing.
        // When/if JediTerm is published to Maven Central or similar, then maybe we can include the dependencies.
        // Here's the java code that is replaced by Groovy...
        /*
        PtyProcess process = PtyProcess.exec(cmd, envs, dir, console);
        PtyProcessTtyConnector connector = new PtyProcessTtyConnector(process, charset);
        DefaultSettingsProvider settings = new DefaultSettingsProvider();

        JediTermWidget result = new JediTermWidget(settings);

        TerminalSession session = result.createTerminalSession(connector);
        session.start();
        return result;
         */
        
        Binding bindings = new Binding();
        bindings.setProperty("cmd", cmd);
        bindings.setProperty("envs", envs);
        bindings.setProperty("dir", dir);
        bindings.setProperty("charset", charset);
        bindings.setProperty("console", console);

        GroovyScriptlet script = new GroovyScriptlet("" +
            "com.pty4j.PtyProcess process = com.pty4j.PtyProcess.exec(cmd, envs, dir, console);"
            +
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
        
        JPanel panel = (JPanel) script.run(bindings);

        return panel;
    }

}
