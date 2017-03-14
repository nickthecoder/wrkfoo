package uk.co.nickthecoder.wrkfoo.editor;

public class SearcherEvent
{
    public enum Type { MARK, FIND, REPLACEALL };

    
    public Searcher searcher;

    public Type type;

    public String message;
    
    public SearcherEvent( Searcher searcher, Type type, String results )
    {
        this.searcher = searcher;
        this.type = type;
        this.message = results;
    }
}
