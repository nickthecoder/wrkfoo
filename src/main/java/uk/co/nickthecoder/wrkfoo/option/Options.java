package uk.co.nickthecoder.wrkfoo.option;

import uk.co.nickthecoder.wrkfoo.Tool;

public interface Options
{
    public Option getOption(Tool<?> tool, String code, Object row);

    public Option getRowOption(Tool<?> tool, String code, Object row);

    public Option getNonRowOption(Tool<?> tool, String code);

    public Iterable<Option> allOptions();

    public Iterable<Option> applicableOptions( Tool<?> tool);

    public Iterable<Option> applicableOptions( Tool<?> tool, Object row);
}
