package uk.co.nickthecoder.wrkfoo.util;

public abstract class AutoComponentUpdater implements ComponentUpdater
{
    private String description;

    public AutoComponentUpdater(String description)
    {
        this.description = description;
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

    public String toString()
    {
        return description;
    }
}
