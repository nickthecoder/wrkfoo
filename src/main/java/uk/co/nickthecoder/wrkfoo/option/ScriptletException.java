package uk.co.nickthecoder.wrkfoo.option;

public class ScriptletException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public GroovyScriptlet scriptlet;

    public ScriptletException(GroovyScriptlet scriptlet, Exception e)
    {
        super(e);
        this.scriptlet = scriptlet;
    }

    @Override
    public String getMessage()
    {
        return "Error running groovy script.\n\n" + super.getMessage() + "\n" + scriptlet.source + "\n";
    }
}