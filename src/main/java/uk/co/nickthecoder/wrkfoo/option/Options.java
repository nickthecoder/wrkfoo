package uk.co.nickthecoder.wrkfoo.option;

import uk.co.nickthecoder.wrkfoo.Tool;

public interface Options extends Iterable<Option>
{
    public Option getOption(Tool<?> tool, String code, Object row);

    public Option getRowOption(Tool<?> tool, String code, Object row);

    public Option getNonRowOption(Tool<?> tool, String code);

}
