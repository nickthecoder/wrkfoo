package uk.co.nickthecoder.wrkfoo.option;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.CommandTab;
import uk.co.nickthecoder.wrkfoo.MainWindow;

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

        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(importCustomizer);


        return new GroovyShell( configuration);
    }
    
    private String groovySource;
    
    private Script groovyScript;

    public GroovyOption(String code, String label, String script, boolean isRow)
    {
        super(code, label, isRow);
        this.groovySource = script;
    }

    @Override
    public void runOption(Command<?> command, Object row, boolean openNewTab)
    {
        if ( groovyScript == null ) {
            groovyScript = createShell().parse(groovySource);
        }

        CommandTab tab = command.getCommandTab();

        // The new tab cannot share the same Command as the current tab, so create a copy first
        // just in case the option reuses the command.
        if ( openNewTab ) {
            command = command.clone();
        }
        
        Binding bindings = new Binding();
        bindings.setProperty("command", command);
        bindings.setProperty("task", command.getTask());
        bindings.setProperty("row", row);

        groovyScript.setBinding(bindings);
        
        Object result = groovyScript.run();

        if (result instanceof Command) {
            Command<?> newCommand = (Command<?>) result;
            if (openNewTab) {
                MainWindow mainWindow = tab.getMainWindow();
                CommandTab newTab = mainWindow.addTab( newCommand );
                mainWindow.tabbedPane.setSelectedComponent(newTab.getPanel());

            } else {
                tab.go(newCommand);
            }
            
        } else if (result instanceof Runnable) {
            Thread thread = new Thread( (Runnable) result );
            thread.start();
            
        } else if (result == null) {
            // Do nothing
            
        } else {
            // TODO Do something with groovy output
            //System.out.println(result);
        }

    }
}
