package uk.co.nickthecoder.wrkfoo;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.tool.OpenProject;
import uk.co.nickthecoder.wrkfoo.tool.SaveProject;

/**
 * Handles all of MainWindows events from JButtons and keyboard shortcuts etc.
 * This class was created just to de-clutter MainWindow.
 */
public class MainWindowEvents
{
    private MainWindow mainWindow;

    MainWindowEvents(MainWindow mw)
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

    public void onNewTab()
    {
        Home tool = new Home();
        mainWindow.addTab(tool);
    }

    public void onDuplicateTab()
    {
        Tab tab = mainWindow.getCurrentTab();
        if (tab != null) {
            Tool<?> copyMain = tab.getMainHalfTab().getTool().duplicate();
            Tool<?> other = null;
            if (tab.getOtherHalfTab() != null) {
                other = tab.getOtherHalfTab().getTool().duplicate();
            }
            mainWindow.addTab(copyMain, other);
        }
    }

    public void onSplitHorizontal()
    {
        mainWindow.getCurrentTab().split(false);
    }

    public void onSplitVertical()
    {
        mainWindow.getCurrentTab().split(true);
    }

    public void onUnsplit()
    {
        Tab tab = mainWindow.getCurrentTab();
        tab.unsplit(tab.getMainHalfTab());
    }

    public void onCloseTab()
    {
        int currentTabIndex = mainWindow.mainTabs.getSelectedIndex();
        if (currentTabIndex >= 0) {
            mainWindow.mainTabs.removeTabAt(currentTabIndex);
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

    public void onSaveProject()
    {
        SaveProject sp = new SaveProject(mainWindow);
        sp.promptTask();
    }

    public void onOpenProject()
    {
        OpenProject openProject = new OpenProject();
        openProject.promptTask();
    }

    public void onNextTab()
    {
        mainWindow.mainTabs.nextTab();
    }

    public void onPreviousTab()
    {
        mainWindow.mainTabs.previousTab();
    }

    public void onCloseWindow()
    {
        mainWindow.setVisible(false);
    }

    public void onJumpToResults()
    {
        Tab tab = mainWindow.getCurrentTab();
        if (tab != null) {
            tab.getMainHalfTab().getTool().getToolPanel().getSplitPane().showLeft();
            Focuser.focusLater("MainWindow jumpToResults",
                tab.getMainHalfTab().getTool().getResultsPanel().getComponent(), 8);
        }
    }

    public void onJumpToParameters()
    {
        Tab tab = mainWindow.getCurrentTab();
        if (tab != null) {
            tab.getMainHalfTab().getTool().getToolPanel().getSplitPane().showRight();
            Focuser.focusLater("MainWindow jumpToParameters",
                tab.getMainHalfTab().getTool().getToolPanel().getParametersPanel(), 8);
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
