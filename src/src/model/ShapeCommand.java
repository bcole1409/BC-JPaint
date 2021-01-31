package model;

import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.util.ArrayList;

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
        this.p1 = p1;
        this.p2 = p2;
        primaryColor = myPrimaryColor;
        secondaryColor = mySecondaryColor;
        sShadingType = myShadingType;
    }

    public void drawMe() {
    }

    @Override
    public void redo() {
        //Add yourself to the drawlist
        drawList.add(this);
    }

    @Override
    public void undo() {
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
}
