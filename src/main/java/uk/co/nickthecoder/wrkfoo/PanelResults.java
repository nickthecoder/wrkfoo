package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PanelResults implements Results
{
    private JPanel panel;

    public PanelResults()
    {
        panel = new JPanel()
        {
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

    public JPanel getComponent()
    {
        return panel;
    }
}
