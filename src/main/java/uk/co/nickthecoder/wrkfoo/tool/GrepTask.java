package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.IntegerParameter;
import uk.co.nickthecoder.jguifier.parameter.PatternParameter;
import uk.co.nickthecoder.jguifier.parameter.StringChoiceParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.tool.GrepTask.GrepRow;


public class GrepTask extends GenericFileTask<GrepRow>
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

    public IntegerParameter maxMatches = new IntegerParameter.Builder("maxMatches")
        .value(1).required().range(1, 100)
        .parameter();

    public GrepTask(File directory)
    {
        this();
        this.directory.setDefaultValue(directory);
    }

    Pattern parseLineNumber;

    public GrepTask()
    {
        addParameters(regex, directory, maxMatches, type, matchCase, matchWords, matchLines, invertResults);
        parseLineNumber = Pattern.compile(":[0-9]*:");
    }

    @Override
    public Exec getExec()
    {
        Exec exec = new Exec("grep", "-rHsn" + type.getValue(),
            invertResults.getValue() ? "-L" : null,
            matchCase.getValue() ? null : "-i",
            matchWords.getValue() ? "-w" : null,
            matchLines.getValue() ? "-x" : null);

        if (!invertResults.getValue()) {
            exec.add("-m");
            exec.add("" + maxMatches.getValue());
        }

        exec.add(regex.getValue());
        exec.add(".");

        return exec;
    }

    @Override
    protected GrepRow parseLine(String line)
    {
        if (invertResults.getValue()) {
            return new GrepRow(line, 0, "");
        } else {

            Matcher matcher = parseLineNumber.matcher(line);

            if (matcher.find()) {

                String path = line.substring(0, matcher.start() );
                int lineNumber = Integer.parseInt(line.substring(matcher.start() + 1, matcher.end() - 1));
                String text = line.substring(matcher.end() );
                return new GrepRow(path, lineNumber, text);

            } else {
                return new GrepRow(line, 0, "");
            }
        }

    }

    public class GrepRow extends RelativePath
    {
        public int line;
        public String text;
        
        public GrepRow(String path, int lineNumber, String text)
        {
            super(path);
            this.line = lineNumber;
            this.text = text;
        }

        @Override
        public File getBase()
        {
            return directory.getValue();
        }
    }

}
