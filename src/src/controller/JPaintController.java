package controller;

import model.*;
import model.Point;
import model.interfaces.IApplicationState;
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
    private ArrayList<IShape> drawList;
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
                myCommandHistory.add(myDRC);
                //drawRectangle(pressedPoint, releasedPoint);
            }

            if(applicationState.getActiveShapeType() == ShapeType.TRIANGLE){
                ShapeCommand myDTC = ShapeFactory.getDrawTriangleCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        applicationState.getActiveSecondaryColor(), applicationState.getActiveShapeShadingType());
                drawList.add(myDTC);
                myCommandHistory.add(myDTC);
            }

            if(applicationState.getActiveShapeType() == ShapeType.ELLIPSE){
                ShapeCommand myDEC = ShapeFactory.getDrawEllipseCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        applicationState.getActiveSecondaryColor(), applicationState.getActiveShapeShadingType());
                drawList.add(myDEC);
                myCommandHistory.add(myDEC);
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
                            myShape.debugGotSelected();
                            //TODO Comment Out
                        }
                        else{
                            tempUnselectShapes.add(myShape);
                        }
                    }
                    unselectedShapes = (ArrayList<ShapeCommand>)tempUnselectShapes.clone();
                }
            }
            //TODO Comment Out
            System.out.println(selectedShapesList.size() + " Shapes Currently selected");
        }
    }

    //ADDED CODE
    public void handleMouseModeMove(Point pressedPoint, Point releasedPoint){
        if(applicationState.getActiveMouseMode() == MouseMode.MOVE){
            //working with one shape now
            if(selectedShapesList.size() != 0){
                ShapeCommand mySelectedShape = selectedShapesList.get(0);
                MoveCommand myMC = new MoveCommand(pressedPoint,releasedPoint, mySelectedShape);
                myCommandHistory.add(myMC);

                //Find the shape we are trying to move in the drawList and Remove
                //empty drawlist iterate through and add every shape to temp drawlist unless it matches
                ArrayList<IShape> tempDrawList = new ArrayList<IShape>();
                for(IShape myShape : drawList){
                    //TYPES ISSUES
                    if((IShape)mySelectedShape != myShape){
                        tempDrawList.add(myShape);
                    }
                }

                drawList = (ArrayList<IShape>)tempDrawList.clone();
                System.out.println("DrawList size: " + drawList.size());
                //COMPARE SELECTED TO DRAWLIST
                //FOR SHAPE IN DRAWLIST
            }


            //FOR SHAPE IN SELECTLIST

                //IF SHAPE IN DRAWLIST = SHAPEINSELECTED SHAPE

                    //ADD NEW SHAPE TO DRAWLIST
                    //REMOVE SHAPE FROM DRAWLIST


        //somehow need to update moveCommand to command history
        //RESETCANVAS
        //REDRAWLIST
                //don''t know what correct algo is for new top left, but we can find w and h
                //delta change of releasedpoint.x + width =
                //delta change releasedpoint.y height =
        }
    }

    private void UndoButtonHandler(){
        resetCanvas();
        myCommandHistory.undo();
        //REMOVE LAST SHAPE
        int last = drawList.size();
        if(last != 0){
            drawList.remove(last-1);
        }
        redraw();
    }

    private void RedoButtonHandler(){
        resetCanvas();
        myCommandHistory.redo();
        //REMOVE LAST SHAPE
        redraw();
    }

    //ADDED
    private void SelectButtonHandler(){
        //call function
        //if function returns empty array do nothing
        //else
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

    /*
    public void drawTriangle(Point startingPoint, Point endingPoint){
        System.out.println("NOT DONE");
    }

    public void drawEllipse(Point startingPoint, Point endingPoint){
        System.out.println("NOT DONE");
    }
    */
}
        /*
        // Filled in rectangle
        Graphics2D graphics2d = paintCanvas.getGraphics2D();
        graphics2d.setColor(Color.GREEN);
        graphics2d.fillRect(12, 13, 200, 400);

        // Outlined rectangle
        graphics2d.setStroke(new BasicStroke(5));
        graphics2d.setColor(Color.BLUE);
        graphics2d.drawRect(12, 13, 200, 400);

        // Selected Shape
        Stroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{9}, 0);
        graphics2d.setStroke(stroke);
        graphics2d.setColor(Color.BLACK);
        graphics2d.drawRect(7, 8, 210, 410);
         */