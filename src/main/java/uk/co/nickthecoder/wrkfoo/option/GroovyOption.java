package uk.co.nickthecoder.wrkfoo.option;

import java.util.List;

import javax.swing.SwingUtilities;

import groovy.lang.Binding;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskAdaptor;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.TableTool;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.Tab;
import uk.co.nickthecoder.wrkfoo.TopLevel;
import uk.co.nickthecoder.wrkfoo.tool.Terminal;
import uk.co.nickthecoder.wrkfoo.util.OSHelper;

public class GroovyOption extends AbstractOption
{
    private final GroovyScriptlet ifScriptlet;

    private final GroovyScriptlet action;

    public GroovyOption(String code, String label, String script, String ifScript, boolean isRow, boolean isMulti,
        boolean newTab, boolean refreshResults, boolean prompt)
    {
        super(code, label, isRow, isMulti, newTab, refreshResults, prompt);

        this.action = new GroovyScriptlet(script);
        if (Util.empty(ifScript)) {
            ifScriptlet = null;
        } else {
            this.ifScriptlet = new GroovyScriptlet(ifScript);
        }
    }

    @Override
    public void runMultiOption(TableTool<?,?> tool, List<Object> rows, boolean openNewTab, boolean prompt)
    {
        privateRunOption(tool, rows, openNewTab, prompt);
    }

    @Override
    public void runOption(Tool<?> tool, boolean openNewTab, boolean prompt)
    {
        privateRunOption(tool, null, openNewTab, prompt);
    }

    @Override
    public void runOption(TableTool<?,?> tool, Object row, boolean openNewTab, boolean prompt)
    {
        privateRunOption(tool, row, openNewTab, prompt);
    }

    @Override
    public boolean isApplicable(Tool<?> tool, Object row)
    {
        if (ifScriptlet == null) {
            return true;
        }

        try {
            Object result = runScript(ifScriptlet, tool, false, row);
            return result == Boolean.TRUE;
        } catch (Exception e) {
            return false;
        }

    }

    private void privateRunOption(Tool<?> currentTool, Object rowOrRows, boolean openNewTab, boolean prompt)
    {
        Tab tab = currentTool.getTab();

        openNewTab |= this.getNewTab();
        prompt |= this.getPrompt();

        Object result = runScript(action, currentTool, isMultiRow(), rowOrRows);

        if ( result instanceof Command ) {
            result = new Terminal( (Command) result );
        }
        
        if (result instanceof Tool) {
            Tool<?> newTool = (Tool<?>) result;

            if (openNewTab) {

                if (getRefreshResults()) {
                    listen(currentTool, newTool.getTask());
                }

                TopLevel topLevel = currentTool.getToolPanel().getTopLevel();
                Tab newTab = topLevel.insertTab(newTool, prompt);
                newTab.select();

            } else {
                tab.goPrompt(newTool, prompt);
            }

        } else if (result instanceof Task) {

            Task task = (Task) result;
            if (getRefreshResults()) {
                listen(currentTool, task);
            }
            // Either prompt the Task, or run it straight away
            if (prompt) {
                task.promptTask();
            } else {
                Thread thread = new Thread(task);
                thread.start();
            }            
            
        } else if (result instanceof Runnable) {

            if (getRefreshResults()) {
                listen(currentTool, (Runnable) result);
            } else {
                Thread thread = new Thread((Runnable) result);
                thread.start();
            }

        }

    }

    /**
     * Run the Runnable, and then refresh the current tool.
     * We do this by wrapping making another runnable, which run the first Runnable, and then
     * refreshes the current tool.
     * 
     * @param currentTool
     *            The tool that will be refreshed.
     * @param runnable
     *            The action to be performed.
     */
    private void listen(final Tool<?> currentTool, final Runnable runnable)
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                runnable.run();
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        currentTool.go();
                    }
                });
            }
        };
        thread.start();
    }

    private void listen(final Tool<?> currentTool, Task task)
    {
        task.addTaskListener(new TaskAdaptor()
        {
            @Override
            public void ended(Task task, boolean normally)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        currentTool.go();
                    }
                });
            }
        });
    }

    private Object runScript(GroovyScriptlet scriplet, Tool<?> tool, boolean isMulti, Object rowOrRows)
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

    public String toString()
    {
        return getCode() + " : " + this.label + " -> " + this.action.source;
    }
}
