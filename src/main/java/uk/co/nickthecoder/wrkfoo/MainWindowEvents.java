package uk.co.nickthecoder.wrkfoo;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.tool.Projects;
import uk.co.nickthecoder.wrkfoo.tool.SaveProject;

/**
 * Handles all of MainWindows events from JButtons and keyboard shortcuts etc.
 * This class was created just to de-clutter MainWindow.
 */
public class MainWindowEvents
{
    private MainWindow mainWindow;

    MainWindowEvents( MainWindow mw )
    {
        mainWindow = mw;
    }
    
    public void onQuit()
    {
        // Close all of the windows, which will stop any stoppable tasks.
        for (MainWindow window : MainWindow.windows) {
            window.setVisible(false);
        }
        System.exit(0);
    }

    public void onHome()
    {
        Home tool = new Home();
        mainWindow.getCurrentOrNewTab().go(tool);
    }

    public void onNewTab()
    {
        Home tool = new Home();
        mainWindow.addTab(tool);
        mainWindow.tabbedPane.setSelectedIndex(mainWindow.tabbedPane.getTabCount() - 1);
    }

    public void onDuplicateTab()
    {
        ToolTab tab = mainWindow.getCurrentTab();
        if (tab != null) {
            Tool<?> copy = tab.getTool().duplicate();
            mainWindow.addTab(copy);
            mainWindow.tabbedPane.setSelectedIndex(mainWindow.tabbedPane.getTabCount() - 1);
        }
    }

    public void onCloseTab()
    {
        int currentTabIndex = mainWindow.tabbedPane.getSelectedIndex();
        if (currentTabIndex >= 0) {
            mainWindow.tabbedPane.removeTabAt(currentTabIndex);
        }
    }

    public void onNewWindow()
    {
        Home tool = new Home();
        MainWindow newWindow = new MainWindow();
        newWindow.addTab(tool);
        tool.go();
        newWindow.setVisible(true);
    }

    public void onBack()
    {
        if (mainWindow.getCurrentTab() != null) {
            mainWindow.getCurrentTab().onUndoTool();
        }
    }

    public void onForward()
    {
        if (mainWindow.getCurrentTab() != null) {
            mainWindow.getCurrentTab().onRedoTool();
        }
    }

    public void onRun()
    {
        if (mainWindow.getCurrentTab() != null) {
            mainWindow.getCurrentTab().getTool().getToolPanel().go();
        }
    }

    public void onStop()
    {
        if (mainWindow.getCurrentTab() != null) {
            mainWindow.getCurrentTab().getTool().stop();
        }
    }

    public void onWorkProjects()
    {
        Projects tool = new Projects();
        mainWindow.getCurrentOrNewTab().go(tool);
    }

    public void onSaveProject()
    {
        SaveProject sp = new SaveProject(mainWindow);
        sp.promptTask();
    }

    public void onExportTable()
    {
        if (mainWindow.getCurrentTab() != null) {
            Tool<?> tool = mainWindow.getCurrentTab().getTool();
            if (tool instanceof TableTool<?, ?>) {
                ExportTableData std = new ExportTableData((TableTool<?, ?>) tool);
                std.promptTask();
            }
        }
    }

    public void onNextTab()
    {
        mainWindow.tabbedPane.nextTab();
    }

    public void onPreviousTab()
    {
        mainWindow.tabbedPane.previousTab();
    }

    public void onCloseWindow()
    {
        mainWindow.setVisible(false);
    }

    public void onJumpToToolBar()
    {
        Focuser.focusLater("MainWindow.JumptToToolBar", mainWindow.optionTextField, 8);
    }

    public void onJumpToResults()
    {
        ToolTab tab = mainWindow.getCurrentTab();
        if (tab != null) {
            tab.getTool().getToolPanel().getSplitPane().showLeft();
            Focuser.focusLater("MainWindow jumpToResults", tab.getTool().getResultsPanel().getComponent(), 8);
        }
    }

    public void onJumpToParameters()
    {
        ToolTab tab = mainWindow.getCurrentTab();
        if (tab != null) {
            tab.getTool().getToolPanel().getSplitPane().showRight();
            Focuser.focusLater("MainWindow jumpToParameters", tab.getTool().getToolPanel().getParametersPanel(), 8);
        }
    }

    public void onShowError()
    {
        mainWindow.errorButton.setVisible(false);
        if (mainWindow.stackTrace != null) {
            JTextArea textArea = new JTextArea(mainWindow.stackTrace);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(900, 300));
            JOptionPane.showMessageDialog(null, scrollPane, "Error", JOptionPane.OK_OPTION);
        }
    }

}
