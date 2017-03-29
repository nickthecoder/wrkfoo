package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;
import java.net.MalformedURLException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.tool.HTMLViewer.HTMLViewerTask;

public class HTMLViewer extends AbstractUnthreadedTool<HTMLViewerTask>
{
    private HTMLResultsPanel htmlResultsPanel;

    public HTMLViewer()
    {
        super(new HTMLViewerTask());
    }

    @Override
    public void updateResults()
    {
        htmlResultsPanel.show(getTask().address.getValue());
    }

    @Override
    public ResultsPanel createResultsComponent()
    {
        htmlResultsPanel = new HTMLResultsPanel();
        return htmlResultsPanel;
    }

    public static class HTMLResultsPanel extends ResultsPanel
    {
        final JFXPanel fxPanel = new JFXPanel();

        public HTMLResultsPanel()
        {
            super();
            Util.assertIsEDT();
            setLayout(new BorderLayout());
            add(fxPanel, BorderLayout.CENTER);
        }

        public void show(String address)
        {
            Util.assertIsEDT();
            fxPanel.removeAll();
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
            // Without this, JavaFX will terminate when there are no JFXPanels visible, and
            // then all JFX stuff will stop working
            Platform.setImplicitExit(false);

            StackPane root = new StackPane();
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);

            WebView webView = new WebView();
            root.getChildren().add(webView);

            WebEngine webEngine = webView.getEngine();
            webEngine.load(address);
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
