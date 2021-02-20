package controller;

import model.*;
import model.Point;
import model.interfaces.IApplicationState;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import view.EventName;
import view.interfaces.IUiModule;
import view.interfaces.PaintCanvasBase;
import model.CommandHistory;
import java.util.ArrayList;
import java.awt.*;

public class JPaintController implements IJPaintController {
    private final IUiModule uiModule;
    private final IApplicationState applicationState;
    private final PaintCanvasBase paintCanvas;

    //the list of shapes that should be immediately drawn on screen the next instant
    //any shape movement should update the drawList so that the moved shape is in its final position in the drawList
    //ClipBoard used for storing selectedShapes when copy is active
    public static ArrayList<IShape> drawList;
    public static ArrayList<ShapeCommand> selectedShapesList;
    public static ArrayList<ShapeCommand> clipboard;

    public JPaintController(IUiModule uiModule, IApplicationState applicationState, PaintCanvasBase MyPaintCanvas) {
        this.uiModule = uiModule;
        this.applicationState = applicationState;
        this.paintCanvas = MyPaintCanvas;
        this.drawList = new ArrayList<IShape>();
        this.selectedShapesList = new ArrayList<ShapeCommand>();
        this.clipboard = new ArrayList<ShapeCommand>();
    }

    @Override
    public void setup() {
        setupEvents();
    }

    private void setupEvents() {
        uiModule.addEvent(EventName.CHOOSE_SHAPE, () -> applicationState.setActiveShape());
        uiModule.addEvent(EventName.CHOOSE_PRIMARY_COLOR, () -> applicationState.setActivePrimaryColor());
        uiModule.addEvent(EventName.CHOOSE_SECONDARY_COLOR, () -> applicationState.setActiveSecondaryColor());
        uiModule.addEvent(EventName.CHOOSE_SHADING_TYPE, () -> applicationState.setActiveShadingType());
        uiModule.addEvent(EventName.CHOOSE_MOUSE_MODE, () -> applicationState.setActiveStartAndEndPointMode()); //LAMBDA function is an anonymous function
        uiModule.addEvent(EventName.UNDO, () -> UndoButtonHandler());
        uiModule.addEvent(EventName.REDO, () -> RedoButtonHandler());
        uiModule.addEvent(EventName.COPY, () -> CopyButtonHandler());
        uiModule.addEvent(EventName.PASTE, () -> PasteButtonHandler());
        uiModule.addEvent(EventName.DELETE, () -> DeleteButtonHandler());
    }

    public void mouseReleasedController(Point pressedPoint, Point releasedPoint){
        handleMouseModeDraw(pressedPoint, releasedPoint);
        handleMouseModeSelect(pressedPoint, releasedPoint);
        handleMouseModeMove(pressedPoint, releasedPoint);
    }

    public void handleMouseModeDraw(Point pressedPoint, Point releasedPoint){
        if(applicationState.getActiveMouseMode() == MouseMode.DRAW){
            if(applicationState.getActiveShapeType() == ShapeType.RECTANGLE){
                ShapeCommand myDRC = ShapeFactory.getDrawRectangleCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        applicationState.getActiveSecondaryColor(), applicationState.getActiveShapeShadingType());
                //Wrap in proxy?
                drawList.add(myDRC);
                myDRC.run(); //ADDS COMMAND TO COMMAND HISTORY. THEN USES GRAPHICS2D TO ACTUALLY DRAW THE SHAPE
            }

            if(applicationState.getActiveShapeType() == ShapeType.TRIANGLE){
                ShapeCommand myDTC = ShapeFactory.getDrawTriangleCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        applicationState.getActiveSecondaryColor(), applicationState.getActiveShapeShadingType());
                drawList.add(myDTC);
                myDTC.run();
            }

