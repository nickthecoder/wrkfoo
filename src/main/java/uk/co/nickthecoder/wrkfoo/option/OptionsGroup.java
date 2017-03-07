package uk.co.nickthecoder.wrkfoo.option;

import java.util.ArrayList;
import java.util.Iterator;
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
    public Option getDefaultRowOption(Object row)
    {
        return getRowOption( "", row );
    }

    @Override
    public Option getOption(String code, Object row)
    {
        Option result = getRowOption(code, row);
        if ( result == null) {
            return getNonRowOption(code);
        } else {
            return result;
        }
    }
    
    @Override
    public Option getRowOption(String code, Object row)
    {
        for (Options options : optionsList) {
            Option option = options.getRowOption(code, row);
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
