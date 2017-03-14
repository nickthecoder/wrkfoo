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

            GitStatusLine gsl = new GitStatusLine(index, work, path, renamed);
            if (gsl.getFile().isDirectory()) {
                addDirectory(gsl);
            }
            results.add(gsl);
        }
    }

    private void addDirectory(GitStatusLine gsl)
    {
        int prefix = gsl.getFile().getPath().length() + 1;

        FileLister fileLister = new FileLister().depth(10).includeHidden();
        List<File> listing = fileLister.listFiles(gsl.getFile());
        for (File file : listing) {
            String newPath = gsl.path + file.getPath().substring(prefix);
            GitStatusLine extra = new GitStatusLine(gsl.index, gsl.work, newPath, null);
            results.add(extra);
        }
    }

    @Override
    public List<GitStatusLine> getResults()
    {
        return results;
    }

    public class GitStatusLine
    {
        public char index;
        public char work;
        public String path;
        public String renamed;
        public String name;

        public GitStatusLine(char index, char work, String path, String renamed)
        {
            this.index = index;
            this.work = work;
            this.path = path;
            if (this.path.endsWith(File.separator)) {
                this.path = this.path.substring(0, this.path.length() - 1);
            }
            int lastSlash = path.lastIndexOf(File.separatorChar);
            if (lastSlash >= 0) {
                name = path.substring(lastSlash + 1);
            } else {
                name = path;
            }

            this.renamed = renamed;
        }

        public File getFile()
        {
            return new File(directory.getValue(), this.path);
        }

        public File getRenamedFile()
        {
            if (this.renamed == null) {
                return null;
            }
            return new File(directory.getValue(), this.renamed);
        }

        @Override
        public String toString()
        {
            return getFile().getPath();
        }
    }
}
