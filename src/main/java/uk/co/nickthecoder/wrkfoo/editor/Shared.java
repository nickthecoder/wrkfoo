package uk.co.nickthecoder.wrkfoo.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;

/**
 * When two editors share the same document, we need to track the dirty state. We can't rely on
 * TextEditorPane.isDirty() because the two instances of TextEditorPane will disagree (only the
 * one that performed the save will believe that the document is clean).
 * 
 * Whenever one of the TextEditorPanes change its dirty state, we will be notified, and change
 * OUR dirty flag, and then notify all EditorListeners of the change.
 */
public class Shared
{
    private boolean dirty;

    private List<EditorListener> listeners = new ArrayList<>();

    public Shared()
    {
        listeners = new ArrayList<>();
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public void listen(final TextEditorPane textPane)
    {
        textPane.addPropertyChangeListener(TextEditorPane.DIRTY_PROPERTY, new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                dirty = textPane.isDirty();
                for (EditorListener l : listeners) {
                    l.dirtyChanged();
                }
            }
        });
    }

    public void addEditorListener(EditorListener listener)
    {
        listeners.add(listener);
    }

    public void removeEditorListener(EditorListener listener)
    {
        listeners.remove(listener);
    }

}
