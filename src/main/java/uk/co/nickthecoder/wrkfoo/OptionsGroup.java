package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

public class OptionsGroup implements Options
{
    public List<Options> optionsList;

    public OptionsGroup()
    {
        optionsList = new ArrayList<Options>();
    }

    public void add(Options options)
    {
        if (options == null)
            return;

        optionsList.add(options);
    }

    @Override
    public void add(Option option)
    {
        optionsList.get(0).add(option);
    }

    @Override
    public Option getDefault()
    {
        return get("");
    }

    @Override
    public Option getUnsafe(String shortcut)
    {
        for (Options options : optionsList) {
            Option option = options.get(shortcut);
            if (option != null) {
                return option;
            }
        }
        return null;
    }

    public Option get(String code)
    {
        Option result = getUnsafe(code);
        if (result == null) {
            return NullOption.instance;
        }
        return result;
    }

    @Override
    public boolean contains(String code)
    {
        return get(code) != null;
    }

}
