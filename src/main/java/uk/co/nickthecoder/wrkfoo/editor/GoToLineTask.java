package uk.co.nickthecoder.wrkfoo.editor;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;

import uk.co.nickthecoder.jguifier.IntegerParameter;
import uk.co.nickthecoder.jguifier.Task;

public class GoToLineTask extends Task
{
    public IntegerParameter lineNumber;

    TextEditorPane textEditorPane;

    public GoToLineTask(TextEditorPane tep)
    {
        textEditorPane = tep;

        lineNumber = new IntegerParameter.Builder("Line Number")
            .optional()
            .range(1, tep.getLineCount())
            .parameter();

        addParameters(lineNumber);
        setName( "Go to Line" );
    }

    @Override
    public void body()
    {
        if (lineNumber.getValue() != null) {

            int line = lineNumber.getValue() - 1;

            try {
                textEditorPane.setCaretPosition(textEditorPane.getLineStartOffset(line));
            } catch (Exception e) {
                // We must have asked for a line number too large, so just go to the end of the document
                textEditorPane.setCaretPosition(textEditorPane.getDocument().getLength());
            }

        }
    }

}
