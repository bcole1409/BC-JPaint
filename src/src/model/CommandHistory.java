package model;
import model.interfaces.IUndoable;
import java.util.Stack;

public class CommandHistory {
	//Keeps track of the history of commands using two stacks
	//undo stack keeps track of whats happened
	//redo stack keeps track of future commands
	private static final Stack<IUndoable> undoStack = new Stack<IUndoable>();
	private static final Stack<IUndoable> redoStack = new Stack<IUndoable>();

	//adds onto the history
	public static void add(IUndoable cmd) {
		undoStack.push(cmd);
		redoStack.clear();
	}


	public static boolean undo() {
		boolean result = !undoStack.empty();
		if (result) {
			//coming off the undostack
			IUndoable c = undoStack.pop();
			redoStack.push(c);

			//redo = 2
			//undo = 1
			//need to call this, goes to the drawCommand, undo yourself
			c.undo();
		}
		return result;
	}

	public static boolean redo() {
		boolean result = !redoStack.empty();
		if (result) {
			//coming off the redostack
			IUndoable c = redoStack.pop();
			undoStack.push(c);
			c.redo();
		}
		return result;
	}
}

