package uk.co.nickthecoder.wrkfoo;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import uk.co.nickthecoder.jguifier.util.Exec;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

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

    public GroovyOption(String code, String label, String script)
    {
        super(code, label);
        this.groovySource = script;
    }

    @Override
    public void runOption(Command<?> command, Object row)
    {
        if ( groovyScript == null ) {
            groovyScript = createShell().parse(groovySource);
        }
        
        Binding bindings = new Binding();

        bindings.setProperty("command", command);
        bindings.setProperty("task", command.getTask());
        bindings.setProperty("row", row);

        groovyScript.setBinding(bindings);
        CommandTab tab = command.getCommandTab();
        
        Object result = groovyScript.run();

        if (result instanceof Command) {
            Command<?> newCommand = (Command<?>) result;
            tab.go(newCommand);
            
        } else if (result instanceof Exec) {
            ((Exec) result).run();
            
        } else if (result instanceof Runnable) {
            ((Runnable) result).run();
            
        } else {
            // TODO Do something with groovy output
            System.out.println(result);
        }

    }
}
