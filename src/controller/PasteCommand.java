package controller;

import model.*;
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

    public PasteCommand(JPaintController myJPaintController, PaintCanvasBase myPaintCanvas, ArrayList<ShapeCommand> JPCclipboard, ArrayList<ShapeCommand> myOriginalShapes){
        this.masterJPaintController = myJPaintController;
        this.paintCanvas = myPaintCanvas;
        this.JPCclipboard = JPCclipboard;
        this.OriginalShapes = myOriginalShapes;
    }

    @Override
    public void run(){
        redo();
        CommandHistory.add(this);
    }

    @Override
    public void redo() {
        for (ShapeCommand shape : JPCclipboard) {
            //newShape needs to be added to drawList
            //find offset COORDINATES of new shapes, so we dont draw on top of each other
            int newPressedPointx = shape.p1.x + 10;
            int newPressedPointy = shape.p1.y + 10;
            int newReleasedPointx = shape.p2.x + 10;
            int newReleasedPointy = shape.p2.y + 10;

            //create new points for newShape
            Point newPressedPoint = new Point(newPressedPointx, newPressedPointy);
            Point newReleasedPoint = new Point(newReleasedPointx, newReleasedPointy);

            //create shape based on characteristics of existing shape
            ShapeCommand newShape;

            if(shape instanceof DrawRectangleCommand){
                newShape = ShapeFactory.getDrawRectangleCommand(masterJPaintController.drawList, paintCanvas, newPressedPoint, newReleasedPoint,
                        shape.primaryColor, shape.secondaryColor, shape.sShadingType);
            }

            if(shape instanceof DrawEllipseCommand){
                newShape = ShapeFactory.getDrawEllipseCommand(masterJPaintController.drawList, paintCanvas, newPressedPoint, newReleasedPoint,
                        shape.primaryColor, shape.secondaryColor, shape.sShadingType);
            }
            else{
                newShape = ShapeFactory.getDrawTriangleCommand(masterJPaintController.drawList, paintCanvas, newPressedPoint, newReleasedPoint,
                        shape.primaryColor, shape.secondaryColor, shape.sShadingType);
            }

            //CREATE OFFSETTING POINTS FOR NEW SHAPE
            newShape.p1 = new Point(newPressedPointx, newPressedPointy);
            newShape.p2 = new Point(newReleasedPointx, newReleasedPointy);

            //add new points to new shape
            //add to end of drawList
            newShape.drawMe();
            masterJPaintController.drawList.add(newShape);

            //ICommand cmdPaste = new PasteCommand();
            //cmdPaste.run();

            //drawList.add(newShape);
            //add to command history
            //CommandHistory.add(this)
        }
    }

    @Override
    public void undo(){
    }
}
