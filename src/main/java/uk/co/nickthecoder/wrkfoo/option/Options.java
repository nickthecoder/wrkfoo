package uk.co.nickthecoder.wrkfoo.option;

public interface Options extends Iterable<Option>
{
    public Option getOption(String code, Object row);

    public Option getRowOption(String code, Object row);

    public Option getNonRowOption(String code);

}
