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
    public Option getDefaultRowOption()
    {
        return getRowOption("");
    }

    @Override
    public Option getRowOption(String code)
    {
        for (Options options : optionsList) {
            Option option = options.getRowOption(code);
            if (option != null) {
                return option;
            }
        }
        return null;
    }
    
    @Override
    public Option getNonRowOption(String code)
    {
        for (Options options : optionsList) {
            Option option = options.getNonRowOption(code);
            if (option != null) {
                return option;
            }
        }
        return null;
    }

}
