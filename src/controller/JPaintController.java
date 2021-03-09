package controller;

import model.*;
import model.Point;
import model.interfaces.IApplicationState;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import view.EventName;
import view.interfaces.IUiModule;
import view.interfaces.PaintCanvasBase;
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
    public static ArrayList<GroupCommand> listOfGroups;

    public JPaintController(IUiModule uiModule, IApplicationState applicationState, PaintCanvasBase MyPaintCanvas) {
        this.uiModule = uiModule;
        this.applicationState = applicationState;
        this.paintCanvas = MyPaintCanvas;
        this.drawList = new ArrayList<IShape>();
        this.selectedShapesList = new ArrayList<ShapeCommand>();
        this.clipboard = new ArrayList<ShapeCommand>();
        this.listOfGroups = new ArrayList<GroupCommand>();
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
        uiModule.addEvent(EventName.GROUP, () -> GroupButtonHandler());
        uiModule.addEvent(EventName.UNGROUP, () -> UngroupButtonHandler());
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
                //Wrap in proxy?`
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
            ArrayList<GroupCommand> unselectedGroups = (ArrayList<GroupCommand>)listOfGroups.clone();

            Point topLeftCorner = BoundsUtility.calcTopLeftCorner(pressedPoint, releasedPoint);
            int width = BoundsUtility.calcWidth(pressedPoint, releasedPoint);
            int height = BoundsUtility.calcHeight(pressedPoint, releasedPoint);

            for(int x = topLeftCorner.x; x <= topLeftCorner.x + width; x++){
                for(int y = topLeftCorner.y; y <= topLeftCorner.y + height; y++){

                    //add shapes to selectedShapesList
                    ArrayList<ShapeCommand> tempUnselectedShapes = new ArrayList<ShapeCommand>();
                    for (ShapeCommand myShape : unselectedShapes){
                        if(myShape.didCollideWithMe(x,y)){
                            boolean shapeAlreadyInSSL = false;
                            for(ShapeCommand testShape : selectedShapesList){
                                if(testShape.SCIsEqual(myShape)) shapeAlreadyInSSL = true;
                            }
                            if(!shapeAlreadyInSSL) {
                                selectedShapesList.add(myShape);
                                //proxy to draw dashes around selectedShape
                                //myShape.debugGotSelected(); //function only implemented in proxy
                                ShapeCommandProxy mySCP = new ShapeCommandProxy(myShape);
                                mySCP.drawMe(); //WARNING CAUSES SHAPE TO BE DRAWN A SECOND TIME ON TOP OF ITSELF
                            }
                        }

                        else{
                            tempUnselectedShapes.add(myShape);
                        }
                    }
                    unselectedShapes = (ArrayList<ShapeCommand>)tempUnselectedShapes.clone();


                    //add groups to selectedShapesList
                    ArrayList<GroupCommand> tempUnselectedGroups = new ArrayList<GroupCommand>();
                    for(GroupCommand myGC : unselectedGroups){
                        if(myGC.didCollideWithMe(x,y)){

                            for(ShapeCommand groupMemberShape : myGC.groupMemberList) {
                                boolean GMSAlreadyInSSL = false; //groupMemberShape is already in selectedShapesList
                                System.out.println("131 SSL Size " + selectedShapesList.size());
                                for(ShapeCommand testShape : selectedShapesList){
                                    System.out.println("testing GMS in SSL ");
                                    if(testShape.SCIsEqual(groupMemberShape)) GMSAlreadyInSSL = true;
                                }
                                if (!GMSAlreadyInSSL) {
                                    selectedShapesList.add(groupMemberShape);
                                }
                                else{
                                    System.out.println("GroupMemberShape already in SSL. Was Not ADDED!!!");
                                }
                            }
                            myGC.drawMe();
                        }
                        else{
                            tempUnselectedGroups.add(myGC);
                        }
                    }
                    unselectedGroups = (ArrayList<GroupCommand>)tempUnselectedGroups.clone();


                }
            }
            System.out.println("selectedShapesList.size" + selectedShapesList.size());
        }
    }

    public void handleMouseModeMove(Point pressedPoint, Point releasedPoint){
        if(applicationState.getActiveMouseMode() == MouseMode.MOVE){
            if(selectedShapesList.size() != 0) {
                MoveCommand myMC = new MoveCommand(this, paintCanvas, pressedPoint, releasedPoint, (ArrayList<ShapeCommand>)selectedShapesList.clone(),
                        (ArrayList<GroupCommand>)listOfGroups.clone());
                myMC.run();
                //selectedShapesList = (ArrayList<ShapeCommand>)myMC.JPCNewSelectedShapes.clone();
                //listOfGroups = (ArrayList<GroupCommand>)myMC.JPCNewListOfGroups.clone();

                resetCanvas();
                redraw();

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
        if (clipboard.size() > 0) {
            //CREATED PASTE COMMAND
            PasteCommand myPC = new PasteCommand(this, paintCanvas, clipboard, (ArrayList<ShapeCommand>)drawList.clone());
            //ADD TO COMMAND HISTORY
            myPC.run();
        }
    }

    private void DeleteButtonHandler() {
        if (selectedShapesList.size() > 0) {
            DeleteCommand myDC = new DeleteCommand(this, paintCanvas, selectedShapesList, (ArrayList<ShapeCommand>)drawList.clone(),
                    (ArrayList<GroupCommand>)listOfGroups.clone());
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

    private void GroupButtonHandler(){
        if(selectedShapesList.size() > 0) {
            resetCanvas();
            GroupCommand myGC = GroupFactory.getNewGroupCommand((ArrayList<ShapeCommand>) selectedShapesList.clone(), paintCanvas.getGraphics2D());
            myGC.run(); //Adds GC to CommandHistory
            redraw();
        }
    }

    private void UngroupButtonHandler(){
        if(selectedShapesList.size() > 0 && listOfGroups.size() > 0) {
            //1) which groups are part of the ssl?
            //2) remove these groups from the listofgroups
            resetCanvas();
            //derive listOfSelectedGroup
            ArrayList<GroupCommand> selectedGroups = new ArrayList<GroupCommand>();
            for(GroupCommand LOGgroup : listOfGroups){
                if(LOGgroup.containsAtLeastOneShape(selectedShapesList)){
                    selectedGroups.add(LOGgroup);
                }
            }
            UngroupCommand myUC = GroupFactory.getNewUngroupCommand(selectedGroups);
            myUC.run();
            redraw();
        }
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

        //loop to draw outline of selectedshapes
        for(ShapeCommand selectedShape : selectedShapesList){
            ShapeCommandProxy mySCP = new ShapeCommandProxy(selectedShape);
            mySCP.drawMe(); //WARNING CAUSES SHAPE TO BE DRAWN A SECOND TIME ON TOP OF ITSELF
        }

        //redraw group outlines
        for(GroupCommand myGroup : listOfGroups){
            if(myGroup.containsAtLeastOneShape(selectedShapesList)){
                myGroup.drawMe();
            }
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

//SINGLETON!!!
//--2 instance of shapefactory--
//ShapeFactory myShapeFactory = new ShapeFactory();
//ShapeFactory myShapeFactory2 = new ShapeFactory();
//--1 instance of shapefactory--
//ShapeCommand myDRC = ShapeFactory.instance.getDrawRectangleCommand
////--0 instance of shapefactory--
