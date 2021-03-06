package controller;

import model.CommandHistory;
import model.Point;
import model.ShapeCommand;
import model.ShapeFactory;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.util.ArrayList;

public class DeleteCommand implements IUndoable, ICommand {
    public PaintCanvasBase paintCanvas;
    JPaintController masterJPaintController;
    ArrayList<ShapeCommand> selectedShapes;
    ArrayList<ShapeCommand> OriginalShapes;

    public DeleteCommand(JPaintController myJPaintController, PaintCanvasBase myPaintCanvas, ArrayList<ShapeCommand> mySelectedShapes, ArrayList<ShapeCommand> myOriginalShapes){
        this.masterJPaintController = myJPaintController;
        this.paintCanvas = myPaintCanvas;
        this.selectedShapes = mySelectedShapes;
        this.OriginalShapes = myOriginalShapes;
    }

    @Override
    public void run(){
        redo();
        CommandHistory.add(this);
    }

    @Override
    public void redo() {
        //check to see whether selected shapes list is empty
        for (ShapeCommand mySelectedShape : selectedShapes) {
            //Find the shape we are trying to DELETE in the drawList and Remove
            //empty drawlist iterate through and add every shape to temp drawlist unless it matches
            ArrayList<IShape> tempDrawList = new ArrayList<IShape>();

            for (IShape myShape : masterJPaintController.drawList) {
                if (!mySelectedShape.SCIsEqual((ShapeCommand) myShape)) {
                    tempDrawList.add(myShape);
                }
            }
            masterJPaintController.drawList = (ArrayList<IShape>) tempDrawList.clone();
        }
    }

    @Override
    public void undo(){
        masterJPaintController.drawList = (ArrayList<IShape>)OriginalShapes.clone();
    }
}
