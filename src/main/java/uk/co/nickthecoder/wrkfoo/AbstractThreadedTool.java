package uk.co.nickthecoder.wrkfoo;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Stoppable;

public abstract class AbstractThreadedTool<T extends Task> extends AbstractTool<T>
{
    public AbstractThreadedTool(T task)
    {
        super(task);
    }

    private GoThread goThread;

    @Override
    public void go()
    {
        if (goThread == null) {
            goThread = new GoThread();
            
            try {
                goThread.start();
            } catch (Exception e) {
                goThread = null;
            }
        }
    }

    @Override
    public void stop()
    {
        if (task instanceof Stoppable) {
            ((Stoppable) task).stop();
        }
    }

    private void end()
    {
        goThread = null;

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                getToolPanel().getSplitPane().showLeft();
                focusOnResults(7);
            }
        });
    }

    public class GoThread extends Thread
    {
        @Override
        public void run()
        {
            try {
                task.run();
            } finally {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateResults();
                        end();
                    }
                });
            }
        }
    }

}
