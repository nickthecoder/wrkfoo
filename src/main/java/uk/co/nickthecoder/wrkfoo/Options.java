package uk.co.nickthecoder.wrkfoo;

public interface Options
{
    public Option getDefaultRowOption();

    public Option getRowOption(String code);

    public Option getNonRowOption(String code);

}
