package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;
import uk.co.nickthecoder.jguifier.parameter.GroupParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.NullTool;

public class History
{
    public List<Moment> history;

    public int currentIndex;

    public History()
    {
        history = new ArrayList<>();
        currentIndex = -1; // We don't have any Moment yet.
    }

    /**
     * Is the current state of the tool, the same as the current Moment.
     * Used to determine if another Moment should be created by {@link #add}.
     * 
     * @param tool
     *            The tool to compare with the current Moment.
     * @return true if the Tool is the same class, and the parameters have all the same values.
     */
    public boolean sameAsCurrent(Tool tool)
    {
        if (currentIndex < 0) {
            return false;
        }

        Moment current = history.get(currentIndex);
        if (current.tool.getClass() != tool.getClass()) {
            return false;
        }

        for (String paramName : current.parameterValues.keySet()) {
            Parameter parameter = tool.getTask().findParameter(paramName);
            if (parameter instanceof ValueParameter) {
                ValueParameter<?> vp = (ValueParameter<?>) parameter;
                if (!Util.equals(vp.getValue(), current.parameterValues.get(paramName))) {
                    return false;
                }
            }
        }

        return true;
    }

    public void add(Tool tool)
    {
        if (sameAsCurrent(tool)) {
            return;
        }

        while (currentIndex < history.size() - 1) {
            history.remove(history.size() - 1);
        }

        Moment moment;
        if (tool.isRerunnable()) {
            moment = new Moment(tool);
        } else {
            moment = new Moment(new NullTool());
        }
        history.add(moment);
        currentIndex++;
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
            parameterValues = new HashMap<>();

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
