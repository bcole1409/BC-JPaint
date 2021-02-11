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
    private final CommandHistory myCommandHistory;

    //the list of shapes that should be immediately drawn on screen the next instant
    //any shape movement should update the drawList so that the moved shape is in its final position in the drawList
    public static ArrayList<IShape> drawList;
    private ArrayList<ShapeCommand> selectedShapesList;

    public JPaintController(IUiModule uiModule, IApplicationState applicationState, PaintCanvasBase MyPaintCanvas) {
        this.uiModule = uiModule;
        this.applicationState = applicationState;
        this.paintCanvas = MyPaintCanvas;

        //new
        this.drawList = new ArrayList<IShape>();
        this.selectedShapesList = new ArrayList<ShapeCommand>();
        this.myCommandHistory = new CommandHistory();
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
                drawList.add(myDRC);
                myDRC.run();
                //drawRectangle(pressedPoint, releasedPoint);
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
                            //myShape.debugGotSelected();
                        }
                        else{
                            tempUnselectShapes.add(myShape);
                        }
                    }
                    unselectedShapes = (ArrayList<ShapeCommand>)tempUnselectShapes.clone();
                }
            }
            //System.out.println(selectedShapesList.size() + " Shapes Currently selected");
        }
    }

    public void handleMouseModeMove(Point pressedPoint, Point releasedPoint){
        if(applicationState.getActiveMouseMode() == MouseMode.MOVE){
            //working with one shape now
            if(selectedShapesList.size() != 0) {
                //System.out.println("Executing New Move");
                //ArrayList<ShapeCommand> newSelectedShapesList = new ArrayList<ShapeCommand>();
                MoveCommand myMC = new MoveCommand(this, paintCanvas, pressedPoint, releasedPoint, (ArrayList<ShapeCommand>)selectedShapesList.clone());

                myCommandHistory.add(myMC);
                myMC.run();

                selectedShapesList = (ArrayList<ShapeCommand>)myMC.JPCNewSelectedShapes.clone();
                resetCanvas();
                redraw();
                //System.out.println("finished handling move: " + drawList.toString());
            }
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
