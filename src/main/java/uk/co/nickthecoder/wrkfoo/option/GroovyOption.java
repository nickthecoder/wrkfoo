package uk.co.nickthecoder.wrkfoo.option;

import java.util.List;

import groovy.lang.Binding;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.TableTool;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.util.OSHelper;

public class GroovyOption extends AbstractOption
{

    private final GroovyScriptlet ifScriptlet;

    private final GroovyScriptlet action;

    public GroovyOption(String code, String label, String script, String ifScript, boolean isRow, boolean isMulti,
        boolean newTab)
    {
        super(code, label, isRow, isMulti, newTab);
        this.action = new GroovyScriptlet(script);
        if (Util.empty(ifScript)) {
            ifScriptlet = null;
        } else {
            this.ifScriptlet = new GroovyScriptlet(ifScript);
        }
    }

    @Override
    public void runMultiOption(TableTool<?> tool, List<Object> rows, boolean openNewTab)
    {
        privateRunOption(tool, rows, openNewTab);
    }

    @Override
    public void runOption(Tool tool, boolean openNewTab)
    {
        privateRunOption(tool, null, openNewTab);
    }

    @Override
    public void runOption(TableTool<?> tool, Object row, boolean openNewTab)
    {
        privateRunOption(tool, row, openNewTab);
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

    private void privateRunOption(Tool tool, Object rowOrRows, boolean openNewTab)
    {
        ToolTab tab = tool.getToolTab();

        openNewTab |= this.getNewTab();

        // The new tab cannot share the same Tool as the current tab, so create a copy first
        // just in case the option reuses the tool.
        if (openNewTab) {
            tool = tool.duplicate();
        }

        Object result = runScript(action, tool, isMultiRow(), rowOrRows);

        if (result instanceof Tool) {
            Tool newTool = (Tool) result;
            if (openNewTab || newTool.getUseNewTab()) {
                MainWindow mainWindow = MainWindow.getMainWindow(tab.getPanel());
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
        bindings.setProperty("os", OSHelper.instance);
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
