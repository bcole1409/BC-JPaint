package controller;

import model.*;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;
import java.util.ArrayList;

public class MoveCommand implements IUndoable, ICommand {
    public PaintCanvasBase paintCanvas;
    JPaintController masterJPaintController;
    Point pressedPoint;
    Point releasedPoint;
    ArrayList<ShapeCommand> originalShapes;
    ArrayList<ShapeCommand> JPCNewSelectedShapes;

    public MoveCommand(JPaintController myJPaintController, PaintCanvasBase myPaintCanvas, Point myPressedPoint, Point  myReleasedPoint, ArrayList<ShapeCommand> myOriginalShapes){
        this.masterJPaintController = myJPaintController;
        this.paintCanvas = myPaintCanvas;
        this.pressedPoint = myPressedPoint;
        this.releasedPoint = myReleasedPoint;
        this.originalShapes = myOriginalShapes;
        this.JPCNewSelectedShapes = new ArrayList<ShapeCommand>();
    }

    @Override
    public void run(){
        redo();
        CommandHistory.add(this);
    }

    @Override
    public void redo() {
        //reset list
        this.JPCNewSelectedShapes = new ArrayList<ShapeCommand>();
        for(ShapeCommand mySelectedShape : originalShapes) {
            //Find the shape we are trying to move in the drawList and Remove
            //empty drawlist iterate through and add every shape to temp drawlist unless it matches
            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();

            for (IShape myShape : masterJPaintController.drawList) {
                if (!mySelectedShape.SCIsEqual((ShapeCommand)myShape)){
                    tempDrawList.add(myShape);
                }
            }

            masterJPaintController.drawList = (ArrayList<IShape>) tempDrawList.clone();
            //System.out.println("DrawList size: " + drawList.size());

            int deltaX = BoundsUtility.calcDeltaX(pressedPoint, releasedPoint);
            int deltaY = BoundsUtility.calcDeltaY(pressedPoint, releasedPoint);

            ShapeCommand shapeInNewPosition = MoveUtility.CreateShapeGivenMovement(masterJPaintController.drawList, paintCanvas, mySelectedShape, deltaX, deltaY);
            //System.out.println("JPC mySelectedShape: " + mySelectedShape.p1.x + ", " + mySelectedShape.p1.y + "      " + mySelectedShape.p2.x + ", " + mySelectedShape.p2.y);
            //System.out.println("JPC shapeInNewPosition: " + shapeInNewPosition.p1.x + ", " + shapeInNewPosition.p1.y + "      " + shapeInNewPosition.p2.x + ", " + shapeInNewPosition.p2.y);
            masterJPaintController.drawList.add(shapeInNewPosition);
            //selectedShapesList.remove(0);
            JPCNewSelectedShapes.add(shapeInNewPosition);
        }
    }

    @Override
    public void undo() {
        //1. we need to calc deltax, deltay
        //System.out.println("inside undo move command. My DrawList size is: " + drawList.size());
        System.out.println("inside undo move command: " + masterJPaintController.drawList.toString());
        int deltax = BoundsUtility.calcDeltaX(pressedPoint,releasedPoint);
        int deltay = BoundsUtility.calcDeltaY(pressedPoint,releasedPoint);

        //clear selectedShapesList
        JPaintController.selectedShapesList = new ArrayList<ShapeCommand>();

        //3. remove moved shapes from the drawlist
        for(ShapeCommand mySelectedShape : originalShapes) {
            //System.out.println("MySelectedShape: " + mySelectedShape.p1.x + ", " + mySelectedShape.p1.y + "      " + mySelectedShape.p2.x + ", " + mySelectedShape.p2.y);
            ShapeCommand shapeInMovedPosition = MoveUtility.CreateShapeGivenMovement(masterJPaintController.drawList, paintCanvas, mySelectedShape, deltax, deltay);
            //System.out.println("A DrawList size: " + masterJPaintController.drawList.size());
            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();

            for (IShape myShape : masterJPaintController.drawList) {
                if (!shapeInMovedPosition.SCIsEqual((ShapeCommand)myShape)){ //comparison bug
                    tempDrawList.add(myShape);
                }
            }

            masterJPaintController.drawList = (ArrayList<IShape>) tempDrawList.clone();
            //System.out.println("B DrawList size: " + masterJPaintController.drawList.size());
            masterJPaintController.drawList.add(mySelectedShape);
            //selected shapes list is never updated
            JPaintController.selectedShapesList.add(mySelectedShape);
            //System.out.println("C DrawList size: " + masterJPaintController.drawList.size());
        }
    }
}