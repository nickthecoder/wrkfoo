package uk.co.nickthecoder.wrkfoo.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.TableModel;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TableTool;

public class ExportTableData extends Task
{
    private TableTool<?,?> tool;

    public FileParameter saveAs = new FileParameter.Builder("saveAs")
        .writable().mayExist().file()
        .value(Resources.getInstance().getHomeDirectory())
        .description("Save location - Should end in .csv .html or .txt")
        .parameter();

    public BooleanParameter includeHeadings = new BooleanParameter.Builder("includeHeadings")
        .value(true)
        .parameter();

    public BooleanParameter includeParameters = new BooleanParameter.Builder("includeParameters")
        .value(false)
        .parameter();

    public ExportTableData(TableTool<?,?> tool)
    {
        this.tool = tool;
        addParameters(saveAs, includeHeadings, includeParameters);
    }

    @Override
    public void body()
    {
        TableModel model = tool.getTableModel();
        Columns<?> columns = tool.getColumns();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(saveAs.getValue());

            TableFormat format = createTableFormat(writer);

            int rowCount = model.getRowCount();
            int columnCount = model.getColumnCount();

            if (includeParameters.getValue()) {
                String paramString = tool.getTask().getCommandString(false);
                format.begin(paramString);
            } else {
                format.begin();
            }

            if (includeHeadings.getValue()) {
                format.beingRow(true);
                for (int c = 1; c < columnCount; c++) {
                    if (columns.getColumn(c).save) {
                        format.cell(true, model.getColumnName(c));
                    }
                }
                format.endRow(true);
            }

            for (int r = 0; r < rowCount; r++) {
                format.beingRow(false);
                for (int c = 1; c < columnCount; c++) {
                    if (columns.getColumn(c).save) {
                        format.cell(false, model.getValueAt(r, c));
                    }
                }
                format.endRow(false);
            }

            format.end();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected TableFormat createTableFormat(PrintWriter out)
    {
        String filename = saveAs.getValue().getName();
        if (filename.endsWith(".txt")) {
            return new PlainTextFormat(out);
        } else if (filename.endsWith(".html")) {
            return new HTMLTextFormat(out);
        } else {
            return new CVSFormat(out);
        }
    }

    public interface TableFormat
    {
        public void begin();

        public void begin(String title);

        public void beingRow(boolean isHeading);

        public void cell(boolean isHeading, Object cell);

        public void endRow(boolean isHeading);

        public void end();
    }

    public static abstract class AbstractTableFormat implements TableFormat
    {
        protected PrintWriter out;

        public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        public String formatObject(Object obj)
        {
            if (obj instanceof Date) {
                return dateFormat.format((Date) obj);
            }
            return obj.toString();
        }

        public AbstractTableFormat(PrintWriter out)
        {
            this.out = out;
        }
    }

    public static class PlainTextFormat extends AbstractTableFormat
    {

        public PlainTextFormat(PrintWriter out)
        {
            super(out);
        }

        boolean isFirstColumn;

        @Override
        public void begin()
        {
        }

        @Override
        public void begin(String title)
        {
            out.println(title);
        }

        @Override
        public void beingRow(boolean isHeading)
        {
            isFirstColumn = true;
        }

        @Override
        public void cell(boolean isHeading, Object cell)
        {
            if (!isFirstColumn) {
                out.print("\t");
            }
            if (cell != null) {
                out.print(formatObject(cell));
            }
            isFirstColumn = false;
        }

        @Override
        public void endRow(boolean isHeading)
        {
            out.println();
        }

        @Override
        public void end()
        {
        }

    }

    public static class CVSFormat extends PlainTextFormat
    {
        public CVSFormat(PrintWriter out)
        {
            super(out);
        }

        @Override
        public void begin(String title)
        {
            out.print("#");
            super.begin(title);
        }

        @Override
        public void cell(boolean isHeading, Object cell)
        {
            if (!isFirstColumn) {
                out.print(",");
            }
            if (cell != null) {
                out.print(Util.csvQuote(formatObject(cell)));
            }
            isFirstColumn = false;
        }
    }

    public static class HTMLTextFormat extends AbstractTableFormat
    {

        public HTMLTextFormat(PrintWriter out)
        {
            super(out);
        }

        @Override
        public void begin()
        {
            out.println("<html>");
            out.println("<head></head>");
            out.println("<body>");
            out.println("  <table>");
        }

        @Override
        public void begin(String title)
        {
            out.println("<html>");
            out.println("<head>");
            out.print("  <title>");
            out.print(escapeHTML(title));
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");
            out.print("<h1>");
            out.print(escapeHTML(title));
            out.println("</h1>");
            out.println("  <table>");
        }

        @Override
        public void beingRow(boolean isHeading)
        {
            out.println("    <tr>");
            out.print("      ");
        }

        @Override
        public void cell(boolean isHeading, Object cell)
        {
            String tag = isHeading ? "th" : "td";

            out.print("<");
            out.print(tag);
            out.print(">");
            if (cell != null) {
                out.print(escapeHTML(formatObject(cell)));
            }
            out.print("</");
            out.print(tag);
            out.print(">");
        }

        @Override
        public void endRow(boolean isHeading)
        {
            out.println();
            out.println("    </tr>");
        }

        @Override
        public void end()
        {
            out.println("  </table>");
            out.println("</body>");
            out.println("</html>");
        }

    }

    public static String escapeHTML(String s)
    {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127) {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c == '"') {
                out.append("&quot;");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
