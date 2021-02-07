package controller;

import model.Point;
import model.ShapeCommand;
import model.interfaces.IUndoable;

public class MoveCommand implements IUndoable {
    //what is a move command?
    //ex) in 1903 the green rect moved 50 units
    Point pressedPoint;
    Point releasePoint;
    ShapeCommand shape;

    public MoveCommand(Point myPressedPoint, Point  myReleasedPoint, ShapeCommand myShape){
        this.pressedPoint = myPressedPoint;
        this.releasePoint = myReleasedPoint;
        this.shape = myShape;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }
}
    //need array given by selected function
    //public ArrayList<IShape> mySelectedList;
/*
    public moveCommand(ArrayList<IShape> selectedList) {
        this.mySelectedList = selectedList;
    }
}
*/