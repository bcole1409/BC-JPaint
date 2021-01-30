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
    public ShapeColor currentColor;
    public ShapeShadingType sShadingType;

    public ShapeCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                Point p2, ShapeColor myCurrentColor, ShapeShadingType myShadingType) {
        this.drawList = myDrawList;
        this.myGraphics2D = base.getGraphics2D();
        this.p1 = p1;
        this.p2 = p2;
        currentColor = myCurrentColor;
        sShadingType = myShadingType;
    }
    public void drawMe() {

    }

    //public int getNumSides(){
       //return numSides;
    //}
}
