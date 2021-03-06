package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.guiutil.FilteredPopupMenu;
import uk.co.nickthecoder.jguifier.guiutil.MenuItemFilter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.Options;

/**
 * A central place for running {@link Tool}'s {@link Option}.
 * This includes creating {@link JPopupMenu}s for row and non-row option fields.
 */
public class OptionsRunner
{
    public static final String DEFAULT_CODE = ".";

    public static final MenuItemFilter menuItemFilter = new MenuItemFilter()
    {
        @Override
        public boolean accept(JMenuItem menuItem, String filterText)
        {
            if (menuItem instanceof OptionMenuItem) {
                Option option = ((OptionMenuItem) menuItem).option;
                filterText = filterText.toLowerCase();

                return option.getLabel().toLowerCase().contains(filterText) ||
                    option.getCode().toLowerCase().contains(filterText);
            }
            return true;
        }
    };

    private Tool<?> tool;

    private TableTool<?,?> tableTool;

    /**
     * The table for the {@link TableTool} passed into the constructor.
     * Do NOT access this directly, because it is lazily evaluated. It couldn't be set in the constructor, because the
     * table may not have been created at that point.
     */
    private SimpleTable<?> table;

    /**
     * Create an OptionsRunner for a non-table based tool.
     * Attempting to use row related methods, will result in null pointer exceptions.
     * 
     * @param tool
     */
    public OptionsRunner(Tool<?> tool)
    {
        this.tool = tool;
    }

    /**
     * Create an OptionsRunner for a table based tool.
     * 
     * @param tableTool
     */
    public OptionsRunner(TableTool<?,?> tableTool)
    {
        this.tool = tableTool;
        this.tableTool = tableTool;
    }

    /**
     * Lazily get the table associated with the {@link TableTool} passed into the constructor
     * {@link OptionsRunner#OptionsRunner(TableTool)}. The table couldn't be determined then, because the
     * {@link TableToolPanel} hadn't been created.
     * 
     * @return
     */
    private SimpleTable<?> getTable()
    {
        if (table == null) {
            table = tableTool.getTable();
        }
        return table;
    }

    private MainWindow getMainWindow()
    {
        return (MainWindow) SwingUtilities.getRoot(tool.getToolPanel().getComponent());
    }

    /**
     * Creates an empty JPopupMenu. This implementation creates a FilteredPopupMenu, which allows the contents of the
     * menu to be filtered by typing the text. Only those menu items containing that text will be shown.
     * 
     * @return An empty {@link FilteredPopupMenu}
     */
    private JPopupMenu createPopupMenu()
    {
        FilteredPopupMenu menu = FilteredPopupMenu.create(menuItemFilter);
        return menu;
    }

    /**
     * Pops up a menu of non-row options.
     * 
     * @param me
     */
    public void popupNonRowMenu(MouseEvent me)
    {
        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = createPopupMenu();
        Options options = tool.getOptions();
        for (Option option : options.applicableOptions(tool)) {
            if (!option.isRow()) {
                menu.add(createMenuItem(option, useNewTab));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    /**
     * Pops up a menu for a table. If the table has multiple rows selected, then only multi-row options are displayed.
     * Otherwise, non-row, row, and multi-row options are displayed.
     * 
     * @param me
     */
    public void popupRowMenu(MouseEvent me)
    {
        int r = getTable().rowAtPoint(me.getPoint());
        if (r < 0) {
            popupNonRowMenu(me);
            return;
        }

        int rowIndex = getTable().convertRowIndexToModel(r);
        getTable().getSelectionModel().clearSelection();
        getTable().getSelectionModel().addSelectionInterval(r, r);
        Object row = getTable().getModel().getRow(rowIndex);

        if (getTable().getSelectedRowCount() > 1) {
            popupMultiOptionsMenu(me);
            return;
        }

        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = createPopupMenu();

        // Add row options first
        Options options = tableTool.getOptions();
        for (Option option : options.applicableOptions(tableTool, row)) {
            if (option.isRow()) {
                if (option.isApplicable(tableTool,row)) {
                    menu.add(createMenuItem(option, rowIndex, useNewTab));
                }
            }
        }

        // Add non-row options next
        boolean first = true;
        for (Option option : options.applicableOptions(tableTool)) {
            if (!option.isRow()) {
                if (first) {
                    menu.addSeparator();
                    JMenuItem instruction = new JMenuItem("Non-Row Options");
                    instruction.setEnabled(false);
                    menu.add(instruction);
                    first = false;
                }
                menu.add(createMenuItem(option, rowIndex, useNewTab));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    private void popupMultiOptionsMenu(MouseEvent me)
    {
        JPopupMenu menu = createPopupMenu();

        Options options = tableTool.getOptions();
        for (Option option : options.applicableOptions(tableTool)) {
            if (option.isMultiRow()) {
                menu.add(createMultiMenuItem(option, false));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    /**
     * Creates a plain JMenuItem, without attaching an action to it.
     * 
     * @param option
     * @return A new JMenuItem, which will do nothing when activated.
     */
    private OptionMenuItem createPlainMenuItem(Option option)
    {
        return new OptionMenuItem(option);
    }

    /**
     * Creates a non-row menu item, which will run the option when clicked.
     * 
     * @param option
     * @param useNewTab
     * @return A new JMenuItem
     */
    private OptionMenuItem createMenuItem(final Option option, final boolean useNewTab)
    {
        OptionMenuItem item = createPlainMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (option != null) {
                    runOption(option, useNewTab, false);
                }
            }
        });

        return item;
    }

    /**
     * Creates a row menu item, which will run the options when clicked.
     * 
     * @param option
     * @param rowIndex
     * @param useNewTab
     * @return A new JMenuItem
     */
    private OptionMenuItem createMenuItem(final Option option, final int rowIndex, final boolean useNewTab)
    {
        OptionMenuItem item = createPlainMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Object row = rowIndex >= 0 ? getTable().getModel().getRow(rowIndex) : null;
                if (option != null) {
                    runOption(option, row, useNewTab, false);
                }
            }
        });

        return item;
    }

    /**
     * Create a menu item for a multi-row option.
     * When clicked, it will run the option against all selected rows in the table.
     * 
     * @param option
     * @param newTab
     * @return
     */
    private OptionMenuItem createMultiMenuItem(final Option option, final boolean newTab)
    {
        OptionMenuItem item = createPlainMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ToolTableModel<?> model = getTable().getModel();
                List<Object> rows = new ArrayList<>();

                for (int r : getTable().getSelectedRows()) {
                    Object row = model.getRow(r);

                    if (option.isApplicable(tableTool, row)) {
                        rows.add(row);
                        getTable().removeRowSelectionInterval(r, r);
                    }
                }
                runMultipleOption(option, rows, newTab, false);
            }
        });

        return item;
    }

