package uk.co.nickthecoder.wrkfoo.option;


public interface Options extends Iterable<Option>
{
    public Option getDefaultRowOption();

    public Option getRowOption(String code);

    public Option getNonRowOption(String code);

}
