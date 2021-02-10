package model;

import controller.DrawEllipseCommand;
import controller.DrawRectangleCommand;
import controller.DrawTriangleCommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;
import java.awt.*;
import java.util.ArrayList;

//Parent Class to all DrawShapeCommands
//This means that shape command has all of the variables and functions that all shape commands share in common
public abstract class ShapeCommand implements IUndoable,IShape {
    public ArrayList<IShape> drawList;
    public Graphics2D myGraphics2D;
    public Point p1,p2;
    public ShapeColor primaryColor;
    public ShapeColor secondaryColor;
    public ShapeShadingType sShadingType;

    public ShapeCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                Point p2, ShapeColor myPrimaryColor, ShapeColor mySecondaryColor, ShapeShadingType myShadingType) {
        this.drawList = myDrawList;
        this.myGraphics2D = base.getGraphics2D();
        this.p1 = p1; //pressedPoint
        this.p2 = p2; //releasedPoint
        primaryColor = myPrimaryColor;
        secondaryColor = mySecondaryColor;
        sShadingType = myShadingType;
    }

    @Override
    public void redo() {
        //Add yourself to the drawlist
        drawList.add(this);
    }

    @Override
    public void undo() {
        //REMOVE LAST SHAPE
        int last = drawList.size();
        if(last != 0){
            drawList.remove(last-1);
        }
    }

    public boolean didCollideWithMe(int x, int y){
        int minX, minY, maxX, maxY;
        minX = BoundsUtility.calcTopLeftCorner(p1, p2).x;
        minY = BoundsUtility.calcTopLeftCorner(p1, p2).y;
        maxX = minX + BoundsUtility.calcWidth(p1,p2);
        maxY = minY + BoundsUtility.calcHeight(p1,p2);

        if(x <= maxX && x >= minX){
            if(y <= maxY && y >= minY){
                //System.out.println("XY Collided" + x + " " + y);
                return true;
            }
        }
        //System.out.println("XY Missed" + x + " " + y);
        return false;
    }

    public void setActiveColor(Graphics2D myGraphics2d, ShapeColor myColor) {
        switch (myColor) {
            case BLACK:
                myGraphics2d.setColor(Color.BLACK);
                break;
            case BLUE:
                myGraphics2d.setColor(Color.BLUE);
                break;
            case CYAN:
                myGraphics2d.setColor(Color.CYAN);
                break;
            case DARK_GRAY:
                myGraphics2d.setColor(Color.DARK_GRAY);
                break;
            case GRAY:
                myGraphics2d.setColor(Color.GRAY);
                break;
            case GREEN:
                myGraphics2d.setColor(Color.GREEN);
                break;
            case LIGHT_GRAY:
                myGraphics2d.setColor(Color.LIGHT_GRAY);
                break;
            case MAGENTA:
                myGraphics2d.setColor(Color.MAGENTA);
                break;
            case ORANGE:
                myGraphics2d.setColor(Color.ORANGE);
                break;
            case PINK:
                myGraphics2d.setColor(Color.PINK);
                break;
            case RED:
                myGraphics2d.setColor(Color.RED);
                break;
            case WHITE:
                myGraphics2d.setColor(Color.WHITE);
                break;
            case YELLOW:
                myGraphics2d.setColor(Color.YELLOW);
                break;
        }
    }

    public boolean SCIsEqual(ShapeCommand otherSC){
        boolean areSameShape = false;

        if (this instanceof DrawRectangleCommand && otherSC instanceof DrawRectangleCommand) {
           System.out.println("SC is equal its a Rectangle");
           areSameShape = true;
        }
        if (this instanceof DrawTriangleCommand && otherSC instanceof DrawTriangleCommand) {
            System.out.println("SC is equal its a Triangle");
            areSameShape = true;
        }

        if (this instanceof DrawEllipseCommand && otherSC instanceof DrawEllipseCommand) {
            System.out.println("SC is equal its a Ellipse");
            areSameShape = true;
        }

        boolean sameTopLefts = false;
        Point myTopLeft = BoundsUtility.calcTopLeftCorner(p1,p2);
        Point otherTopLeft = BoundsUtility.calcTopLeftCorner(otherSC.p1,otherSC.p2);
        if(myTopLeft.x == otherTopLeft.x && myTopLeft.y == otherTopLeft.y){
            sameTopLefts = true;
        }

        boolean sameBottomRights = false;
        Point myBottomRight = BoundsUtility.calcBottomRightCorner(p1,p2);
        Point otherBottomRight= BoundsUtility.calcBottomRightCorner(otherSC.p1,otherSC.p2);
        if(myBottomRight.x == otherBottomRight.x && myBottomRight.y == otherBottomRight.y){
            sameBottomRights = true;
        }

        boolean sameColorsAndShading = false;
        if(primaryColor == otherSC.primaryColor && secondaryColor == otherSC.secondaryColor && sShadingType == otherSC.sShadingType){
            sameColorsAndShading = true;
        }

        if(areSameShape && sameTopLefts && sameBottomRights && sameColorsAndShading){ //Largely Untested
           return true;
       }

        else{
            return false;
        }
    }

    public void drawMe() {
    }

    //TEST
    public void debugGotSelected(){
        Point topLeft = BoundsUtility.calcTopLeftCorner(p1,p2);
        myGraphics2D.fillOval(topLeft.x - 10, topLeft.y - 10, 20, 20); //X,Y IS THE TOP LEFT CORNER
    }
}
