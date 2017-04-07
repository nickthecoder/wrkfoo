package uk.co.nickthecoder.wrkfoo.util;

public abstract class AutoComponentUpdater implements ComponentUpdater
{
    public AutoComponentUpdater()
    {
        ComponentUpdateManager.getInstance().add(this);
    }

    @Override
    public boolean update()
    {
        try {
            autoUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract void autoUpdate();
}
