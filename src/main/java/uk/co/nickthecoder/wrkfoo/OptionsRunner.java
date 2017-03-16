package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

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
    private Tool tool;

    private TableTool<?> tableTool;

    private SimpleTable<?> table;

    public OptionsRunner(Tool tool)
    {
        this.tool = tool;
    }

    public OptionsRunner(TableTool<?> tableTool)
    {
        this((Tool) tableTool);
        this.tableTool = tableTool;
    }
    
    private SimpleTable<?> getTable()
    {
        if (table == null) {
            table = tableTool.getToolPanel().table;
        }
        return table;
    }

    public FilteredPopupMenu createPopupMenu()
    {
        FilteredPopupMenu menu = FilteredPopupMenu.create(menuItemFilter);
        return menu;
    }

    private MainWindow getMainWindow()
    {
        return (MainWindow) SwingUtilities.getRoot(tool.getToolPanel());
    }

    public static final MenuItemFilter menuItemFilter = new MenuItemFilter()
    {
        @Override
        public boolean accept(JMenuItem menuItem, String filterText)
        {
            if (!menuItem.isEnabled()) {
                // Keep information items, such as "Non-Row Options"
                return true;
            }
            return menuItem.getText().toLowerCase().contains(filterText.toLowerCase());
        }
    };

    public void createNonRowOptionsMenu(MouseEvent me)
    {
        boolean useNewTab = me.isControlDown();

        FilteredPopupMenu menu = createPopupMenu();
        Options options = tool.getOptions();
        for (Option option : options) {
            if (!option.isRow()) {
                menu.add(createOptionsMenuItem(option, useNewTab));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    protected JMenuItem createOptionsMenuItem(final Option option, final boolean useNewTab)
    {
        String extra = Util.empty(option.getCode()) ? "" : " (" + option.getCode() + ")";
        JMenuItem item = new JMenuItem(option.getLabel() + extra);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (option != null) {
                    runOption(option, useNewTab);
                }
            }
        });

        return item;
    }

    public void createOptionsMenu(MouseEvent me)
    {
        int r = getTable().rowAtPoint(me.getPoint());
        if (r < 0) {
            createNonRowOptionsMenu(me);
            return;
        }

        if (getTable().getSelectedRowCount() > 1) {
            createMultiOptionsMenu(me);
            return;
        }

        int rowIndex = getTable().convertRowIndexToModel(r);
        getTable().getSelectionModel().clearSelection();
        getTable().getSelectionModel().addSelectionInterval(r, r);
        Object row = getTable().getModel().getRow(rowIndex);

        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = createPopupMenu();

        // Add row options first
        Options options = tableTool.getOptions();
        for (Option option : options) {
            if (option.isRow()) {
                if (option.isApplicable(row)) {
                    menu.add(createMenuItem(option, rowIndex, useNewTab));
                }
            }
        }

        // Add non-row options next
        boolean first = true;
        for (Option option : options) {
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

    public void createMultiOptionsMenu(MouseEvent me)
    {
        JPopupMenu menu = createPopupMenu();

        Options options = tableTool.getOptions();
        for (Option option : options) {
            if (option.isMultiRow()) {
                menu.add(createMultiMenuItem(option));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    public JMenuItem createMenuItem(Option option)
    {
        String extra = Util.empty(option.getCode()) ? "" : " (" + option.getCode() + ")";
        String text = option.getLabel() + extra;
        return new JMenuItem(text);
    }

    public JMenuItem createMenuItem(final Option option, final int rowIndex, final boolean useNewTab)
    {
        JMenuItem item = createMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Object row = rowIndex >= 0 ? getTable().getModel().getRow(rowIndex) : null;
                if (option != null) {
                    runOption(option, row, useNewTab);
                }
            }
        });

        return item;
    }

    public JMenuItem createMultiMenuItem(final Option option)
    {
        JMenuItem item = createMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ToolTableModel<?> model = getTable().getModel();
                List<Object> rows = new ArrayList<>();

                for (int r : getTable().getSelectedRows()) {
                    // TODO check if I need to convert from view to model
                    Object row = model.getRow(r);

                    if (option.isApplicable(row)) {
                        rows.add(row);
                        getTable().removeRowSelectionInterval(r, r);
                    }
                }
                runMultipleOption(option, rows, false);
            }
        });

        return item;
    }

    public boolean runOption(String code, boolean newTab)
    {
        Option option = tool.getOptions().getNonRowOption(code);
        if (option != null) {
            return runOption(option, tool, newTab);
        }
        return false;
    }

    public boolean runOption(Option option, boolean newTab)
    {
        try {
            option.runOption(tool, newTab);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    public boolean runOption(Option option, Object row, boolean newTab)
    {
        try {
            option.runOption(tableTool, row, newTab);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    public boolean runMultipleOption(Option option, List<Object> rows, boolean newTab)
    {
        try {
            option.runMultiOption(tableTool, rows, newTab);
            return true;
        } catch (Throwable e) {
            handleException(e);
            return false;
        }
    }

    private void handleException(Throwable e)
    {
        getMainWindow().handleException(e);
    }
}
