package controller;

import model.*;
import model.Point;
import model.interfaces.IApplicationState;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.EventName;
import view.gui.PaintCanvas;
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

    //new
    //the list of shapes that are currently visible on screen
    private final ArrayList<IShape> drawList;

    public JPaintController(IUiModule uiModule, IApplicationState applicationState, PaintCanvasBase MyPaintCanvas) {
        this.uiModule = uiModule;
        this.applicationState = applicationState;
        this.paintCanvas = MyPaintCanvas;

        //new
        this.drawList = new ArrayList<IShape>();
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
        if(applicationState.getActiveMouseMode() == MouseMode.DRAW){
            if(applicationState.getActiveShapeType() == ShapeType.RECTANGLE){
                DrawRectangleCommand myDRC = new DrawRectangleCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        applicationState.getActiveShapeShadingType());
                drawList.add(myDRC);
                myCommandHistory.add(myDRC);
                //drawRectangle(pressedPoint, releasedPoint);
            }

            if(applicationState.getActiveShapeType() == ShapeType.TRIANGLE){
               //drawRectangle(pressedPoint, releasedPoint);
                //DrawTriangleCommand myDRC = new DrawTriangleCommand(drawList, paintCanvas, pressedPoint, releasedPoint, applicationState.getActivePrimaryColor(),
                        //applicationState.getActiveShapeShadingType());
                //drawList.add(myDRC);
                //myCommandHistory.add(myDRC);
            }

            if(applicationState.getActiveShapeType() == ShapeType.ELLIPSE){
                //drawRectangle(pressedPoint, releasedPoint);
            }
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