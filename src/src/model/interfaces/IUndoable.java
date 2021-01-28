package model.interfaces;

import java.awt.*;

public interface IUndoable {
    void undo();
    void redo();
    void drawMe(); //only implements for draw commands
}
