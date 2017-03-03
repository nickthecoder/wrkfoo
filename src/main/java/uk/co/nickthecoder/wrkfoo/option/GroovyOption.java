package uk.co.nickthecoder.wrkfoo.option;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.CommandTab;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.util.OSCommand;

public class GroovyOption extends AbstractOption
{
    private static GroovyShell createShell()
    {
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(
            "uk.co.nickthecoder.wrkfoo",
            "uk.co.nickthecoder.wrkfoo.command",
            "uk.co.nickthecoder.wrkfoo.util",
            "uk.co.nickthecoder.jguifier.util");
        importCustomizer.addStaticImport(OSCommand.class.getName(),"command");
        importCustomizer.addStaticImport(OSCommand.class.getName(),"edit");

        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(configuration);
    }

    private String groovySource;

    private Script groovyScript;

    public GroovyOption(String code, String label, String script, boolean isRow, boolean isMulti)
    {
        super(code, label, isRow, isMulti);
        this.groovySource = script;
    }

    @Override
    public void runMultiOption(Command<?> command, List<Object> rows, boolean openNewTab)
    {
        privateRunOption( command, true, rows, openNewTab);
    }
    
    public void runOption(Command<?> command, Object row, boolean openNewTab)
    {
        privateRunOption( command, false, row, openNewTab);        
    }
    
    private void privateRunOption(Command<?> command, boolean isMulti, Object rowOrRows, boolean openNewTab)
    {
        if (groovyScript == null) {
            groovyScript = createShell().parse(groovySource);
        }

        CommandTab tab = command.getCommandTab();

        // The new tab cannot share the same Command as the current tab, so create a copy first
        // just in case the option reuses the command.
        if (openNewTab) {
            command = command.clone();
        }

        Binding bindings = new Binding();
        bindings.setProperty("command", command);
        bindings.setProperty("task", command.getTask());
        
        if (isMulti) {
            bindings.setProperty("rows", rowOrRows);
        } else {
            bindings.setProperty("row", rowOrRows);
        }
        
        groovyScript.setBinding(bindings);

        Object result = groovyScript.run();

        if (result instanceof Command) {
            Command<?> newCommand = (Command<?>) result;
            if (openNewTab) {
                MainWindow mainWindow = tab.getMainWindow();
                CommandTab newTab = mainWindow.addTab(newCommand);
                mainWindow.tabbedPane.setSelectedComponent(newTab.getPanel());

            } else {
                tab.go(newCommand);
            }

        } else if (result instanceof Runnable) {
            Thread thread = new Thread((Runnable) result);
            thread.start();

        } else if (result == null) {
            // Do nothing

        } else {
            // TODO Do something with groovy output
            // System.out.println(result);
        }

    }
}