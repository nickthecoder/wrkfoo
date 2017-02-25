package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;

public class TaskHistory
{
    public List<Moment> history;

    public int currentIndex;

    public TaskHistory()
    {
        history = new ArrayList<Moment>();
        currentIndex = -1; // We don't have any Moment yet.
    }

    public void add(Task task)
    {
        while (currentIndex < history.size() - 1) {
            history.remove(history.size() - 1);
        }

        Moment moment = new Moment(task);
        history.add(moment);
        currentIndex ++;
    }

    public boolean canUndo()
    {
        return currentIndex > 0;
    }

    public Task undo()
    {
        if (currentIndex == 0) {
            throw new RuntimeException("Cannot undo");
        }

        currentIndex--;
        return history.get(currentIndex).restoreTask();
    }

    public boolean canRedo()
    {
        return currentIndex < history.size() - 1;
    }

    public Task redo()
    {
        if (currentIndex > history.size() - 1) {
            throw new RuntimeException("Cannot redo");
        }

        currentIndex++;
        return history.get(currentIndex).restoreTask();
    }

    /**
     * A moment in history, stores the state of a task, so that the task can be re-run
     */
    private final class Moment
    {
        Task task;
        Map<String, Object> parameterValues;

        Moment(Task task)
        {
            this.task = task;
            parameterValues = new HashMap<String, Object>();

            saveParameters(task.getParameters());
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
        public Task restoreTask()
        {
            for (String key : parameterValues.keySet()) {
                try {
                    Parameter parameter = task.getParameters().findParameter(key);
                    ((ValueParameter) parameter).setValue(parameterValues.get(key));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

    }
}
