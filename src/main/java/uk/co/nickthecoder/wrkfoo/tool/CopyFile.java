package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Exec;

/**
 * Copies a single file
 */
public class CopyFile extends Task
{
    public FileParameter source = new FileParameter.Builder("source").file().mustExist().parameter();

    public FileParameter destination = new FileParameter.Builder("destination").parameter();

    public CopyFile()
    {
        addParameters(source,destination);
    }
    
    public CopyFile( File file )
    {
        this();
        source.setDefaultValue(file);
    }
    
    @Override
    public void body()
    {
        Exec exec = new Exec("cp", source.getValue().getPath(), destination.getValue().getPath());
        exec.run();
    }

    public void promptTask()
    {
        if ( source.getValue() != null) {
            if (destination.getValue() == null) {
                destination.setDefaultValue(source.getValue().getParentFile());
            }
        }
        
        super.promptTask();
    }
}
