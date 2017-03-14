package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.BooleanParameter;
import uk.co.nickthecoder.jguifier.PatternParameter;
import uk.co.nickthecoder.jguifier.StringChoiceParameter;
import uk.co.nickthecoder.jguifier.util.Exec;

public class GrepTask extends FileTask
{
    public PatternParameter regex = new PatternParameter.Builder("regex")
        .parameter();

    public StringChoiceParameter type = new StringChoiceParameter.Builder("type")
        .choice("G", "G", "Regular")
        .choice("E", "E", "Extended")
        .choice("F", "F", "Fixed")
        .choice("P", "P", "Perl")
        .parameter();

    public BooleanParameter matchCase = new BooleanParameter.Builder("matchCase")
        .parameter();

    public BooleanParameter matchWords = new BooleanParameter.Builder("matchWords")
        .description("Force pattern to match only whole words")
        .parameter();

    public BooleanParameter matchLines = new BooleanParameter.Builder("matchLines")
        .description("Force pattern to match only whole lines")
        .parameter();

    public BooleanParameter invertResults = new BooleanParameter.Builder("invertResults")
        .description("List files NOT matching the pattern")
        .parameter();

    public GrepTask(File directory)
    {
        this();
        this.directory.setDefaultValue(directory);
    }

    public GrepTask()
    {
        addParameters(regex, directory, type, matchCase, matchWords, matchLines, invertResults);
    }

    @Override
    public Exec getExec()
    {
        return new Exec("grep", "-rHs" + type.getValue(),
            invertResults.getValue() ? "-L" : "-l",
            matchCase.getValue() ? null : "-i",
            matchWords.getValue() ? "-w" : null,
            matchLines.getValue() ? "-x" : null,
            regex.getRegex(), ".");
    }
}
