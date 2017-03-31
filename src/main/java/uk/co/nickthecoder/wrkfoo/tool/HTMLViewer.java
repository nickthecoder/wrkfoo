package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.Icon;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.PanelResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabNotifier;
import uk.co.nickthecoder.wrkfoo.tool.HTMLViewer.HTMLResultsPanel;
import uk.co.nickthecoder.wrkfoo.tool.HTMLViewer.HTMLViewerTask;

public class HTMLViewer extends AbstractUnthreadedTool<HTMLResultsPanel, HTMLViewerTask>
{
    private HTMLResultsPanel htmlResultsPanel;

    private WebView webView;

    private String title = "HTMLViewer";

    private String docTitle = "";

    public HTMLViewer(File file)
    {
        this(Util.toURL(file).toString());
    }

    public HTMLViewer(String address)
    {
        this();
        getTask().address.setValue(address);
    }

    public HTMLViewer()
    {
        super(new HTMLViewerTask());

        // Without this, JavaFX will terminate when there are no JFXPanels visible, and
        // then all JavaFX stuff will stop working
        Platform.setImplicitExit(false);
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLongTitle()
    {
        return getTitle() + " " + docTitle;
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("html.png");
    }

    @Override
    public void updateResults()
    {
        htmlResultsPanel.show(getTask().address.getValue());
    }

    @Override
    public HTMLResultsPanel createResultsPanel()
    {
        htmlResultsPanel = new HTMLResultsPanel();
        return htmlResultsPanel;
    }

    private void pageLoaded()
    {
        docTitle = webView.getEngine().getTitle();
        if (docTitle == null) {
            docTitle = "";
        }
        TabNotifier.fireChangedTitle(getToolTab());
    }

    private void changedAddress(String address)
    {
        if ((address == null) || (task.address.getValue() == null)) {
            return;
        }

        if (address.equals(task.address.getValue())) {
            return;
        }
        task.address.setValue(address);
        this.getToolTab().pushHistory();
    }

    public class HTMLResultsPanel extends PanelResults
    {
        final JFXPanel fxPanel;

        public HTMLResultsPanel()
        {
            super(HTMLViewer.this);
            Util.assertIsEDT();

            fxPanel = new JFXPanel();
            getComponent().add(fxPanel, BorderLayout.CENTER);
        }

        public void show(String address)
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    buildFX(address);
                }
            });
        }

        /* Creates a WebView and fires up google.com */
        private void buildFX(String address)
        {
            if (!Platform.isFxApplicationThread()) {
                throw new RuntimeException("Not the FXApplicationThread");
            }
            StackPane root = new StackPane();
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);

            webView = new WebView();
            root.getChildren().add(webView);

            final WebEngine webEngine = webView.getEngine();
            webEngine.load(address);
            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>()
            {
                @Override
                public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue)
                {
                    changedAddress(webEngine.getLocation());
                }
            });

            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>()
            {
                @Override
                public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
                    Worker.State newValue)
                {
                    if (newValue != Worker.State.SUCCEEDED) {
                        return;
                    }

                    pageLoaded();
                }
            });
        }
    }

    public static class HTMLViewerTask extends Task
    {
        public StringParameter address = new StringParameter.Builder("address")
            .parameter();

        public HTMLViewerTask()
        {
            addParameters(address);
        }

        @Override
        public void body() throws MalformedURLException
        {
        }
    }
}
