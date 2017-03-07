package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.tool.ExecTool;

public class History
{
    public List<Moment> history;

    public int currentIndex;

    public History()
    {
        history = new ArrayList<Moment>();
        currentIndex = -1; // We don't have any Moment yet.
    }

    public void add(Tool tool)
    {
        while (currentIndex < history.size() - 1) {
            history.remove(history.size() - 1);
        }

        Moment moment;
        if (tool.isRerunnable()) {
            moment = new Moment(tool);
        } else {
            moment = new Moment(new ExecTool(new Exec("echo", "Tool prevented from rerunning")));
        }
        history.add(moment);
        currentIndex ++;
    }

    public boolean canUndo()
    {
        return currentIndex > 0;
    }

    public Tool undo()
    {
        if (currentIndex == 0) {
            throw new RuntimeException("Cannot undo");
        }

        currentIndex--;
        return history.get(currentIndex).restore();
    }

    public boolean canRedo()
    {
        return currentIndex < history.size() - 1;
    }

    public Tool redo()
    {
        if (currentIndex > history.size() - 1) {
            throw new RuntimeException("Cannot redo");
        }

        currentIndex++;
        return history.get(currentIndex).restore();
    }

    /**
     * A moment in history, stores the state of a task, so that the task can be re-run
     */
    private final class Moment
    {
        Tool tool;
        Map<String, Object> parameterValues;

        Moment(Tool tool)
        {
            this.tool = tool;
            parameterValues = new HashMap<String, Object>();

            saveParameters(tool.getTask().getParameters());
        }

        void saveParameters(GroupParameter group)
        {
            for (Parameter parameter : group.getChildren()) {
                if (parameter instanceof ValueParameter) {
                    parameterValues.put(parameter.getName(), ((ValueParameter<?>) parameter).getValue());
                }
            }

        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Tool restore()
        {
            Task task = tool.getTask();
            
            for (String key : parameterValues.keySet()) {
                try {
                    Parameter parameter = task.getParameters().findParameter(key);
                    ((ValueParameter) parameter).setDefaultValue(parameterValues.get(key));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return tool;
        }

    }
}
