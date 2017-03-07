package uk.co.nickthecoder.wrkfoo.option;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import uk.co.nickthecoder.wrkfoo.util.OSCommand;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GroovyScriptlet
{
    private static GroovyShell createShell()
    {
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(
            "uk.co.nickthecoder.wrkfoo",
            "uk.co.nickthecoder.wrkfoo.tool",
            "uk.co.nickthecoder.wrkfoo.util",
            "uk.co.nickthecoder.jguifier.util");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "command");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "gui");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "edit");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "openFolder");

        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(configuration);
    }

    public String source;

    public Script script;

    public GroovyScriptlet(String source)
    {
        this.source = source;
    }

    public Object run(Binding binding)
    {
        if (script == null) {
            script = createShell().parse(source);
        }
        script.setBinding(binding);

        try {
            return script.run();
        } catch (Exception e) {
            throw new ScriptletException(this, e);
        }
    }
}