    /**
     * Processes the codes that have been typed in the option column of a table
     * 
     * @param newTab
     */
    public void processTableOptions(boolean newTab, boolean prompt)
    {
        ToolTableModel<?> model = getTable().getModel();
        getTable().stopEditing();

        // Apply the options on all rows.
        boolean foundOne = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            String code = model.getCode(i);
            if (!Util.empty(code)) {
                foundOne = true;

                Object row = model.getRow(i);
                Option option = tableTool.getOptions().getOption(tool, code, row);
                
                if (option == null) {
                    message("Unknown Option Code : '" + code + "'");
                } else {
                    if (option.isMultiRow()) {
                        processMultiRowOptions(tableTool, option, newTab, prompt);
                    } else {
                        model.setCode(i, "");
                        if (!runOption(option, row, newTab, prompt)) {
                            model.setCode(i, code); // Put back the code, to indicate this code did not run.
                            break;
                        }

                    }
                }
            }
        }

        // Run the default option on the current row if no options have been entered.
        if (!foundOne) {
            int r = getTable().getSelectedRow();

            if (r >= 0) {
                int rowIndex = getTable().convertRowIndexToModel(r);
                if (Util.empty(model.getCode(rowIndex))) {
                    Object row = getTable().getModel().getRow(rowIndex);
                    Option option = tableTool.getOptions().getOption(tool, DEFAULT_CODE, row);
                    if (option == null) {
                        message("No default Option");
                    } else {
                        runOption(option, row, newTab, prompt);
                    }
                }
            }
        }
    }

    private void processMultiRowOptions(TableTool<?,?> tableTool, Option option, boolean newTab, boolean prompt)
    {
        ToolTableModel<?> model = getTable().getModel();

        List<Object> rows = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object row = model.getRow(i);
            Option otherOption = tableTool.getOptions().getRowOption(tool, model.getCode(i), row);
            if (otherOption == option) {
                if (option.isApplicable(tableTool, row)) {
                    model.setCode(i, "");
                    rows.add(model.getRow(i));
                }
            }
        }
        runMultipleOption(option, rows, newTab, prompt);
    }

    /**
     * Runs a non-row option
     * 
     * @param code
     *            The {@link Option}'s code.
     * @param newTab
     * @return true iff the option run ok. Note, this does NOT wait for {@link Task}s, or other {@link Runnable}s
     *         to finish.
     */
    public boolean runOption(String code, boolean newTab, boolean prompt)
    {
        Option option = tool.getOptions().getNonRowOption(tool, code);
        if (option != null) {
            return runOption(option, newTab, prompt);
        } else {
            message("Unknown Option Code : '" + code + "'");
            return false;

        }
    }

    /**
     * Runs a non-row option
     * 
     * @param option
     *            The option to run
     * @param newTab
     * @return true iff the option run ok. Note, this does NOT wait for {@link Task}s, or other {@link Runnable}s
     *         to finish.
     */
    public boolean runOption(Option option, boolean newTab, boolean prompt)
    {
        try {
            option.runOption(tool, newTab, prompt);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    /**
     * Runs an option against a single row of a table.
     * 
     * @param option
     *            The option to run
     * @param row
     *            The row of the table
     * @param newTab
     * @return true iff the option run ok. Note, this does NOT wait for {@link Task}s, or other {@link Runnable}s
     *         to finish.
     */
    public boolean runOption(Option option, Object row, boolean newTab, boolean prompt)
    {
        try {
            option.runOption(tableTool, row, newTab, prompt);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    /**
     * Runs a multi-row options against a list of table rows.
     * 
     * @param option
     *            The option to run
     * @param rows
     *            The table rows to run the option against
     * @param newTab
     * @return true iff the option run ok. Note, this does NOT wait for {@link Task}s, or other {@link Runnable}s
     *         to finish.
     */
    public boolean runMultipleOption(Option option, List<Object> rows, boolean newTab, boolean prompt)
    {
        try {
            option.runMultiOption(tableTool, rows, newTab, prompt);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    private void message(String message)
    {
        getMainWindow().setMessage(message);
    }

    /**
     * Passes the exception to {@link MainWindow} for it to handle.
     * 
     * @param e
     *            The exception to report to the user
     */
    private void handleException(Throwable e)
    {
        if ( getMainWindow() == null ) {
            e.printStackTrace();
            return;
        }
        getMainWindow().handleException(e);
    }
}
