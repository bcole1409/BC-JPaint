package controller;

import model.*;
import model.Point;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;
import java.util.ArrayList;

public class PasteCommand implements IUndoable, ICommand {
    public PaintCanvasBase paintCanvas;
    JPaintController masterJPaintController;
    ArrayList<ShapeCommand> JPCclipboard;
    ArrayList<ShapeCommand> OriginalShapes;
    ArrayList<GroupCommand> OriginalGroups;

    public PasteCommand(JPaintController myJPaintController, PaintCanvasBase myPaintCanvas, ArrayList<ShapeCommand> JPCclipboard, ArrayList<ShapeCommand> myOriginalShapes,
                        ArrayList<GroupCommand> myOriginalGroups){
        this.masterJPaintController = myJPaintController;
        this.paintCanvas = myPaintCanvas;
        this.JPCclipboard = JPCclipboard;
        this.OriginalShapes = myOriginalShapes;
        this.OriginalGroups = myOriginalGroups;
    }

    @Override
    public void run(){
        redo();
        CommandHistory.add(this);
    }

    @Override
    public void redo() {
        //create all the new pastedShapes and add them to drawList.
        for (ShapeCommand shape : JPCclipboard) {

            ShapeCommand newShape = createNewPastedShape(shape);
            //Draw New Shape
            newShape.drawMe();

            //add newShape to end of drawList
            masterJPaintController.drawList.add(newShape);
        }

        //create all the new pastedGroups
            //find the selectedGroups
        ArrayList<GroupCommand> selectedGroupsList = new ArrayList<GroupCommand>();
        for(GroupCommand myGroup : OriginalGroups){
            if(myGroup.containsAtLeastOneShape(JPCclipboard)){
                selectedGroupsList.add(myGroup);
            }
        }

        //REMEMBER: newly pastedShapes are sitting in the drawList

        for(GroupCommand selectedGroup : selectedGroupsList){
            //group1
            ArrayList<ShapeCommand> memberListOfNewPastedGroup = new ArrayList<ShapeCommand>(); //build up this list. Then use to create newPastedGroup

            for(ShapeCommand memberShape : selectedGroup.groupMemberList){
                ShapeCommand tempPastedShape = createNewPastedShape(memberShape);

                for(IShape drawListIShape : masterJPaintController.drawList){
                    ShapeCommand drawListSC = (ShapeCommand)drawListIShape;
                    if(tempPastedShape.SCIsEqual(drawListSC)){
                        memberListOfNewPastedGroup.add(drawListSC);
                    }
                }
            }

            GroupCommand newPastedGroup = GroupFactory.getNewGroupCommand(memberListOfNewPastedGroup,selectedGroup.Graphics2D);
            newPastedGroup.drawMe();
            masterJPaintController.listOfGroups.add(newPastedGroup);

        }
    }

    @Override
    public void undo(){
        //TO GO BACK TO THE ORIGINAL SHAPES
        //DRAWLIST = ORIGINAL SHAPES
        masterJPaintController.drawList = (ArrayList<IShape>)OriginalShapes.clone();
        masterJPaintController.listOfGroups = (ArrayList<GroupCommand>)OriginalGroups.clone();
    }

    private ShapeCommand createNewPastedShape(ShapeCommand shape){
        //find offset COORDINATES of new shapes, so we don't draw on top of each other
        int newPressedPointx = shape.p1.x + 20;
        int newPressedPointy = shape.p1.y + 20;
        int newReleasedPointx = shape.p2.x + 20;
        int newReleasedPointy = shape.p2.y + 20;

        //create new points for newShape
        Point newPressedPoint = new Point(newPressedPointx, newPressedPointy);
        Point newReleasedPoint = new Point(newReleasedPointx, newReleasedPointy);

        //create shape based on characteristics of existing shape and check which shape to draw
        ShapeCommand newShape = null;
        if(shape instanceof DrawRectangleCommand){
            newShape = ShapeFactory.getDrawRectangleCommand(masterJPaintController.drawList, paintCanvas, newPressedPoint, newReleasedPoint,
                    shape.primaryColor, shape.secondaryColor, shape.sShadingType);
        }

        else if(shape instanceof DrawEllipseCommand){
            newShape = ShapeFactory.getDrawEllipseCommand(masterJPaintController.drawList, paintCanvas, newPressedPoint, newReleasedPoint,
                    shape.primaryColor, shape.secondaryColor, shape.sShadingType);
        }

        else{
            newShape = ShapeFactory.getDrawTriangleCommand(masterJPaintController.drawList, paintCanvas, newPressedPoint, newReleasedPoint,
                    shape.primaryColor, shape.secondaryColor, shape.sShadingType);
        }
        return newShape;
    }
}
