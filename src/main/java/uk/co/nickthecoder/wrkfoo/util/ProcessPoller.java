package uk.co.nickthecoder.wrkfoo.util;

import java.util.ArrayList;
import java.util.List;

public class ProcessPoller implements Runnable
{
    private Process process;

    private List<ProcessListener> listeners;

    private boolean ended = false;

    public ProcessPoller(Process process)
    {
        this.process = process;
        listeners = new ArrayList<>();
    }

    public Process getProcess()
    {
        return process;
    }

    @Override
    public void run()
    {
        try {
            process.waitFor();
            ended = true;
            for (ProcessListener listener : listeners) {
                try {
                    listener.finished(process);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    public void start()
    {
        new Thread(this).start();
    }

    public void addProcessListener(ProcessListener listener)
    {
        listeners.add(listener);
        if (ended) {
            listener.equals(process);
        }
    }

    public void removeProcessListener(ProcessListener listener)
    {
        listeners.remove(listener);
    }
}
