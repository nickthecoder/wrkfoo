package uk.co.nickthecoder.wrkfoo.option;


public class ScriptletException extends RuntimeException
{
    public GroovyScriptlet scriptlet;

    public ScriptletException(GroovyScriptlet scriptlet, Exception e)
    {
        super(e);
        this.scriptlet = scriptlet;
    }

    public String getMessage()
    {
        return "Error running groovy script : " + scriptlet.source + "\n" + super.getMessage();
    }
}