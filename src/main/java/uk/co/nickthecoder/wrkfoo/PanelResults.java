package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PanelResults implements Results
{
    protected JPanel panel;

    private Tool<?> tool;

    public PanelResults(Tool<?> tool)
    {
        this.tool = tool;

        panel = new JPanel()
        {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean requestFocusInWindow()
            {
                Focuser.log("ResultsPanel.requestFocusInWindow");
                return getFocusComponent().requestFocusInWindow();
            }

            @Override
            public void requestFocus()
            {
                Focuser.log("ResultsPanel.requestFocus");
                getFocusComponent().requestFocus();
            }

        };
        panel.setLayout(new BorderLayout());
    }

    @Override
    public JPanel getComponent()
    {
        return panel;
    }

    @Override
    public Tool<?> getTool()
    {
        return tool;
    }
}
