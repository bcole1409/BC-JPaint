package model;

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
    }

    public boolean didCollideWithMe(int x, int y){
        int minX, minY, maxX, maxY;
        minX = calcTopLeftCorner(p1, p2).x;
        minY = calcTopLeftCorner(p1, p2).y;
        maxX = minX + calcWidth(p1,p2);
        maxY = minY + calcHeight(p1,p2);

        if(x <= maxX && x >= minX){
            if(y <= maxY && y >= minY){
                return true;
            }
        }
        return false;
    }

    public int calcWidth(Point p1, Point p2){
        int width = Math.abs(p2.x-p1.x);
        return width;
    }

    public int calcHeight(Point p1, Point p2){
        int height = Math.abs(p2.y-p1.y);
        return height;
    }

    //p1 p2 should be pressed and released points
    public Point calcTopLeftCorner(Point p1, Point p2){
        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        Point topLeft = new Point(x,y);
        return topLeft;
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

    public void drawMe() {
    }
}
