package controller;

import model.BoundsUtility;
import model.MoveUtility;
import model.Point;
import model.ShapeCommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;
import java.util.ArrayList;

public class MoveCommand implements IUndoable {
    //what is a move command?
    //ex) in 1903 the green rect moved 50 units
    public ArrayList<IShape> drawList;
    public PaintCanvasBase paintCanvas;
    JPaintController masterJPaintController;
    Point pressedPoint;
    Point releasePoint;
    ArrayList<ShapeCommand> originalShapes;

    public MoveCommand(JPaintController myJPaintController, PaintCanvasBase myPaintCanvas, Point myPressedPoint, Point  myReleasedPoint, ArrayList<ShapeCommand> myOriginalShapes){
        this.masterJPaintController = myJPaintController;
        //this.drawList = myJPaintController.drawList;
        this.paintCanvas = myPaintCanvas;
        this.pressedPoint = myPressedPoint;
        this.releasePoint = myReleasedPoint;
        this.originalShapes = myOriginalShapes;
    }

    @Override
    public void undo() {
        //1. we need to calc deltax, deltay
        //System.out.println("inside undo move command. My DrawList size is: " + drawList.size());
        System.out.println("inside undo move command: " + masterJPaintController.drawList.toString());
        int deltax = BoundsUtility.calcDeltaX(pressedPoint,releasePoint);
        int deltay = BoundsUtility.calcDeltaY(pressedPoint,releasePoint);

        //3. remove moved shapes from the drawlist
        for(ShapeCommand mySelectedShape : originalShapes) {
            System.out.println("MySelectedShape: " + mySelectedShape.p1.x + ", " + mySelectedShape.p1.y + "      " + mySelectedShape.p2.x + ", " + mySelectedShape.p2.y);
            ShapeCommand shapeInMovedPosition = MoveUtility.CreateShapeGivenMovement(masterJPaintController.drawList, paintCanvas, mySelectedShape, deltax, deltay);
            System.out.println("A DrawList size: " + masterJPaintController.drawList.size());
            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();

            for (IShape myShape : masterJPaintController.drawList) {
                if (!shapeInMovedPosition.SCIsEqual((ShapeCommand)myShape)){ //comparison bug
                    tempDrawList.add(myShape);
                }
            }

            masterJPaintController.drawList = (ArrayList<IShape>) tempDrawList.clone();
            System.out.println("B DrawList size: " + masterJPaintController.drawList.size());
            masterJPaintController.drawList.add(mySelectedShape);
            System.out.println("C DrawList size: " + masterJPaintController.drawList.size());
        }
    }

    @Override
    public void redo() {

    }
}