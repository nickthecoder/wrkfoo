package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class SizeRenderer extends JPanel implements TableCellRenderer, LayoutManager
{
    private static SizeRenderer instance;

    public static SizeRenderer getInstance()
    {
        if (instance == null) {
            instance = new SizeRenderer();
        }
        return instance;
    }

    public static final String units[] = { "", " KB", " MB", " GB", " TB" };

    public static final Color colors[] = {
        new Color(255, 255, 204),
        new Color(255, 255, 136),
        new Color(255, 178, 0),
        new Color(252, 100, 0),
        new Color(234, 11, 11)
    };

    private JLabel label;

    private JPanel progress;

    private DefaultTableCellRenderer dtcr;

    public SizeRenderer()
    {
        progress = new JPanel();

        dtcr = new DefaultTableCellRenderer();

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setOpaque(false);
        
        this.setLayout(this);

        add(label);
        add(progress);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof Long) {
            long size = (Long) value;
            int index = 0;
            while (size > 1000) {
                size /= 1000;
                index++;
            }

            int width = ((label.getWidth() - 10) * (int) size) / 1000 + (index > 0 ? 10 : 0);

            progress.setBackground(colors[index]);
            progress.setBounds(0, 0, width, this.getHeight());
            label.setText(size + units[index]);
        }

        dtcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setBackground(dtcr.getBackground());
        
        label.setForeground(dtcr.getForeground());
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
        progress.setBounds(insets.left, insets.top, progress.getWidth(), height);
    }
}
