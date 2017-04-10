package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.ListItem;
import uk.co.nickthecoder.jguifier.parameter.ListParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.Util;

public abstract class AbstractTableTool<S extends Results, T extends Task, R>
    extends AbstractThreadedTool<S, T>
    implements TableTool<S, R>
{
    protected DragListConverter<R, ?> dragListConverter;

    public AbstractTableTool(T task)
    {
        super(task);
    }

    protected Columns<R> columns;

    @Override
    public Columns<R> getColumns()
    {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

    public ListParameter<ColumnListItem> createColumnsParameter()
    {
        ListParameter<ColumnListItem> result = new ListParameter.Builder<ColumnListItem>("columns").parameter();

        for (Column<?> column : getColumns()) {
            if (!Util.empty(column.key)) {
                ColumnListItem cli = new ColumnListItem(column);
                result.addPossibleValue(cli);
                if (column.visible) {
                    result.add(cli);
                }
            }
        }

        result.addListener(new ParameterListener()
        {
            @Override
            public void changed(Object initiator, Parameter source)
            {
                Columns<R> columns = getColumns();

                List<Column<?>> columnList = new ArrayList<>();
                columnList.add(getColumns().getColumn(0)); // Add the options column.
                for (ColumnListItem item : result.getValue()) {
                    columnList.add(columns.find(item.key));
                }
                getColumns().initialiseColumns(getTable(), columnList);
            }
        });

        return result;
    }

    protected abstract Columns<R> createColumns();

    class ColumnListItem implements ListItem<String>
    {
        private String key;

        public ColumnListItem(Column<?> column)
        {
            key = column.key;
        }

        @Override
        public String getValue()
        {
            return key;
        }

        @Override
        public String getStringValue()
        {
            return key;
        }

        @Override
        public String parse(String stringValue)
        {
            return stringValue;
        }

        @Override
        public String toString()
        {
            Column<?> column = getColumns().find(key);
            if (column == null) {
                return "<<UNKNOWN COLUMN>>";
            }
            return Util.empty(column.label) ? column.key : column.label;
        }
    }
}
