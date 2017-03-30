/**
 * The WrkFoo {@link MainWindow} is composed of a JToolBar at the top a status bar at the bottom, and the rest
 * is a {@link TabbedPane}. The contents of each tab is a {@link ToolPanel}, which is divided into two using a
 * {@link HidingSplitPane}.
 * The split pane has a {@link Results} on the top, and a JPanel (containing a
 * {@link ParametersPanel}) on the bottom .
 * <p>
 * Many {@link Tool}s produce lists, and these are displayed as JTables in the ResultsPanel.
 * The Tools do not generate the lists though, instead, each Tool has a {@link Task}. The Tasks have
 * {@link Parameter}s, for example, the {@link WrkF} Tool has a {@link FileParameter} holding the directory to be
 * listed.
 * </p>
 * <p>
 * Over time, the contents of each tab may change, replaced by different ToolPanels, but the a tab maintains the
 * same {@link ToolTab} (which is not a GUI component) forever.
 * ToolTab has a {@link History}, allowing the user to move backwards and forwards, similar to the the back/forwards
 * buttons in a web browser.
 * </p>
 * <p>
 * Most Tools, such as {@link WrkF}, {{@link DiskUsage}, {@link GitStatus}, {@link Grep} and {@link PlacesTool}
 * produce lists. Each Tool have {@link Option}s, that can be used to interact with the data. For example, the GitStatus
 * Tool has an Option code of &quot;co&quot; to check out a file. The Options are grouped together using the
 * {@link Options} class.
 * </p>
 * <p>
 * Currently all Options use Groovy scripts to perform their actions (See {@link GroovyOption}).
 * </p>
 * <p>
 * Options can be executed against a table row, or against the tool as a whole (without referencing a particular row).
 * For example the GitStatus has row options to check-out files, to perform diffs, and to add files to the git index.
 * It also has non-row options, such as &quot;c&quot; to perform a git commit.
 * </p>
 * <p>
 * There are non-list based Tools too, such as {@link HTMLViewer}, {@link Terminal} and
 * {@link Editor} (a simple text editor),
 * </p>
 * 
 * <a href="classDiagram.html"><img src="classDiagramThumb.png"/></a>
 * Click the image, for an interactive class diagram.
 */
package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.wrkfoo.Results;

import uk.co.nickthecoder.wrkfoo.tool.WrkF;
import uk.co.nickthecoder.wrkfoo.tool.GitStatus;
import uk.co.nickthecoder.wrkfoo.tool.DiskUsage;
import uk.co.nickthecoder.wrkfoo.tool.Grep;
import uk.co.nickthecoder.wrkfoo.tool.PlacesTool;
import uk.co.nickthecoder.wrkfoo.tool.HTMLViewer;
import uk.co.nickthecoder.wrkfoo.tool.Terminal;

import uk.co.nickthecoder.wrkfoo.editor.Editor;

import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.GroovyOption;

import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
