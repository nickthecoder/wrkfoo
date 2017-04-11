package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import groovy.lang.Binding;
import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.Focuser;
import uk.co.nickthecoder.wrkfoo.PanelResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.Tab;
import uk.co.nickthecoder.wrkfoo.option.GroovyScriptlet;
import uk.co.nickthecoder.wrkfoo.tool.Terminal.TerminalResults;
import uk.co.nickthecoder.wrkfoo.util.ProcessListener;
import uk.co.nickthecoder.wrkfoo.util.ProcessPoller;
import uk.co.nickthecoder.wrkfoo.util.SimpleTerminalWidget;

public class Terminal extends AbstractUnthreadedTool<TerminalResults, TerminalTask>
    implements ProcessListener
{
    public static Icon icon = Resources.icon("terminal.png");

    TerminalResults results;

    private JPanel terminal;

    private SimpleTerminalWidget simpleTerminal;

    private Process process;

    private ProcessPoller processPoller;

    private Command cmd;

    /**
     * Can we re-run this command? If it is a user-defined command, set using the TerminalTask's
     * parameters, then it can be re-run. However, if it was created from a Command object, then it cannot be
     * re-run.
     */
    private boolean reRunnable;

    public Terminal()
    {
        super(new TerminalTask(false));
        init();
        reRunnable = true;
    }

    public Terminal(Command command)
    {
        super(new TerminalTask(true));
        init();
        reRunnable = false;
        cmd = command;
    }

    private final void init()
    {
        results = new TerminalResults();
        results.getComponent().setLayout(new BorderLayout());

        task.directory.addListener(new ParameterListener()
        {
            @Override
            public void changed(Object initiator, Parameter source)
            {
                if (cmd != null) {
                    cmd.dir(task.directory.getValue());
                }
            }
        });
    }

    @Override
    public Terminal duplicate()
    {
        Terminal result = (Terminal) super.duplicate();
        result.cmd = cmd;
        result.reRunnable = reRunnable;
        if (task.findParameter("command") == null) {
            result.task.removeParameter(result.task.command);
        }
        return result;
    }

    /**
     * A Fluent API for setting the directory
     * 
     * @param directory
     * @return this
     */
    public Terminal dir(File directory)
    {
        task.directory.setValue(directory);
        return this;
    }

    /**
     * A Fluent API for setting the directory
     * 
     * @param path
     *            The directory's path
     * @return this
     */
    public Terminal dir(String path)
    {
        task.directory.setValue(new File(path));
        return this;
    }

    /**
     * A fluent API for choosing to use the simple terminal (instead of JediTermWidget).
     * 
     * @return this
     */
    public Terminal simple()
    {
        task.useSimpleTerminal.setValue(true);
        return this;
    }

    /**
     * A fluent API for setting the name, as it will appear in the tab.
     * 
     * @param value
     * @return this
     */
    public Terminal title(String value)
    {
        task.title.setValue(value);
        return this;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public String getTitle()
    {
        return task.title.getValue();
    }

    public boolean isRerunnable()
    {
        return reRunnable;
    }

    @Override
    public void updateResults()
    {
    }

    @Override
    public void go()
    {
        // When re-running this task, we need to reset
        processPoller = null;
        results.getComponent().removeAll();

        String[] commandArray;
        String directoryString;
        Map<String, String> env;

        if (cmd == null) {
            List<String> commandList = new ArrayList<>();
            commandList.add(task.command.getValue());
            commandList.addAll(task.arguments.getValue());
            commandArray = commandList.toArray(new String[] {});
            directoryString = task.directory.getValue().getPath();
            env = new HashMap<>(System.getenv());
        } else {
            commandArray = cmd.getCommandArray();
            directoryString = cmd.directory;
            env = cmd.env;
        }

        boolean useFallback = task.useSimpleTerminal.getValue();

        if (!useFallback) {
            try {
                Class.forName("com.jediterm.terminal.ui.JediTermWidget");
            } catch (ClassNotFoundException e1) {
                useFallback = true;
                System.err.println("JediTermWidget not found. Falling back to using the simple terminal.");
            }
        }

        if (useFallback) {

            simpleTerminal = createExecPanel(commandArray, env, directoryString);
            results.getComponent().add(simpleTerminal.getInputComponent(), BorderLayout.SOUTH);
            results.getComponent().add(simpleTerminal.getOutputComponent(), BorderLayout.CENTER);

        } else {

            boolean console = false;
            env.put("TERM", "xterm");
            Charset charset = Charset.forName("UTF-8");
            try {
                terminal = createTerminal(commandArray, env, directoryString, charset, console);
            } catch (IOException e) {
                e.printStackTrace();
            }
            results.getComponent().add(terminal);
        }

        ProcessPoller pp = getProcessPoller();
        pp.addProcessListener(this);

        Focuser.focusLater("Terminal just created", getResultsPanel().getFocusComponent(), 8);

        super.go();
    }

    @Override
    public TerminalResults createResultsPanel()
    {
        return results;
    }

    @Override
    public void detached()
    {
        if (terminal != null) {
            closeTerminal();
            terminal = null;
        }
        results.getComponent().removeAll();
        if (task.killOnClose.getValue()) {
            killProcess();
        }
    }

    @Override
    public void finished(Process process)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (getHalfTab() == null) {
                    return;
                }

                Tab tab = getHalfTab().getTab();

                if (tab.getMainTabs().getSelectedTab() == tab) {
                    Focuser.focusLater("TerminalEnded", getToolPanel().getToolBar().getOptionsTextField(), 6);
                }

                if (task.autoClose.getValue()) {
                    tab.removeHalfTab(getToolPanel().getHalfTab());
                }
            }
        });
    }

    public SimpleTerminalWidget createExecPanel(String[] commandArray, Map<String, String> env, String directoryString)
    {
        final Exec exec = new Exec(commandArray);
        if (directoryString != null) {
            exec.dir(new File(directoryString));
        }
        for (Entry<String, String> entry : env.entrySet()) {
            exec.var(entry.getKey(), entry.getValue());
        }

        SimpleTerminalWidget result = new SimpleTerminalWidget(exec);
        process = exec.getProcess();

        return result;
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

    public void killProcess()
    {
        if (process != null) {
            process.destroy();
        }
    }

    class TerminalResults extends PanelResults
    {
        public TerminalResults()
        {
            super(Terminal.this);
        }

        @Override
        public JComponent getFocusComponent()
        {
            if (terminal != null) {
                Focuser.log("Terminal.getFocusComponent - terminal");
                return terminal;
            } else if (simpleTerminal != null) {
                Focuser.log("Terminal.getFocusComponent - terminal");
                return simpleTerminal.getInputTextField();
            }
            Focuser.log("Terminal.getFocusComponent calling super");
            return super.getFocusComponent();
        }
    }

}
