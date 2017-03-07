package uk.co.nickthecoder.wrkfoo.option;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.TableTool;
import uk.co.nickthecoder.wrkfoo.util.OSCommand;

public class GroovyOption extends AbstractOption
{
    private static GroovyShell createShell()
    {
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports(
            "uk.co.nickthecoder.wrkfoo",
            "uk.co.nickthecoder.wrkfoo.tool",
            "uk.co.nickthecoder.wrkfoo.util",
            "uk.co.nickthecoder.jguifier.util");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "tool");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "gui");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "edit");
        importCustomizer.addStaticImport(OSCommand.class.getName(), "openFolder");

        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(configuration);
    }

    private final String groovyIfSource;

    private final String groovySource;

    private Script groovyScript;

    private Script groovyIfScript;

    public GroovyOption(String code, String label, String script, String ifScript, boolean isRow, boolean isMulti)
    {
        super(code, label, isRow, isMulti);
        this.groovySource = script;
        this.groovyIfSource = ifScript;
    }
    
    @Override
    public void runMultiOption(TableTool<?> tool, List<Object> rows, boolean openNewTab)
    {
        privateRunOption(tool, true, rows, openNewTab);
    }

    @Override
    public void runOption(Tool tool, boolean openNewTab)
    {
        privateRunOption(tool, false, null, openNewTab);
    }

    @Override
    public void runOption(TableTool<?> tool, Object row, boolean openNewTab)
    {
        privateRunOption(tool, false, row, openNewTab);
    }

    @Override
    public boolean isApplicable( Object row )
    {
        if ( groovyIfSource == null) {
            return true;
        }
        
        if (groovyIfScript == null) {
            groovyIfScript = createShell().parse(groovyIfSource);
        }

        Object result = runScript(groovyIfScript, null, false, row);

        return result == Boolean.TRUE;
    }

    private void privateRunOption(Tool tool, boolean isMulti, Object rowOrRows, boolean openNewTab)
    {
        if (groovyScript == null) {
            groovyScript = createShell().parse(groovySource);
        }
        ToolTab tab = tool.getToolTab();
        
        // The new tab cannot share the same Tool as the current tab, so create a copy first
        // just in case the option reuses the tool.
        if (openNewTab) {
            tool = tool.duplicate();
        }
        
        Object result = runScript(groovyScript, tool, isMulti, rowOrRows);

        if (result instanceof Tool) {
            Tool newTool = (Tool) result;
            if (openNewTab) {
                MainWindow mainWindow = tab.getMainWindow();
                ToolTab newTab = mainWindow.addTab(newTool);
                mainWindow.tabbedPane.setSelectedComponent(newTab.getPanel());

            } else {
                tab.go(newTool);
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

    private Object runScript(Script script, Tool tool, boolean isMulti, Object rowOrRows)
    {

        Binding bindings = new Binding();
        bindings.setProperty("tool", tool);
        if ( tool != null ) {
            bindings.setProperty("task", tool.getTask());
        }

        if (isRow()) {
            if (isMulti) {
                bindings.setProperty("rows", rowOrRows);
            } else {
                bindings.setProperty("row", rowOrRows);
            }
        }
        script.setBinding(bindings);

        return script.run();
    }
}
