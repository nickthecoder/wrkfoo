package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.Icon;
import javax.swing.JSplitPane;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.guiutil.Places.Place;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.MergedToolPanel;
import uk.co.nickthecoder.wrkfoo.PanelResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.SimpleTable;
import uk.co.nickthecoder.wrkfoo.TableResults;
import uk.co.nickthecoder.wrkfoo.ToolPanel;
import uk.co.nickthecoder.wrkfoo.tool.PlacesChoices.PlacesChoicesResults;
import uk.co.nickthecoder.wrkfoo.util.FileNameRenderer;

/**
 * Lists a directory containing "places" files. The contents of one of the places files is listed in a
 * separate table.
 */
public class PlacesChoices extends AbstractListTool<PlacesChoicesResults, PlacesChoicesTask, WrappedFile>
{
    private PlacesTool placesTool;

    public PlacesChoices()
    {
        super(new PlacesChoicesTask());

        placesTool = new PlacesTool()
        {
            @Override
            protected ToolPanel createToolPanel()
            {
                return new MergedToolPanel(PlacesChoices.this.getToolPanel());
            }
        };
    }

    @Override
    public PlacesChoicesTask getTask()
    {
        return (PlacesChoicesTask) super.getTask();
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("places.png");
    }

    @Override
    public SimpleTable<WrappedFile> getTable()
    {
        return getResultsPanel().tableResults.getTable();
    }

    @Override
    public Columns<WrappedFile> createColumns()
    {
        Columns<WrappedFile> columns = new Columns<>();

        columns = new Columns<>();

        columns.add(new Column<WrappedFile>(File.class, "name")
        {
            @Override
            public File getValue(WrappedFile row)
            {
                return row.file;
            }
        }.sort().width(300).renderer(FileNameRenderer.instance));

        return columns;
    }

    @Override
    protected PlacesChoicesResults createResultsPanel()
    {
        return new PlacesChoicesResults();
    }

    public void showPlaces(File file)
    {
        placesTool.getTask().store.setValue(file);
        placesTool.go();
    }

    public Task createPlacesTask()
    {
        return new CreatePlacesTask();
    }

    public class CreatePlacesTask extends Task
    {
        public StringParameter name = new StringParameter.Builder("name")
            .parameter();

        public CreatePlacesTask()
        {
            addParameters(name);
        }

        @Override
        public void body() throws Exception
        {
            File file = new File(getTask().directory.getValue(), name.getValue());
            // Create an empty file
            PrintWriter writer = new PrintWriter(file);
            writer.close();
        }
    }

    public class PlacesChoicesResults extends PanelResults
    {
        TableResults<WrappedFile> tableResults;

        TableResults<Place> placesTableResults;

        public PlacesChoicesResults()
        {
            super(PlacesChoices.this);
            SimpleTable<WrappedFile> table = getColumns().createTable(getTableModel());

            tableResults = new TableResults<WrappedFile>(PlacesChoices.this, table);

            placesTableResults = placesTool.getResultsPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.3);

            getComponent().add(splitPane, BorderLayout.CENTER);

            splitPane.setTopComponent(tableResults.getComponent());
            splitPane.setBottomComponent(placesTableResults.getComponent());
        }
    }
}
