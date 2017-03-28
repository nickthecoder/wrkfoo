package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.PatternParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.FileLister;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.tool.GitStatusTask.GitStatusLine;

public class GitStatusTask extends Task implements ListResults<GitStatusLine>
{
    public List<GitStatusLine> results;

    public FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .value(new File(".")).parameter();

    public PatternParameter filterIndex = new PatternParameter.Builder("filterIndex")
        .optional().parameter();

    public PatternParameter filterWork = new PatternParameter.Builder("filterWork")
        .optional().parameter();

    public GitStatusTask()
    {
        super();
        addParameters(directory, filterIndex, filterWork);
    }

    @Override
    public void body()
    {
        results = new ArrayList<>();

        Exec gitStatus = new Exec("git", "status", "--porcelain").stdout().dir(directory.getValue());

        gitStatus.run();
        String output = gitStatus.getStdout().toString();
        String[] lines = output.split("\n");

        for (String line : lines) {
            if (line.length() < 4) {
                continue;
            }

            char index = line.charAt(0);
            Pattern indexPattern = filterIndex.getPattern();
            if (indexPattern != null) {
                if (!indexPattern.matcher("" + index).matches()) {
                    continue;
                }
            }

            char work = line.charAt(1);
            Pattern workPattern = filterWork.getPattern();
            if (workPattern != null) {
                if (!workPattern.matcher("" + work).matches()) {
                    continue;
                }
            }

            String path = line.substring(3);
            String renamed = null;
            int arrow = path.indexOf(" -> ");
            if (arrow >= 0) {
                renamed = path.substring(0, arrow);
                path = path.substring(arrow + 4);
            }

            File file = createFile(path);
            GitStatusLine gsl = new GitStatusLine(file, index, work, renamed);
            if (file.isDirectory()) {
                addDirectory(file, index, work);
            }
            results.add(gsl);
        }
    }

    private void addDirectory(File directory, char index, char work)
    {
        FileLister fileLister = new FileLister().depth(10).includeHidden();
        List<File> listing = fileLister.listFiles(directory);
        for (File file : listing) {
            GitStatusLine extra = new GitStatusLine(file, index, work, null);
            results.add(extra);
        }
    }

    private File createFile( String path )
    {
        return new File( directory.getValue(), path );
    }
    
    @Override
    public List<GitStatusLine> getResults()
    {
        return results;
    }

    public class GitStatusLine extends WrappedFile
    {
        public char index;
        public char work;
        public String renamed;

        public GitStatusLine(File file, char index, char work, String renamed)
        {
            super(file);
            
            this.index = index;
            this.work = work;

            this.renamed = renamed;
        }

        @Override
        public File getBase()
        {
            return directory.getValue();
        }

        public File getRenamedFile()
        {
            if (this.renamed == null) {
                return null;
            }
            return new File(directory.getValue(), this.renamed);
        }
    }
}
