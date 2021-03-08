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
    ArrayList<GroupCommand> originalGroups;
    ArrayList<GroupCommand> JPCNewListOfGroups;

    public MoveCommand(JPaintController myJPaintController, PaintCanvasBase myPaintCanvas, Point myPressedPoint, Point myReleasedPoint, ArrayList<ShapeCommand> myOriginalShapes,
                       ArrayList<GroupCommand> myOriginalGroups){
        this.masterJPaintController = myJPaintController;
        this.paintCanvas = myPaintCanvas;
        this.pressedPoint = myPressedPoint;
        this.releasedPoint = myReleasedPoint;
        this.originalShapes = myOriginalShapes;
        this.JPCNewSelectedShapes = new ArrayList<ShapeCommand>();
        this.originalGroups = myOriginalGroups;
        this.JPCNewListOfGroups = new ArrayList<GroupCommand>();
    }

    @Override
    public void run(){
        redo();
        CommandHistory.add(this);
    }

    @Override
    public void redo() {
        int deltaX = BoundsUtility.calcDeltaX(pressedPoint, releasedPoint);
        int deltaY = BoundsUtility.calcDeltaY(pressedPoint, releasedPoint);
        //movement for selectedShapesList and drawList
        //reset list
        this.JPCNewSelectedShapes = new ArrayList<ShapeCommand>();
        for(ShapeCommand mySelectedShape : originalShapes) {
            //step 1) Find the shape we are trying to move in the drawList and Remove

            //empty drawlist iterate through and add every shape to temp drawlist unless it matches
            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();

            for (IShape myShape : masterJPaintController.drawList) {
                if (!mySelectedShape.SCIsEqual((ShapeCommand)myShape)){
                    tempDrawList.add(myShape);
                }
            }

            masterJPaintController.drawList = (ArrayList<IShape>) tempDrawList.clone();
            //System.out.println("DrawList size: " + drawList.size());

            //step 2) create shapeInNewPosition, then add it to the drawList and selectedShapesList


            //Important: shapeInNewPosition is never added to CommandHistory
            ShapeCommand shapeInNewPosition = MoveUtility.CreateShapeGivenMovement(masterJPaintController.drawList, paintCanvas, mySelectedShape, deltaX, deltaY);
            //System.out.println("JPC mySelectedShape: " + mySelectedShape.p1.x + ", " + mySelectedShape.p1.y + "      " + mySelectedShape.p2.x + ", " + mySelectedShape.p2.y);
            //System.out.println("JPC shapeInNewPosition: " + shapeInNewPosition.p1.x + ", " + shapeInNewPosition.p1.y + "      " + shapeInNewPosition.p2.x + ", " + shapeInNewPosition.p2.y);
            masterJPaintController.drawList.add(shapeInNewPosition);
            //selectedShapesList.remove(0);
            JPCNewSelectedShapes.add(shapeInNewPosition);
        }

        //bug????? fix movement for ssl and dl?
        //movement for groups
        this.JPCNewListOfGroups = new ArrayList<GroupCommand>();
        for(GroupCommand myGroup : originalGroups){
            if(myGroup.containsAtLeastOneShape(originalShapes)) { //ContainsAtLeastOneShape is a double for-loop
                GroupCommand newMovedGroup = MoveUtility.CreateGroupGivenMovement(masterJPaintController.drawList, paintCanvas, myGroup, deltaX, deltaY);
                newMovedGroup.drawMe(); //Only reason is to calculate TopLeft/BottomRight Corners. Don't care about actual drawing.
                JPCNewListOfGroups.add(newMovedGroup);
            }
            else{
                myGroup.drawMe();
                JPCNewListOfGroups.add(myGroup);
            }
        }

        masterJPaintController.selectedShapesList = (ArrayList<ShapeCommand>)JPCNewSelectedShapes.clone();
        masterJPaintController.listOfGroups = (ArrayList<GroupCommand>)JPCNewListOfGroups.clone();
    }

    @Override
    public void undo() {

        //---UNDO CODE BLOCK FOR SELECTEDSHAPESLIST AND DRAWLIST---
        //1. we need to calc deltax, deltay

        //System.out.println("inside undo move command. My DrawList size is: " + drawList.size());
        //System.out.println("inside undo move command: " + masterJPaintController.drawList.toString());
        int deltaX = BoundsUtility.calcDeltaX(pressedPoint,releasedPoint);
        int deltaY = BoundsUtility.calcDeltaY(pressedPoint,releasedPoint);

        //clear selectedShapesList
        JPaintController.selectedShapesList = new ArrayList<ShapeCommand>();


        for(ShapeCommand mySelectedShape : originalShapes) {
            //2. remove moved shapes from the drawlist
            ShapeCommand shapeInMovedPosition = MoveUtility.CreateShapeGivenMovement(masterJPaintController.drawList, paintCanvas, mySelectedShape, deltaX, deltaY);

            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();

            for (IShape myShape : masterJPaintController.drawList) {
                if (!shapeInMovedPosition.SCIsEqual((ShapeCommand)myShape)){ //comparison bug
                    tempDrawList.add(myShape);
                }
            }

            masterJPaintController.drawList = (ArrayList<IShape>) tempDrawList.clone();
            //3. add original shape back into drawList
            masterJPaintController.drawList.add(mySelectedShape);

            //selected shapes list is never updated
            JPaintController.selectedShapesList.add(mySelectedShape);

            //System.out.println("C DrawList size: " + masterJPaintController.drawList.size());
        }

        //---UNDO CODE BLOCK FOR GROUPS---
        ArrayList<ShapeCommand> movedShapesList = new ArrayList<ShapeCommand>();
        for(ShapeCommand myShape : originalShapes){
            ShapeCommand movedShape = MoveUtility.CreateShapeGivenMovement(masterJPaintController.drawList, paintCanvas, myShape, deltaX, deltaY);
            movedShapesList.add(movedShape);
        }

        ArrayList<GroupCommand> newJPCListOfGroups = new ArrayList<GroupCommand>();
        for(GroupCommand myGroup : masterJPaintController.listOfGroups){
            if(myGroup.containsAtLeastOneShape(movedShapesList)){
                GroupCommand newMovedGroup = MoveUtility.CreateGroupGivenMovement(masterJPaintController.drawList, paintCanvas, myGroup, -deltaX, -deltaY);
                newMovedGroup.drawMe(); //Only reason is to calculate TopLeft/BottomRight Corners. Don't care about actual drawing.
                newJPCListOfGroups.add(newMovedGroup);
            }
            else{
                newJPCListOfGroups.add(myGroup);
            }
        }
        masterJPaintController.listOfGroups = newJPCListOfGroups;
        System.out.println("148 size: " + masterJPaintController.listOfGroups.size());
    }
}