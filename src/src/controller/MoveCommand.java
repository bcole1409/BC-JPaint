package controller;

import model.BoundsUtility;
import model.MoveUtility;
import model.Point;
import model.ShapeCommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.util.ArrayList;

public class MoveCommand implements IUndoable {
    //what is a move command?
    //ex) in 1903 the green rect moved 50 units
    public ArrayList<IShape> drawList;
    public PaintCanvasBase paintCanvas;
    Point pressedPoint;
    Point releasePoint;
    ArrayList<ShapeCommand> shapesToBeMoved;

    public MoveCommand(ArrayList<IShape> myDrawList, PaintCanvasBase myPaintCanvas, Point myPressedPoint, Point  myReleasedPoint, ArrayList<ShapeCommand> myShapesToBeMoved){
        this.drawList = myDrawList;
        this.paintCanvas = myPaintCanvas;
        this.pressedPoint = myPressedPoint;
        this.releasePoint = myReleasedPoint;
        this.shapesToBeMoved = myShapesToBeMoved;
    }

    @Override
    public void undo() {
        //1. we need to calc deltax, deltay
        System.out.println("inside undo move command");
        int deltax = BoundsUtility.calcDeltaX(pressedPoint,releasePoint);
        int deltay = BoundsUtility.calcDeltaY(pressedPoint,releasePoint);

        int ideltax = deltax*-1;
        int ideltay = deltay*-1;
        //3. remove moved shapes from the drawlist
        for(ShapeCommand mySelectedShape : shapesToBeMoved) {
            ShapeCommand shapeInMovedPosition = MoveUtility.CreateShapeGivenMovement(drawList, paintCanvas, mySelectedShape, ideltax, ideltay);
            System.out.println("A DrawList size: " + drawList.size());
            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();
            for (IShape myShape : drawList) {
                if ((IShape) shapeInMovedPosition != myShape) { //comparison bug
                    tempDrawList.add(myShape);
                }
            }
            drawList = (ArrayList<IShape>) tempDrawList.clone();
            System.out.println("B DrawList size: " + drawList.size());
            drawList.add(mySelectedShape);
            System.out.println("C DrawList size: " + drawList.size());
        }
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