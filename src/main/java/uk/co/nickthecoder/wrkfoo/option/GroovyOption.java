package uk.co.nickthecoder.wrkfoo.option;

import groovy.lang.Binding;

import java.util.List;

import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.TableTool;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.ToolTab;

public class GroovyOption extends AbstractOption
{

    private final GroovyScriptlet ifScriptlet;

    private final GroovyScriptlet action;

    public GroovyOption(String code, String label, String script, String ifScript, boolean isRow, boolean isMulti)
    {
        super(code, label, isRow, isMulti);
        this.action = new GroovyScriptlet(script);
        if (ifScript != null) {
            this.ifScriptlet = new GroovyScriptlet(ifScript);
        } else {
            ifScriptlet = null;
        }
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
    public boolean isApplicable(Object row)
    {
        if (ifScriptlet == null) {
            return true;
        }

        Object result = runScript(ifScriptlet, null, false, row);

        return result == Boolean.TRUE;
    }

    private void privateRunOption(Tool tool, boolean isMulti, Object rowOrRows, boolean openNewTab)
    {
        ToolTab tab = tool.getToolTab();

        // The new tab cannot share the same Tool as the current tab, so create a copy first
        // just in case the option reuses the tool.
        if (openNewTab) {
            tool = tool.duplicate();
        }

        Object result = runScript(action, tool, isMulti, rowOrRows);

        if (result instanceof Tool) {
            Tool newTool = (Tool) result;
            if (openNewTab || newTool.getUseNewTab()) {
                MainWindow mainWindow = tab.getMainWindow();
                ToolTab newTab = mainWindow.insertTab(newTool);
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

    private Object runScript(GroovyScriptlet scriplet, Tool tool, boolean isMulti, Object rowOrRows)
    {
        Binding bindings = new Binding();
        bindings.setProperty("tool", tool);
        if (tool != null) {
            bindings.setProperty("task", tool.getTask());
        }

        if (isRow()) {
            if (isMulti) {
                bindings.setProperty("rows", rowOrRows);
            } else {
                bindings.setProperty("row", rowOrRows);
            }
        }

        return scriplet.run(bindings);
    }

}
