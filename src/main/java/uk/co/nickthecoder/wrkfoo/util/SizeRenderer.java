package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class SizeRenderer extends JPanel implements TableCellRenderer, LayoutManager
{
    private static SizeRenderer instance;

    public static final DecimalFormat format = new DecimalFormat("#.#");

    public static SizeRenderer getInstance()
    {
        if (instance == null) {
            instance = new SizeRenderer();
        }
        return instance;
    }

    public static final String units[] = { "", " KB", " MB", " GB", " TB" };

    public static final Color colors[] = {
        Color.white,
        new Color(255, 255, 204),
        new Color(255, 255, 136),
        new Color(255, 178, 0),
        new Color(252, 100, 0),
        new Color(234, 11, 11)
    };
    public static final Color foregrounds[] = {
        Color.darkGray,
        Color.darkGray,
        Color.darkGray,
        Color.darkGray,
        Color.white
    };

    private JLabel label;

    private JPanel back;

    private JPanel progress;

    private DefaultTableCellRenderer dtcr;

    public SizeRenderer()
    {
        progress = new JPanel();

        dtcr = new DefaultTableCellRenderer();

        label = new JLabel();
        back = new JPanel();
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setOpaque(false);

        this.setLayout(this);

        add(label);
        add(progress);
        add(back);

        // Ignore the background color give to us by any "prepareRenderer" code, such as in
        // Columns.Table.prepareRenderer
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof Long) {
            float size = (Long) value;
            int index = 0;
            while (size > 1000) {
                size /= 1000;
                index++;
            }

            int width = (label.getWidth() * (int) size) / 1000;

            dtcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            progress.setBackground(colors[index + 1]);
            back.setBackground(colors[index]);

            progress.setBounds(0, 0, width, this.getHeight());
            label.setForeground(foregrounds[index]);

            label.setText(format.format(size) + units[index]);
        }

        label.setBorder(dtcr.getBorder());

        return this;
    }

    @Override
    public void addLayoutComponent(String name, Component comp)
    {
    }

    @Override
    public void removeLayoutComponent(Component comp)
    {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        return label.getPreferredSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent)
    {
        return label.getMinimumSize();
    }

    @Override
    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();
        int height = getHeight() - insets.left - insets.right;
        int width = getWidth() - insets.left + insets.right;

        label.setBounds(insets.left, insets.top, width, height);
        back.setBounds(insets.left, insets.top, width, height);
        progress.setBounds(insets.left, insets.top, progress.getWidth(), height);
    }
}