            if(applicationState.getActiveShapeType() == ShapeType.ELLIPSE){
                ShapeCommand myDEC = ShapeFactory.getDrawEllipseCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        applicationState.getActiveSecondaryColor(), applicationState.getActiveShapeShadingType());
                drawList.add(myDEC);
                myDEC.run();
            }
        }
    }

    public void handleMouseModeSelect(Point pressedPoint, Point releasedPoint){
        if(applicationState.getActiveMouseMode() == MouseMode.SELECT){
            resetCanvas();
            redraw();
            this.selectedShapesList = new ArrayList<ShapeCommand>(); //everytime user selects we clear the selection list
            ArrayList<ShapeCommand> unselectedShapes = (ArrayList<ShapeCommand>)drawList.clone(); //clone of drawList

            Point topLeftCorner = BoundsUtility.calcTopLeftCorner(pressedPoint, releasedPoint);
            int width = BoundsUtility.calcWidth(pressedPoint, releasedPoint);
            int height = BoundsUtility.calcHeight(pressedPoint, releasedPoint);

            for(int x = topLeftCorner.x; x <= topLeftCorner.x + width; x++){
                for(int y = topLeftCorner.y; y <= topLeftCorner.y + height; y++){
                    ArrayList<ShapeCommand> tempUnselectShapes = new ArrayList<ShapeCommand>();
                    for (ShapeCommand myShape : unselectedShapes)
                    {
                        if(myShape.didCollideWithMe(x,y)){
                            selectedShapesList.add(myShape);
                            //TODO PROXY OUTLINE SELECTED SHAPES
                            //myShape.debugGotSelected(); //function only implemented in proxy
                            ShapeCommandProxy mySCP = new ShapeCommandProxy(myShape);
                            mySCP.drawMe(); //WARNING CAUSES SHAPE TO BE DRAWN A SECOND TIME ON TOP OF ITSELF
                        }

                        else{
                            tempUnselectShapes.add(myShape);
                        }
                    }
                    unselectedShapes = (ArrayList<ShapeCommand>)tempUnselectShapes.clone();
                }
            }
        }
    }

    public void handleMouseModeMove(Point pressedPoint, Point releasedPoint){
        if(applicationState.getActiveMouseMode() == MouseMode.MOVE){
            //working with one shape now
            if(selectedShapesList.size() != 0) {
                //System.out.println("Executing New Move");
                //ArrayList<ShapeCommand> newSelectedShapesList = new ArrayList<ShapeCommand>();
                MoveCommand myMC = new MoveCommand(this, paintCanvas, pressedPoint, releasedPoint, (ArrayList<ShapeCommand>)selectedShapesList.clone());
                myMC.run();
                selectedShapesList = (ArrayList<ShapeCommand>)myMC.JPCNewSelectedShapes.clone();
                resetCanvas();
                redraw();
                //loop to draw outline of selectedshapes
                for(ShapeCommand selectedShape : selectedShapesList){
                    ShapeCommandProxy mySCP = new ShapeCommandProxy(selectedShape);
                    mySCP.drawMe(); //WARNING CAUSES SHAPE TO BE DRAWN A SECOND TIME ON TOP OF ITSELF
                }
                //System.out.println("finished handling move: " + drawList.toString());
            }
        }
    }

    public void CopyButtonHandler(){
        this.clipboard = new ArrayList<ShapeCommand>(); //create empty list
        //check whether selected list is empty
        if(selectedShapesList.size() != 0){
            for(ShapeCommand shape : selectedShapesList){
                //create copy of selected shapes and save as "clipboard"
                clipboard.add(shape);
            }
        }
    }

    //PASTE SHOULD OFFSET ORIGINAL SHAPES
    private void PasteButtonHandler() {
        //check whether clipboard is empty
        if (clipboard.size() != 0) {
            //CREATED PASTE COMMAND
            PasteCommand myPC = new PasteCommand(this, paintCanvas, clipboard, (ArrayList<ShapeCommand>)drawList.clone());
            //ADD TO COMMAND HISTORY
            myPC.run();
        }
    }

    private void DeleteButtonHandler() {
        if (selectedShapesList.size() != 0) {
            DeleteCommand myDC = new DeleteCommand(this, paintCanvas, selectedShapesList, (ArrayList<ShapeCommand>)drawList.clone());
            myDC.run();
            resetCanvas();
            redraw();
        }
    }

    private void UndoButtonHandler(){
        //System.out.println("Undo Button Handler");
        resetCanvas();
        ICommand cmdUndo = new UndoCommand();
        cmdUndo.run();
        //case: undo movement algorithm creates new shapes in order to work which automatically get drawn on creation
        //so we need to reset the canvas again
        resetCanvas();
        redraw();
    }

    private void RedoButtonHandler(){
        resetCanvas();
        ICommand cmdRedo = new RedoCommand();
        cmdRedo.run();
        redraw();
    }

    private void resetCanvas(){
        //RESET THE CANVAS
        Graphics2D graphics2d = paintCanvas.getGraphics2D();
        graphics2d.setColor(Color.WHITE);
        graphics2d.fillRect(0, 0, 10000, 10000);
    }

    private void redraw(){
        //REDRAW ALL SHAPES
        for(IShape shape : drawList){
            shape.drawMe();
        }
    }
}

/*
            //draw selected shape outline
            for(ShapeCommand shape : selectedShapesList){
                //create temp shape/Stroke
                ShapeCommand tempShape = shape;
                Stroke tempShapeStroke = new BasicStroke(3, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 1, new float[]{9}, 1);
                paintCanvas.getGraphics2D().setStroke(tempShapeStroke);

                System.out.println(shape.p1.x + " " + shape.p1.y + " " + (BoundsUtility.calcWidth(tempShape.p1,tempShape.p2)));
                System.out.println(tempShape.p1.x + " " + tempShape.p1.y + " " + tempShape.p2.x + " " + tempShape.p2.x + " ");

                paintCanvas.getGraphics2D().drawRect(tempShape.p1.x - 5,tempShape.p1.y - 5,
                        BoundsUtility.calcWidth(tempShape.p1,tempShape.p2) + 10,BoundsUtility.calcHeight(tempShape.p1,tempShape.p2) + 10);
            }
            */
//System.out.println(selectedShapesList.size() + " Shapes Currently selected");