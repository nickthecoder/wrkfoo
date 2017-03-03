package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.command.GitStatusTask.GitStatusLine;

public class GitStatusTask extends Task implements ListResults<GitStatusLine>
{
    public List<GitStatusLine> results;

    public FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .value(new File(".")).parameter();

    public GitStatusTask()
    {
        super();
        addParameters(directory);
    }

    @Override
    public void body()
    {
        results = new ArrayList<GitStatusLine>();

        Exec gitStatus = new Exec("git", "status", "--porcelain").stdout().dir(directory.getValue());

        gitStatus.run();
        String output = gitStatus.getStdout().toString();
        String[] lines = output.split("\n");

        for (String line : lines) {
            char x = line.charAt(0);
            char y = line.charAt(1);
            String path = line.substring(3);
            String renamed = null;
            int arrow = line.indexOf(" -> ");
            if (arrow >= 0) {
                path = path.substring(0, arrow);
                renamed = path.substring(arrow + 4);
            }
            GitStatusLine gsl = new GitStatusLine(x, y, path, renamed);
            results.add(gsl);
        }
    }

    @Override
    public List<GitStatusLine> getResults()
    {
        return results;
    }

    public class GitStatusLine
    {
        public char x;
        public char y;
        public String path;
        public String renamed;
        public String name;

        public GitStatusLine(char x, char y, String path, String renamed)
        {
            this.x = x;
            this.y = y;
            this.path = path;
            int lastSlash = path.lastIndexOf( File.separatorChar );
            if (lastSlash >= 0) {
                name = path.substring(lastSlash+ 1);
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
    }
}
