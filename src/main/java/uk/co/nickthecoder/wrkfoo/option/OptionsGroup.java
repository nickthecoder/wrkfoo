package uk.co.nickthecoder.wrkfoo.option;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.wrkfoo.Tool;

public class OptionsGroup implements Options
{
    public List<Options> optionsList;

    public OptionsGroup()
    {
        optionsList = new ArrayList<>();
    }

    public void add(Options options)
    {
        if (options == null) {
            return;
        }

        optionsList.add(options);
    }

    @Override
    public Option getOption(Tool<?> tool, String code, Object row)
    {
        Option result = getRowOption(tool, code, row);
        if (result == null) {
            return getNonRowOption(tool, code);
        } else {
            return result;
        }
    }

    @Override
    public Option getRowOption(Tool<?> tool, String code, Object row)
    {
        for (Options options : optionsList) {
            Option option = options.getRowOption(tool, code, row);
            if (option != null) {
                return option;
            }
        }
        return null;
    }

    @Override
    public Option getNonRowOption(Tool<?> tool, String code)
    {
        for (Options options : optionsList) {
            Option option = options.getNonRowOption(tool, code);
            if (option != null) {
                return option;
            }
        }
        return null;
    }

    public void clear()
    {
        optionsList.clear();
    }

    @Override
    public Iterable<Option> allOptions()
    {
        List <Option> results = new ArrayList<>();
        
        for ( Options options : optionsList) {
            for ( Option option : options.allOptions()) {
                results.add(option);
            }
        }
        return results;
    }

    @Override
    public Iterable<Option> applicableOptions(Tool<?> tool)
    {
        List <Option> results = new ArrayList<>();
        
        for ( Options options : optionsList) {
            for ( Option option : options.applicableOptions(tool)) {
                results.add(option);
            }
        }
        return results;
    }

    @Override
    public Iterable<Option> applicableOptions(Tool<?> tool, Object row)
    {
        List <Option> results = new ArrayList<>();
        
        for ( Options options : optionsList) {
            for ( Option option : options.applicableOptions(tool, row)) {
                results.add(option);
            }
        }
        return results;
    }
}
