package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import org.codehaus.groovy.control.CompilationFailedException;

import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.Tool;

public class GroovyTools extends WrkFBase implements DirectoryTool
{
    private ChoiceParameter<File> directoryChoice;
    
    public GroovyTools()
    {
        super();

        task.fileExtensions.setDefaultValue("groovy");
        task.directory.setDefaultValue(new File(Resources.getInstance().getHomeDirectory(), "bin"));

        task.includeDirectories.visible = false;
        task.includeFiles.visible = false;
        task.includeHidden.visible = false;
        task.enterHidden.visible = false;
        task.depth.visible = false;
        task.directoryPattern.visible = false;
        task.order.visible = false;
        task.reverse.visible = false;
        task.canonical.visible = false;
        task.sort.visible = false;

        getColumns().find("size").visible = false;
        
        directoryChoice = Resources.getInstance().createGroovyDirectoryChoice();
        directoryChoice.addListener(new ParameterListener() {
            @Override
            public void changed(Parameter source)
            {
                task.directory.setValue(directoryChoice.getValue());
            }
        });

        task.insertParameter(0, directoryChoice);
        task.directory.visible = false;

    }
    
    @Override
    public String getTitle()
    {
        return "Groovy Tools";
    }
    
    @Override
    public Icon getIcon()
    {
        return Resources.icon("groovyTools.png");
    }
    
    public Tool<?> openGroovyTool(File file)
        throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException
    {
        return Resources.getInstance().createGroovyTool(file);
    }
}
