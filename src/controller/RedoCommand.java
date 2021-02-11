package controller;
import model.CommandHistory;
import model.interfaces.ICommand;

public class RedoCommand implements ICommand {
    @Override
    public void run() {
        CommandHistory.redo();
    }
}