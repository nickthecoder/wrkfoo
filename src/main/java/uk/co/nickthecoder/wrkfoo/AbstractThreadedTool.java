package uk.co.nickthecoder.wrkfoo;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.jguifier.util.Util;

public abstract class AbstractThreadedTool<S extends Results, T extends Task>
    extends AbstractTool<S, T>
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

    protected void ended()
    {
        Util.assertIsEDT();

        updateResults();
        Focuser.log("AbstractThreadedTool Showing left (results)");
        try {
            getToolPanel().getSplitPane().showLeft();
            Focuser.focusLater("AbstractThreadedTool task complete", getResultsPanel().getFocusComponent(), 9);
        } catch (Exception e) {
            // Ignore
        }
    }

    public class GoThread extends Thread
    {
        @Override
        public void run()
        {
            try {
                task.run();
            } catch (Exception e) {
                getToolPanel().getTopLevel().handleException(e);
            } finally {
                goThread = null;

                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ended();
                    }
                });
            }
        }
    }

}
