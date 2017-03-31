package uk.co.nickthecoder.wrkfoo.option;

import java.util.ArrayList;
import java.util.Iterator;
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
    public Iterator<Option> iterator()
    {
        return new OptionsGroupIterator();
    }

    class OptionsGroupIterator implements Iterator<Option>
    {
        private Option next;

        private Iterator<Options> optionsIterator;

        private Iterator<Option> singleIterator;

        public OptionsGroupIterator()
        {
            optionsIterator = optionsList.iterator();
            if (optionsIterator.hasNext()) {
                singleIterator = optionsIterator.next().iterator();
                lookAhead();
            } else {
                next = null;
            }
        }

        private void lookAhead()
        {
            while (!singleIterator.hasNext()) {
                if (optionsIterator.hasNext()) {
                    singleIterator = optionsIterator.next().iterator();
                } else {
                    next = null;
                    return;
                }
            }
            next = singleIterator.next();
        }

        @Override
        public boolean hasNext()
        {
            return next != null;
        }

        @Override
        public Option next()
        {
            Option result = next;
            lookAhead();
            return result;
        }

        @Override
        public void remove()
        {
            throw new RuntimeException("remove not supported");
        }
    }
}
